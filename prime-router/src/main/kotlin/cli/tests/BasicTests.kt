package gov.cdc.prime.router.cli.tests

import com.azure.storage.blob.models.BlobStorageException
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.google.common.base.CharMatcher
import gov.cdc.prime.router.CovidSender
import gov.cdc.prime.router.MimeFormat
import gov.cdc.prime.router.Options
import gov.cdc.prime.router.REPORT_MAX_ITEM_COLUMNS
import gov.cdc.prime.router.Receiver
import gov.cdc.prime.router.ReportId
import gov.cdc.prime.router.UniversalPipelineSender
import gov.cdc.prime.router.azure.BlobAccess
import gov.cdc.prime.router.azure.HttpUtilities
import gov.cdc.prime.router.azure.WorkflowEngine
import gov.cdc.prime.router.azure.db.enums.TaskAction
import gov.cdc.prime.router.cli.FileUtilities
import gov.cdc.prime.router.cli.OktaCommand
import gov.cdc.prime.router.common.Environment
import gov.cdc.prime.router.common.HttpClientUtils
import gov.cdc.prime.router.common.JacksonMapperUtilities.jacksonObjectMapper
import gov.cdc.prime.router.common.SystemExitCodes
import gov.cdc.prime.router.history.DetailedSubmissionHistory
import kotlinx.coroutines.delay
import org.jooq.exception.DataAccessException
import org.json.JSONObject
import java.io.File
import java.lang.Thread.sleep
import java.net.HttpURLConnection
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Locale
import kotlin.math.abs
import kotlin.system.exitProcess
import kotlin.system.measureTimeMillis

class Ping : CoolTest() {
    override val name = "ping"
    override val description = "CheckConnections: Is the reports endpoint alive and listening?"
    override val status = TestStatus.SMOKE

    override suspend fun run(environment: Environment, options: CoolTestOptions): Boolean {
        ugly("Starting ping Test: run CheckConnections of ${environment.url}")
        val (responseCode, json) = HttpUtilities.postReportBytes(
            environment,
            "x".toByteArray(),
            simpleRepSender,
            options.key,
            Options.CheckConnections,
            payloadName = "$name ${status.description}",
        )
        echo("Response to POST: $responseCode")
        echo("Response message: $json")
        if (responseCode != HttpURLConnection.HTTP_OK) {
            bad("Ping/CheckConnections Test FAILED:  response code $responseCode")
            outputAllMsgs()
            exitProcess(SystemExitCodes.FAILURE.exitCode) // other tests won't work.
        }
        try {
            val tree = jacksonObjectMapper.readTree(json)
            if (tree["errorCount"].intValue() != 0 || tree["warningCount"].intValue() != 0) {
                return bad("***Ping/CheckConnections Test FAILED***")
            } else {
                return good("Test passed: Ping/CheckConnections")
            }
        } catch (e: NullPointerException) {
            return bad("***Ping/CheckConnections FAILED***: Unable to properly parse response json")
        }
    }
}

/**
 * An End-to-End test for the Universal Pipeline. This test posts a report and verifies that the file was sent to
 * the expected receiver(s), then verifies the contents of the file match the expected contents.
 * This test runs through the following scenarios:
 * - A report that is submitted as HL7 and is sent as HL7 and FHIR (Topic: FULL-ELR).
 * - A report that is submitted as HL7 and is sent as HL7 and FHIR (Topic: MARS-OTC-ELR).
 *     - This report contains multiple items, one of which is invalid and filtered out during validation
 * - A report that is submitted as HL7 and is sent as HL7 but has isSendOriginal set to true (Topic: ELR-ELIMS).
 * - A report that is submitted as FHIR and is sent as FHIR (Topic: FULL-ELR).
 * - A report that is submitted as FHIR and is sent as FHIR (Topic: MARS-OTC-ELR).
 */
class End2EndUniversalPipeline : CoolTest() {
    override val name = "end2end_up"
    override val description = "e2e tests that verify reports are correctly uploaded, routed, and received"
    override val status = TestStatus.SMOKE

    override suspend fun run(environment: Environment, options: CoolTestOptions): Boolean {
        val testData = testDataForUniversalPipeline()
        var passed = true

        ugly("Starting $name Test")

        // Posting the reports first. Batching the sending of all reports before pausing in the second step
        testData.forEach {
            ugly("Sending ${it.baseFile.extension} data from ${it.sender.fullName} to the UP")
            it.reportId = postPreTestReports(it.baseFile, environment, options, it.sender)
        }

        // Wait until reports are marked as Delivered, then run validations on results
        testData.forEach {
            echo("\nStarting validation for scenario: ${it.name}")

            it.historyResponse = pauseForBatchProcess(environment, it.reportId)
            echo(it.historyResponse)

            if (it.historyResponse.isEmpty()) {
                passed = bad("***$name test FAILED***: One or more reports failed batch step.")
                return@forEach
            }

            ugly("Examining sent reports for the posted report ID ${it.reportId}")
            passed = passed and end2EndUP(it.historyResponse, it.expectedResults)
        }

        return passed
    }

    /**
     * Posts a file to the UP. Verifies the response from the server and returns the extracted report ID from it.
     */
    private fun postPreTestReports(
        file: File,
        environment: Environment,
        options: CoolTestOptions,
        sender: UniversalPipelineSender,
    ): ReportId? {
        val (responseCode, json) =
            HttpUtilities.postReportFile(
                environment,
                file,
                sender,
                true,
                options.key,
                payloadName = "$name ${status.description}",
            )
        if (responseCode != HttpURLConnection.HTTP_CREATED) {
            bad("***$name test FAILED***: Response code $responseCode")
        } else {
            good("Posting of report succeeded with response code $responseCode")
        }

        if (!examinePostResponse(json, false)) {
            echo(json)
            bad("***$name test FAILED***: Error in post response")
        }

        return getReportIdFromResponse(json)
    }

    /**
     * Calls the report history endpoint periodically, waiting for the overall status to be set to Delivered.
     * Times out after 150 seconds, mostly for breathing room in staging.
     */
    private suspend fun pauseForBatchProcess(environment: Environment, reportId: ReportId?): String {
        val maxPollSecs = 150
        val pollSleepSecs = 5
        var timeElapsedSecs = 0
        var overallStatus = ""
        var finalResponse = ""

        echo("Polling for history endpoint for report ID: $reportId. (Max poll time $maxPollSecs seconds)")
        while (timeElapsedSecs <= maxPollSecs) {
            delay(pollSleepSecs.toLong() * 1000)
            timeElapsedSecs += pollSleepSecs

            val headers = mutableListOf<Pair<String, String>>()
            val oktaToken = OktaCommand.fetchAccessToken(OktaCommand.OktaApp.DH_STAGE)
            headers.add("Authorization" to "Bearer $oktaToken")
            val getUrl = "${environment.url}/api/waters/report/$reportId/history"

            val (_, response) = HttpClientUtils.getWithStringResponse(
                url = getUrl,
                accessToken = oktaToken,
                timeout = 45000
            )

            overallStatus = JSONObject(response).getString("overallStatus")

            if (DetailedSubmissionHistory.Status.DELIVERED.toString() == overallStatus) {
                good("Report $reportId status was $overallStatus after $timeElapsedSecs seconds")
                return response
            }

            finalResponse = response
        }

        bad(
            "***$name test FAILED***: Batch step polling for report $reportId failed. " +
                "After $timeElapsedSecs seconds the report status is $overallStatus."
        )
        return finalResponse
    }

    /**
     * Runs validations after a report has been submitted to the UP and marked as Delivered.
     * For all expected receivers this will validate:
     * - An external filename matching that receiver is present in the history endpoint response
     * - Download the file from BlobStorage
     * - Compare the expected data with the actual data returned from BlobStorage
     */
    private fun end2EndUP(
        historyResponse: String,
        expectedResults: List<Pair<Receiver, File>>,
    ): Boolean {
        var passed = true
        val blobConnectionString = Environment.get().storageEnvVar
        val blobContainerMetadata: BlobAccess.BlobContainerMetadata =
            BlobAccess.BlobContainerMetadata.build("reports", blobConnectionString)

        expectedResults.forEach { (expectedReceiver, expectedFile) ->
            // Retrieve external filename
            val actualFilename = findReportName(historyResponse, expectedReceiver)
            if (actualFilename.isEmpty()) {
                passed = bad(
                    "***$name test FAILED***: " +
                        "Did not find the sent report name for receiver ${expectedReceiver.name}"
                )
                return@forEach
            }

            // Grab the uploaded files out of blob store
            val actualURL = "${blobContainerMetadata.getBlobEndpoint()}/" +
                "none/${expectedReceiver.fullName}/$actualFilename"
            var actualByteArray: ByteArray

            try {
                actualByteArray = BlobAccess.downloadBlobAsByteArray(actualURL, blobContainerMetadata)
                good("File successfully uploaded to blob store as $actualFilename")
            } catch (e: BlobStorageException) {
                passed = bad("***$name test FAILED***: Failed to find $actualURL in blob storage: $e")
                return@forEach
            }

            // Compare the actual file content with the expected content
            val expectedFormat = MimeFormat.valueOfFromExt(expectedReceiver.translation.type)
            val thisRoundPassed = CompareData().compare(
                expectedFile.readBytes().inputStream(),
                actualByteArray.inputStream(),
                expectedFormat,
                null
            ).passed

            passed = passed and thisRoundPassed

            if (thisRoundPassed) {
                good("The contents of $actualFilename matches the expected data")
            } else {
                bad("***$name test FAILED***: The contents of $actualFilename did not match the expected contents")
            }
        }

        return passed
    }

    /**
     * Searches a submission history json response for the filename that matches the expected receiver.
     * Will fail if the expected json objects are not populated.
     * @param response must be a response from the submission history endpoint for json lookup
     * @param expectedReceiver receiver for which to find a matching destination service
     */
    private fun findReportName(response: String, expectedReceiver: Receiver): String {
        var filename = ""

        try {
            val destinations = JSONObject(response).getJSONArray("destinations")

            destinations.forEach { d ->
                val destination = JSONObject(d.toString())

                // Check that the destination service matches the expected receiver. Uses the first name listed.
                if (expectedReceiver.name == destination.getString("service").removeSurrounding("\"")) {
                    val reportId = destination.getJSONArray("sentReports").getJSONObject(0).getString("reportId")
                    filename = "$reportId.${expectedReceiver.translation.type.lowercase()}"
                    good("The report was received by ${expectedReceiver.name}")
                }
            }
        } catch (e: org.json.JSONException) {
            bad("***$name test FAILED***: History response is missing an element for the sent report: $e")
        }

        return filename
    }

    /**
     * NOTE: This method is not currently being used. It is being kept for intended later usage. This method allows the
     * e2e test to generate the expected final sent report from the original file that is posted to the UP.
     * The e2e test is now using expected data files saved in the repo. Using saved files allows for another layer of
     * test coverage on the classes used below. Using this method would allow another layer of test coverage on
     * the translation schemas.
     *
     * Performs the expected transforms that would happen to a file moving through the UP. If the topic is ELI`MS this
     * process will be skipped. Otherwise, the file will be converted to FHIR if necessary. Sender enrichments will be
     * run. Then translated based on the expected receiver format.
     * Caveats:
     *  - This does not perform all transformations from the UP. Notably, batch headers/footers are absent.
     *  - The setting data for receivers and senders is pulling from organizations.yml so that must be kept in sync
     *      with the settings in both the local db and in staging.
     *
     * Example Usage:
     * // Mimic the UP transformations to get the expected file contents
     *    val expectedByteArray = performFileTransforms(
     *        originalFile.inputStream(),
     *        expectedReceiver,
     *        sender,
     *        blobContainerMetadata
     *    )
     *
     * @param originalFile the file to transform
     * @param expectedReceiver determines resulting data format
     * @param sender used for the sender enrichment schema
     * @param blobMetadata necessary for the FhirTransformer
     * @return the transformed ByteArray in the proper data format of HL7 or FHIR
     */
//    private fun performFileTransforms(
//        originalFile: FileInputStream,
//        expectedReceiver: Receiver,
//        sender: Sender,
//        blobMetadata: BlobAccess.BlobContainerMetadata,
//    ): ByteArray {
//        // If topic is set to send original then we do not need to perform any transformations
//        if (expectedReceiver.topic.isSendOriginal) {
//            return originalFile.readBytes()
//        }
//
//        // Create FHIR Bundle, translate to FHIR if originally in HL7
//        var fhirBundle = if (sender.format.toString() == "HL7") {
//            val hl7messages = HL7Reader(ActionLogger()).getMessages(originalFile.bufferedReader().readText())
//            hl7messages.map { message -> HL7toFhirTranslator().translate(message) }.first()
//        } else {
//            FhirTranscoder.getBundles(originalFile.bufferedReader().readText(), ActionLogger()).first()
//        }
//
//        // Run Sender Enrichment if it exists
//        if (sender.schemaName.isNotEmpty()) {
//            fhirBundle = FhirTransformer(sender.schemaName).process(fhirBundle)
//        }
//        val encodedBundle = FhirTranscoder.encode(fhirBundle).byteInputStream()
//
//        // Translate from FHIR to HL7 if required by receiver
//        val translatedFile = if (expectedReceiver.translation.type == "HL7") {
//            fhirBundle = FhirTranscoder.decode(encodedBundle.bufferedReader().readText())
//            FhirToHl7Converter(expectedReceiver.translation.schemaName, blobMetadata)
//                .process(fhirBundle).encodePreserveEncodingChars().byteInputStream().readBytes()
//        } else {
//            FhirTranscoder.encode(fhirBundle).encodeToByteArray()
//        }
//
//        return translatedFile
//    }
}

class End2End : CoolTest() {
    override val name = "end2end"
    override val description = "Create Fake data, submit, wait, confirm sent via database lineage data"
    override val status = TestStatus.SMOKE

    override suspend fun run(environment: Environment, options: CoolTestOptions): Boolean {
        initListOfGoodReceiversAndCountiesForTopicPipeline()
        ugly("Starting $name Test: send ${simpleRepSender.fullName} data to $allGoodCounties")

        // run both sync and async end2end test
        return forceSync(environment, options) && forceAsync(environment, options)
    }

    /**
     * Forces synchronous end2end test
     */
    private suspend fun forceSync(environment: Environment, options: CoolTestOptions): Boolean {
        ugly("Running end2end synchronously -- with no query param")
        var passed = true
        val fakeItemCount = allGoodReceivers.size * options.items
        val file = FileUtilities.createFakeCovidFile(
            metadata,
            settings,
            simpleRepSender.schemaName,
            fakeItemCount,
            receivingStates,
            allGoodCounties,
            options.dir,
        )

        echo("Created datafile $file")
        // Now send it to ReportStream.
        val (responseCode, json) =
            // force sync processing
            HttpUtilities.postReportFile(
                environment, file, simpleRepSender, false, options.key,
                payloadName = "$name ${status.description} with async = false",
            )
        if (responseCode != HttpURLConnection.HTTP_CREATED) {
            bad("***end2end Test FAILED***:  response code $responseCode")
            passed = false
        } else {
            good("Posting of sync report succeeded with response code $responseCode")
        }
        echo(json)
        passed = passed and examinePostResponse(json, true)
        val reportId = getReportIdFromResponse(json)
        if (reportId != null) {
            // check for covid result metadata - the examinePostResponse function above has already
            //  verified that the topic is covid-19. This will need to be updated once we are supporting
            //  non-covid record types
            passed = passed and queryForCovidResults(reportId)
            if (!passed) {
                bad("***sync end2end FAILED***: Covid metadata record not found")
            }

            // check that lineages were generated properly
            passed = passed and pollForLineageResults(
                reportId,
                allGoodReceivers,
                fakeItemCount,
                asyncProcessMode = false
            )
        }

        return passed
    }

    /**
     * Forces asynchronous end2end test
     */
    private suspend fun forceAsync(environment: Environment, options: CoolTestOptions): Boolean {
        ugly("Running end2end asynchronously -- with query param")
        var passed = true
        val fakeItemCount = allGoodReceivers.size * options.items
        val file = FileUtilities.createFakeCovidFile(
            metadata,
            settings,
            simpleRepSender.schemaName,
            fakeItemCount,
            receivingStates,
            allGoodCounties,
            options.dir,
        )

        echo("Created datafile $file")
        // Now send it to ReportStream.
        val (responseCode, json) =
            // force async processing
            HttpUtilities.postReportFile(
                environment, file, simpleRepSender, true, options.key,
                payloadName = "$name ${status.description} with async = true",
            )
        if (responseCode != HttpURLConnection.HTTP_CREATED) {
            bad("***end2end Test FAILED***:  response code $responseCode")
            passed = false
        } else {
            good("Posting of async report succeeded with response code $responseCode")
        }
        echo(json)
        passed = passed and examinePostResponse(json, false)
        if (!passed) {
            bad("***async end2end FAILED***: Error in post response")
        }
        // gets back 'received' reportId
        val reportId = getReportIdFromResponse(json)

        if (reportId != null) {
            // gets back the id of the internal report
            val internalReportId = getSingleChildReportId(reportId)
                ?: return bad("***end2end FAILED***: Single Child Report ID null")

            val processResults = pollForStepResult(internalReportId, TaskAction.process)
            // verify each result is valid
            for (result in processResults.values) {
                passed = passed && examineProcessResponse(result)
            }
            if (!passed) {
                bad("***async end2end FAILED***: Process result invalid")
            }

            // check for covid result metadata - the examinePostResponse function above has already
            //  verified that the topic is covid-19. This will need to be updated once we are supporting
            //  non-covid record types
            passed = passed and queryForCovidResults(reportId)
            if (!passed) {
                bad("***async end2end FAILED***: Covid metadata record not found")
            }

            // check that lineages were generated properly
            passed = passed and pollForLineageResults(
                reportId, allGoodReceivers,
                fakeItemCount,
                asyncProcessMode = true
            )
        }

        return passed
    }
}

class Merge : CoolTest() {
    override val name = "merge"
    override val description = "Submit multiple files, wait, confirm via db that merge occurred"
    override val status = TestStatus.SMOKE

    /**
     * Examine the results stored in the database and compare the number of reports to the expected number.
     * @param reportIds the list of report IDs that were sent to the API
     * @param receivers the list of receivers the reports were sent to
     * @param itemsPerReport the number of reports in each file sent
     * @param reportCount the total number of reports sent
     * @return true if the number of reports matches the expected, false otherwise
     */
    fun queryForMergeResults(
        reportIds: List<ReportId>,
        receivers: List<Receiver>,
        itemsPerReport: Int,
    ): List<Pair<Boolean, String>> {
        var queryResults = mutableListOf<Pair<Boolean, String>>()
        db = WorkflowEngine().db
        db.transact { txn ->
            val expected = (itemsPerReport * reportIds.size) / receivers.size
            receivers.forEach { receiver ->
                val actionsList = mutableListOf<TaskAction>()
                if (receiver.timing != null) actionsList.add(TaskAction.batch)
                if (receiver.transport != null) actionsList.add(TaskAction.send)
                actionsList.forEach { action ->
                    var count = 0
                    reportIds.forEach { reportId ->
                        count += itemLineageCountQuery(
                            txn,
                            reportId,
                            receiver.name,
                            action = action,
                            isUniversalPipeline = false
                        ) ?: 0
                    }
                    if (expected != count) {
                        queryResults += Pair(
                            false,
                            "*** TEST FAILED*** for ${receiver.fullName} action $action: " +
                                " Expecting sum(itemCount)=$expected but got $count"
                        )
                    } else {
                        queryResults += Pair(
                            true,
                            "Test passed: for ${receiver.fullName} action $action: " +
                                " Expecting sum(itemCount)=$expected and got $count"
                        )
                    }
                }
            }
        }
        return queryResults
    }

    // todo  arglebargle - this is mostly repeat of pollForLineageResults, but ever so slightly different.
    suspend fun pollForMergeResults(
        reportIds: List<ReportId>,
        receivers: List<Receiver>,
        itemsPerReport: Int,
        silent: Boolean = false,
        maxPollSecs: Int = 180,
        pollSleepSecs: Int = 20,
    ): Boolean {
        var timeElapsedSecs = 0
        var queryResults = listOf<Pair<Boolean, String>>()
        echo("Polling for ReportStream results.  (Max poll time $maxPollSecs seconds)")
        // Print out some nice dots to show we are waiting only when the output goes directly to the console.
        while (timeElapsedSecs <= maxPollSecs) {
            if (outputToConsole) {
                for (i in 1..pollSleepSecs) {
                    delay(1000)
                    print(".")
                }
                echo()
            } else {
                delay(pollSleepSecs.toLong() * 1000)
            }
            timeElapsedSecs += pollSleepSecs
            queryResults = queryForMergeResults(reportIds, receivers, itemsPerReport)
            if (!queryResults.map { it.first }.contains(false)) break // everything passed!
        }
        if (!silent) {
            queryResults.forEach {
                if (it.first) {
                    good(it.second)
                } else {
                    bad(it.second)
                }
            }
        }
        return !queryResults.map { it.first }.contains(false) // no falses == it passed!
    }

    override suspend fun run(environment: Environment, options: CoolTestOptions): Boolean {
        var passed: Boolean
        val mergingReceivers = listOf<Receiver>(csvReceiver, hl7BatchReceiver)
        val mergingCounties = mergingReceivers.map { it.name }.joinToString(",")
        val fakeItemCount = mergingReceivers.size * options.items
        ugly("Starting merge test:  Merge ${options.submits} reports, each of which sends to $mergingCounties")
        val file = FileUtilities.createFakeCovidFile(
            metadata,
            settings,
            simpleRepSender.schemaName,
            fakeItemCount,
            receivingStates,
            mergingCounties,
            options.dir,
        )
        echo("Created datafile $file")
        val actualTimeElapsedMillis = measureTimeMillis {
            // Now send it to ReportStream over and over
            val reportIds = (1..options.submits).map {
                val (responseCode, json) =
                    HttpUtilities.postReportFile(
                        environment,
                        file,
                        simpleRepSender,
                        options.asyncProcessMode,
                        options.key,
                        payloadName = "$name ${status.description}",
                    )
                echo("Response to POST: $responseCode")
                if (responseCode != HttpURLConnection.HTTP_CREATED) {
                    return bad("***Merge Test FAILED***:  response code $responseCode")
                }
                val reportId = getReportIdFromResponse(json)
                    ?: return bad("***$name Test FAILED***: A report ID came back as null")
                echo("Id of submitted report: $reportId")
                reportId
            }
            passed = pollForMergeResults(reportIds, mergingReceivers, fakeItemCount)
        }
        echo("$name test took ${actualTimeElapsedMillis / 1000} seconds.")
        return passed
    }
}

/**
 * Test using the NULL transport.
 */
class Hl7Null : CoolTest() {
    override val name = "hl7null"
    override val description = "The NULL transport does db work, but no transport.  Uses HL7 format"
    override val status = TestStatus.SMOKE

    override suspend fun run(environment: Environment, options: CoolTestOptions): Boolean {
        val fakeItemCount = 100
        ugly("Starting hl7null Test: test of many threads all doing database interactions, but no sends. ")
        val file = FileUtilities.createFakeCovidFile(
            metadata,
            settings,
            simpleRepSender.schemaName,
            fakeItemCount,
            receivingStates,
            "HL7_NULL",
            options.dir,
        )
        echo("Created datafile $file")
        // Now send it to ReportStream.   Make numResends > 1 to create merges.
        val numResends = 1
        val reportIds = (1..numResends).map {
            val (responseCode, json) =
                HttpUtilities.postReportFile(
                    environment, file, simpleRepSender, options.asyncProcessMode, options.key,
                    payloadName = "$name ${status.description}",
                )
            echo("Response to POST: $responseCode")
            echo(json)
            if (responseCode != HttpURLConnection.HTTP_CREATED) {
                return bad("***hl7null Test FAILED***:  response code $responseCode")
            }
            val reportId = getReportIdFromResponse(json)
                ?: return bad("***$name Test FAILED***: A report ID came back as null")
            echo("Id of submitted report: $reportId")
            reportId
        }
        return pollForLineageResults(
            reportId = reportIds[0],
            receivers = listOf(hl7NullReceiver),
            totalItems = fakeItemCount,
            asyncProcessMode = options.asyncProcessMode
        )
    }
}

class TooManyCols : CoolTest() {
    override val name = "toomanycols"
    override val description = "Submit a file with more than $REPORT_MAX_ITEM_COLUMNS columns, which should error"
    override val status = TestStatus.SMOKE

    override suspend fun run(environment: Environment, options: CoolTestOptions): Boolean {
        ugly("Starting toomanycols Test: submitting a file with too many columns.")
        val file = File("./src/test/csv_test_files/input/too-many-columns.csv")
        if (!file.exists()) {
            error("Unable to find file ${file.absolutePath} to do toomanycols test")
        }
        val (responseCode, json) =
            HttpUtilities.postReportFile(
                environment, file, simpleRepSender, options.asyncProcessMode, options.key,
                payloadName = "$name ${status.description}",
            )
        echo("Response to POST: $responseCode")
        echo(json)
        try {
            val tree = jacksonObjectMapper.readTree(json)
            val firstError = (tree["errors"][0]) as ObjectNode
            if (firstError["message"].textValue().contains("columns")) {
                return good("toomanycols Test passed.")
            } else {
                return bad("***toomanycols Test FAILED***:  did not find the error.")
            }
        } catch (e: Exception) {
            return bad("***toomanycols Test FAILED***: Unable to parse json response")
        }
    }
}

class BadCsv : CoolTest() {
    override val name = "badcsv"
    override val description = "Submit badly formatted csv files - should get errors"
    override val status = TestStatus.SMOKE
    override suspend fun run(environment: Environment, options: CoolTestOptions): Boolean {
        val filenames = listOf("not-a-csv-file.csv", /* "column-headers-only.csv", */ "completely-empty-file.csv")
        var passed = true
        filenames.forEachIndexed { i, filename ->
            ugly("Starting badcsv file Test $i: submitting $filename")
            val file = File("./src/test/csv_test_files/input/$filename")
            if (!file.exists()) {
                error("Unable to find file ${file.absolutePath} to do badcsv test")
            }
            val (responseCode, json) = HttpUtilities.postReportFile(
                environment,
                file,
                simpleRepSender,
                options.asyncProcessMode,
                options.key,
                payloadName = "$name ${status.description}",
            )
            echo("Response to POST: $responseCode")
            if (responseCode >= 400) {
                good("Test of Bad CSV file $filename passed: Failure HttpStatus code was returned.")
            } else {
                bad("***badcsv Test $i of $filename FAILED: Expecting a failure HttpStatus. ***")
                passed = false
            }
            try {
                val tree = jacksonObjectMapper.readTree(json)
                if (tree["id"] == null || tree["id"].isNull) {
                    good("Test of Bad CSV file $filename passed: No UUID was returned.")
                } else {
                    bad("***badcsv Test $i of $filename FAILED: RS returned a valid UUID for a bad CSV. ***")
                    passed = false
                }
                if (tree["errorCount"].intValue() > 0) {
                    good("Test of Bad CSV file $filename passed: At least one error was returned.")
                } else {
                    bad("***badcsv Test $i of $filename FAILED: No error***")
                    passed = false
                }
            } catch (e: Exception) {
                passed = bad("***badcsv Test $i of $filename FAILED***: Unexpected json returned")
            }
        }
        return passed
    }
}

class Strac : CoolTest() {
    override val name = "strac"
    override val description = "Submit data in strac schema, send to all formats and variety of schemas"
    override val status = TestStatus.SMOKE

    override suspend fun run(environment: Environment, options: CoolTestOptions): Boolean {
        initListOfGoodReceiversAndCountiesForTopicPipeline()
        ugly("Starting bigly strac Test: sending Strac data to all of these receivers: $allGoodCounties!")
        var passed = true
        val fakeItemCount = allGoodReceivers.size * options.items
        val file = FileUtilities.createFakeCovidFile(
            metadata,
            settings,
            stracSender.schemaName,
            fakeItemCount,
            receivingStates,
            allGoodCounties,
            options.dir,
        )
        echo("Created datafile $file")
        // Now send it to ReportStream.
        val (responseCode, json) =
            HttpUtilities.postReportFile(
                environment,
                file,
                stracSender,
                options.asyncProcessMode,
                options.key,
                payloadName = "$name ${status.description}",
            )
        echo("Response to POST: $responseCode")
        echo(json)
        if (responseCode != HttpURLConnection.HTTP_CREATED) {
            return bad("**Strac Test FAILED***:  response code $responseCode")
        }
        try {
            val tree = jacksonObjectMapper.readTree(json)
            val reportId = getReportIdFromResponse(json)
                ?: return bad("***$name Test FAILED***: A report ID came back as null")
            echo("Id of submitted report: $reportId")
            val warningCount = tree["warningCount"].intValue()
            good("First part of strac Test passed: $warningCount warnings were returned.")

            return passed and pollForLineageResults(
                reportId = reportId,
                receivers = allGoodReceivers,
                totalItems = fakeItemCount,
                asyncProcessMode = options.asyncProcessMode
            )
        } catch (e: Exception) {
            return bad("***strac Test FAILED***: Unexpected json returned")
        }
    }
}

class Waters : CoolTest() {
    override val name = "waters"
    override val description = "Submit data in waters schema, send to BLOBSTORE only"
    override val status = TestStatus.SMOKE

    override suspend fun run(environment: Environment, options: CoolTestOptions): Boolean {
        ugly("Starting Waters: sending ${options.items} Waters items to ${blobstoreReceiver.name} receiver")
        val file = FileUtilities.createFakeCovidFile(
            metadata,
            settings,
            watersSender.schemaName,
            options.items,
            receivingStates,
            blobstoreReceiver.name,
            options.dir,
        )
        echo("Created datafile $file")
        val (responseCode, json) =
            HttpUtilities.postReportFile(
                environment,
                file,
                watersSender,
                options.asyncProcessMode,
                options.key,
                payloadName = "$name ${status.description}",
            )
        echo("Response to POST: $responseCode")
        if (!options.muted) echo(json)
        if (responseCode != HttpURLConnection.HTTP_CREATED) {
            return bad("***Waters Test FAILED***:  response code $responseCode")
        }
        val reportId = getReportIdFromResponse(json)
            ?: return bad("***$name Test FAILED***: A report ID came back as null")
        echo("Id of submitted report: $reportId")
        return pollForLineageResults(
            reportId = reportId,
            receivers = listOf(blobstoreReceiver),
            totalItems = options.items,
            asyncProcessMode = options.asyncProcessMode
        )
    }
}

class Garbage : CoolTest() {
    override val name = "garbage"
    override val description = "Garbage in - Nice error message out"
    override val status = TestStatus.FAILS // new quality checks now prevent any data from flowing to other checks

    override suspend fun run(environment: Environment, options: CoolTestOptions): Boolean {
        initListOfGoodReceiversAndCountiesForTopicPipeline()
        ugly("Starting $name Test: send ${emptySender.fullName} data to $allGoodCounties")
        var passed = true
        val fakeItemCount = allGoodReceivers.size * options.items
        val file = FileUtilities.createFakeCovidFile(
            metadata,
            settings,
            emptySender.schemaName,
            fakeItemCount,
            receivingStates,
            allGoodCounties,
            options.dir,
        )
        echo("Created datafile $file")
        // Now send it to ReportStream.
        val (responseCode, json) =
            HttpUtilities.postReportFile(
                environment,
                file,
                emptySender,
                options.asyncProcessMode,
                options.key,
                payloadName = "$name ${status.description}",
            )
        echo("Response to POST: $responseCode")
        echo(json)
        try {
            val tree = jacksonObjectMapper.readTree(json)
            val reportId = getReportIdFromResponse(json)
            echo("Id of submitted report: $reportId")
            val warningCount = tree["warningCount"].intValue()
            if (warningCount == allGoodReceivers.size) {
                good("garbage Test passed: $warningCount warnings were returned.")
            } else {
                passed =
                    bad("***garbage Test FAILED: Expecting ${allGoodReceivers.size} warnings but got $warningCount***")
            }
            val destinationCount = tree["destinationCount"].intValue()
            if (destinationCount == 0) {
                good("garbage Test passed: Items went to $destinationCount destinations.")
            } else {
                passed = bad("***garbage Test FAILED: Expecting 0 destinationCount but got $destinationCount ***")
            }
        } catch (e: Exception) {
            passed = bad("***garbage Test FAILED***: Unexpected json returned")
        }
        return passed
    }
}

class QualityFilter : CoolTest() {
    override val name = "qualityfilter"
    override val description = "Test the QualityFilter feature"
    override val status = TestStatus.SMOKE

    /**
     * In the returned [history] response, check the itemCount associated with [receiver] name in the list of
     * destinations against the [expectedCount].
     * @return true if the check succeeds
     */
    private fun checkJsonItemCountForReceiver(
        receiver: Receiver,
        expectedCount: Int,
        history: DetailedSubmissionHistory,
    ): Boolean {
        try {
            val reportId = history.reportId
            echo("Id of submitted report: $reportId")
            val destinations = history.destinations
            for (destination in destinations) {
                if (destination.service == receiver.name) {
                    return if (destination.itemCount == expectedCount) {
                        good("Test Passed: For ${receiver.name} expected $expectedCount and found $expectedCount")
                    } else {
                        bad(
                            "***Test FAILED***; For ${receiver.name} expected " +
                                "$expectedCount but got ${destination.itemCount}"
                        )
                    }
                }
            }
            if (expectedCount == 0) {
                return good("Test Passed: No data went to ${receiver.name} dest")
            } else {
                return bad("***Test FAILED***: No data went to ${receiver.name} dest")
            }
        } catch (e: Exception) {
            return bad("***$name Test FAILED***: Unexpected json returned for ${receiver.name}")
        }
    }

    /**
     * In the returned [json] response, check the itemCount associated with [receiver] name in the list of
     * destinations against the [expectedCount].
     * @return true if the check succeeds
     */
    private fun checkJsonItemCountForReceiver(receiver: Receiver, expectedCount: Int, json: String): Boolean {
        try {
            echo(json)
            val tree = jacksonObjectMapper.readTree(json)
            val reportId = ReportId.fromString(tree["reportId"].textValue())
            echo("Id of submitted report: $reportId")
            val destinations = tree["destinations"] as ArrayNode
            for (i in 0 until destinations.size()) {
                val dest = destinations[i] as ObjectNode
                if (dest["service"].textValue() == receiver.name) {
                    return if (dest["itemCount"].intValue() == expectedCount) {
                        good("Test Passed: For ${receiver.name} expected $expectedCount and found $expectedCount")
                    } else {
                        bad(
                            "***Test FAILED***; For ${receiver.name} expected " +
                                "$expectedCount but got ${dest["itemCount"].intValue()}"
                        )
                    }
                }
            }
            if (expectedCount == 0) {
                return good("Test Passed: No data went to ${receiver.name} dest")
            } else {
                return bad("***Test FAILED***: No data went to ${receiver.name} dest")
            }
        } catch (e: Exception) {
            return bad("***$name Test FAILED***: Unexpected json returned for ${receiver.name}")
        }
    }

    override suspend fun run(environment: Environment, options: CoolTestOptions): Boolean {
        ugly("Starting $name Test")
        // ALLOW ALL
        ugly("\nTest the allowAll QualityFilter")
        val fakeItemCount = 5
        val file = FileUtilities.createFakeCovidFile(
            metadata,
            settings,
            emptySender.schemaName,
            fakeItemCount,
            receivingStates,
            qualityAllReceiver.name, // Has the 'allowAll' quality filter
            options.dir,
        )
        echo("Created datafile $file")
        // Now send it to ReportStream.
        val (responseCode, json) =
            HttpUtilities.postReportFile(
                environment,
                file,
                emptySender,
                options.asyncProcessMode,
                options.key,
                payloadName = "$name ${status.description}"
            )
        echo("Response to POST: $responseCode")

        var passed = true
        var expectItemCount = fakeItemCount
        // if running in async mode, the initial response will not have destinations - the 'process' action result will
        if (options.asyncProcessMode) {
            val reportId = getReportIdFromResponse(json)
            if (reportId != null) {
                // gets back the id of the internal report
                val internalReportId = getSingleChildReportId(reportId)
                    ?: return bad("***qualityfilter FAILED***: Report id null")

                val processResults = pollForStepResult(internalReportId, TaskAction.process)
                // verify each result is valid
                for (result in processResults.values) {
                    passed = passed &&
                        examineProcessResponse(result) &&
                        checkJsonItemCountForReceiver(qualityAllReceiver, expectItemCount, result!!)
                }
            }
        } else {
            passed = passed && checkJsonItemCountForReceiver(qualityAllReceiver, expectItemCount, json)
        }

        // QUALITY_PASS
        ugly("\nTest a QualityFilter that allows some data through")
        expectItemCount = fakeItemCount - 2 // Removed 2 items
        val file2 = FileUtilities.createFakeCovidFile(
            metadata,
            settings,
            emptySender.schemaName,
            fakeItemCount,
            receivingStates,
            qualityGoodReceiver.name + ",removed", // 3 kept, 2 removed
            options.dir,
        )
        echo("Created datafile $file2")
        // Now send it to ReportStream.
        val (responseCode2, json2) =
            HttpUtilities.postReportFile(
                environment,
                file2,
                emptySender,
                options.asyncProcessMode,
                options.key,
                payloadName = "$name ${status.description}"
            )
        echo("Response to POST: $responseCode2")
        // if running in async mode, the initial response will not have destinations - the 'process' action result will
        if (options.asyncProcessMode) {
            val reportId = getReportIdFromResponse(json2)
            if (reportId != null) {
                // gets back the id of the internal report
                val internalReportId2 = getSingleChildReportId(reportId)
                    ?: return bad("***qualityfilter FAILED***: InternalReportId2 report id null")

                val processResults2 = pollForStepResult(internalReportId2, TaskAction.process)
                // verify each result is valid
                for (result in processResults2.values) {
                    passed = passed &&
                        examineProcessResponse(result) &&
                        checkJsonItemCountForReceiver(qualityGoodReceiver, expectItemCount, result!!)
                }
            }
        } else {
            passed = passed && checkJsonItemCountForReceiver(qualityGoodReceiver, expectItemCount, json2)
        }

        // FAIL
        ugly("\nTest a QualityFilter that allows NO data through.")
        expectItemCount = 0 // No Item
        val file3 = FileUtilities.createFakeCovidFile(
            metadata,
            settings,
            emptySender.schemaName,
            fakeItemCount,
            receivingStates,
            qualityFailReceiver.name,
            options.dir,
        )
        echo("Created datafile $file3")
        // Now send it to ReportStream.
        val (responseCode3, json3) =
            HttpUtilities.postReportFile(
                environment,
                file3,
                emptySender,
                options.asyncProcessMode,
                options.key,
                payloadName = "$name ${status.description}",
            )
        echo("Response to POST: $responseCode3")
        // if running in async mode, the initial response will not have destinations - the 'process' action result will
        if (options.asyncProcessMode) {
            val reportId = getReportIdFromResponse(json3)
            if (reportId != null) {
                // gets back the id of the internal report
                val internalReportId3 = getSingleChildReportId(reportId)
                    ?: return bad("***qualityfilter FAILED***: InternalReportId3 report id null")

                val processResults3 = pollForStepResult(internalReportId3, TaskAction.process)
                // verify each result is valid
                for (result in processResults3.values) {
                    passed = passed &&
                        examineProcessResponse(result) &&
                        checkJsonItemCountForReceiver(qualityFailReceiver, expectItemCount, result!!)
                }
            }
        } else {
            passed = passed && checkJsonItemCountForReceiver(qualityFailReceiver, expectItemCount, json3)
        }

        // QUALITY_REVERSED
        ugly("\nTest the REVERSE of the QualityFilter that allows some data through")
        expectItemCount = 2
        val file4 = FileUtilities.createFakeCovidFile(
            metadata,
            settings,
            emptySender.schemaName,
            fakeItemCount,
            receivingStates,
            qualityReversedReceiver.name + ",kept", // 3 removed, 2 kept
            options.dir,
        )
        echo("Created datafile $file4")
        // Now send it to ReportStream.
        val (responseCode4, json4) =
            HttpUtilities.postReportFile(
                environment,
                file4,
                emptySender,
                options.asyncProcessMode,
                options.key,
                payloadName = "$name ${status.description}",
            )
        echo("Response to POST: $responseCode4")
        // if running in async mode, the initial response will not have destinations - the 'process' action result will
        if (options.asyncProcessMode) {
            val reportId = getReportIdFromResponse(json4)
            if (reportId != null) {
                // gets back the id of the internal report
                val internalReportId4 = getSingleChildReportId(reportId)
                    ?: return bad("***qualityfilter FAILED***: internalReportId4 report id null")

                val processResults4 = pollForStepResult(internalReportId4, TaskAction.process)
                // verify each result is valid
                for (result in processResults4.values) {
                    passed = passed &&
                        examineProcessResponse(result) &&
                        checkJsonItemCountForReceiver(qualityReversedReceiver, expectItemCount, result!!)
                }
            }
        } else {
            passed = passed && checkJsonItemCountForReceiver(qualityReversedReceiver, expectItemCount, json4)
        }

        return passed
    }
}

/**
 * Test weirdness in Staging wherein we have strange HL7 'send' numbers
 *
 * This test, when it fails, exposes a database connection exception in Staging.
 * I think this is actually passing now, but the query isn't quite right.
 */
class DbConnections : CoolTest() {
    override val name = "dbconnections"
    override val description = "Test issue wherein many 'sends' caused db connection failures"
    override val status = TestStatus.DRAFT

    override suspend fun run(environment: Environment, options: CoolTestOptions): Boolean {
        ugly("Starting dbconnections Test: test of many threads attempting to sftp ${options.items} HL7s.")
        val file = FileUtilities.createFakeCovidFile(
            metadata,
            settings,
            simpleRepSender.schemaName,
            options.items,
            receivingStates,
            "HL7",
            options.dir,
        )
        echo("Created datafile $file")
        // Now send it to ReportStream.   Make numResends > 1 to create merges.
        val reportIds = (1..options.submits).map {
            val (responseCode, json) =
                HttpUtilities.postReportFile(
                    environment,
                    file,
                    simpleRepSender,
                    options.asyncProcessMode,
                    options.key,
                    payloadName = "$name ${status.description}",
                )
            echo("Response to POST: $responseCode")
            echo(json)
            if (responseCode != HttpURLConnection.HTTP_CREATED) {
                bad("***dbconnections Test FAILED***:  response code $responseCode")
                return false
            }
            val tree = jacksonObjectMapper.readTree(json)
            val reportId = ReportId.fromString(tree["id"].textValue())
            echo("Id of submitted report: $reportId")
            reportId
        }
        waitABit(30, environment)
        var passed = true
        reportIds.forEach {
            passed = passed and
                pollForLineageResults(
                    reportId = it,
                    receivers = listOf(hl7Receiver),
                    totalItems = options.items,
                    asyncProcessMode = options.asyncProcessMode
                )
        }
        return passed
    }
}

/**
 * Test using the a receiver with a broken sftp site.  Note:  there are two failure modes here:
 * - if the sftp auth stuff is set up, then RS get repeated IOException, and multiple retries.
 * - if its NOT set up, then RS fails without retries.
 *
 * Either way, the lineage results for the 'send' step should be zero.
 */
class BadSftp : CoolTest() {
    override val name = "badsftp"
    override val description = "Test ReportStream's response to sftp connection failures. Tests RETRY too!"
    override val status = TestStatus.DRAFT

    override suspend fun run(environment: Environment, options: CoolTestOptions): Boolean {
        ugly("Starting badsftp Test: test that our code handles sftp connectivity problems")
        val file = FileUtilities.createFakeCovidFile(
            metadata,
            settings,
            simpleRepSender.schemaName,
            options.items,
            receivingStates,
            sftpFailReceiver.name,
            options.dir,
        )
        echo("Created datafile $file")
        val (responseCode, json) =
            HttpUtilities.postReportFile(
                environment, file, simpleRepSender, options.asyncProcessMode, options.key,
                payloadName = "$name ${status.description}",
            )
        echo("Response to POST: $responseCode")
        echo(json)
        if (responseCode != HttpURLConnection.HTTP_CREATED) {
            bad("***badsftp Test FAILED***:  response code $responseCode")
            return false
        }
        val reportId = getReportIdFromResponse(json)
            ?: return bad("***$name Test FAILED***: A report ID came back as null")
        echo("Id of submitted report: $reportId")
        waitABit(30, environment)
        echo("For this test, failure during send, is a 'pass'.   Need to fix this.")
        return pollForLineageResults(
            reportId = reportId,
            receivers = listOf(sftpFailReceiver),
            totalItems = options.items,
            asyncProcessMode = options.asyncProcessMode
        )
    }
}

/**
 * Generate a report with international characters to verify we can handle them.  This test will send the
 * report file and inspect the uploaded file to the SFTP server to make sure the international characters are
 * present.  This test can only be run locally because of the testing of the uploaded files.
 */
class InternationalContent : CoolTest() {
    override val name = "intcontent"
    override val description = "Create Fake data that includes international characters, " +
        "submit, wait, confirm sent via database lineage data"
    override val status = TestStatus.DRAFT // Because this can only be run local to get access to the SFTP folder

    override suspend fun run(environment: Environment, options: CoolTestOptions): Boolean {
        if (options.env != "local") {
            return bad(
                "***intcontent Test FAILED***: This test can only be run locally " +
                    "as it needs access to the SFTP folder."
            )
        }

        // Make sure we have access to the SFTP folder
        if (!Files.isDirectory(Paths.get(options.sftpDir))) {
            return bad("***intcontent Test FAILED***: The folder ${options.sftpDir} cannot be found.")
        }
        val receiverName = hl7Receiver.name
        ugly("Starting $name Test: send ${simpleRepSender.fullName} data to $receiverName")
        val file = FileUtilities.createFakeCovidFile(
            metadata,
            settings,
            simpleRepSender.schemaName,
            1,
            receivingStates,
            receiverName,
            options.dir,
            // Use the Chinese locale since the fake data is mainly Chinese characters
            // https://github.com/DiUS/java-faker/blob/master/src/main/resources/zh-CN.yml
            locale = Locale.CHINA
        )
        echo("Created datafile $file")
        // Now send it to ReportStream.
        val (responseCode, json) =
            HttpUtilities.postReportFile(
                environment, file, simpleRepSender, options.asyncProcessMode, options.key,
                payloadName = "$name ${status.description}",
            )
        echo("Response to POST: $responseCode")
        echo(json)
        if (responseCode != HttpURLConnection.HTTP_CREATED) {
            return bad("***intcontent Test FAILED***:  response code $responseCode")
        }
        try {
            // Read the response
            val reportId = getReportIdFromResponse(json)
                ?: return bad("***$name Test FAILED***: A report ID came back as null")

            echo("Id of submitted report: $reportId")
            waitABit(60, environment)
            // Go to the database and get the SFTP filename that was sent
            db = WorkflowEngine().db
            var asciiOnly = false
            db.transact { txn ->
                val filename = sftpFilenameQuery(txn, reportId, receiverName)
                // If we get a file, test the contents to see if it is all ASCII only.
                if (filename != null) {
                    val contents = File(options.sftpDir, filename).inputStream().readBytes().toString(Charsets.UTF_8)
                    asciiOnly = CharMatcher.ascii().matchesAllOf(contents)
                }
            }
            if (asciiOnly) {
                return bad("***intcontent Test FAILED***: File contents are only ASCII characters")
            } else {
                return good("Test passed: for intcontent")
            }
        } catch (e: NullPointerException) {
            return bad("***intcontent Test FAILED***: Unable to properly parse response json")
        } catch (e: DataAccessException) {
            echoFn(e, true, true, "\n")
            return bad("***intcontent Test FAILED***: There was an error fetching data from the database.")
        }
    }
}

/**
 * Creates fake data as if from a sender and tries to send it to every state and territory
 */
class SantaClaus : CoolTest() {
    override val name = "santaclaus"
    override val description = "Creates fake data as if from a sender and tries to send it to every state and territory"
    override val status = TestStatus.DRAFT

    override suspend fun run(environment: Environment, options: CoolTestOptions): Boolean {
        var passed = true
        if (options.env !in listOf("local", "staging")) {
            return createBad("This test can only be run locally or on staging")
        }
        var sendersToTestWith = settings.senders.filter {
            it.organizationName in listOf("simple_report", "waters", "strac", "safehealth")
        }
        options.sender?.let {
            // validates sender existence
            val sender = settings.findSender(it) ?: return createBad("The sender indicated doesn't exists '$it'")
            // replace the list of senders to test
            // with the indicated by parameter
            sendersToTestWith = listOf(sender)
        }

        val states = if (options.targetStates.isNullOrEmpty()) {
            metadata.findLookupTable("fips-county")?.FilterBuilder()?.findAllUnique("State")
                ?.toList() ?: error("Santa is unable to find any states in the fips-county table")
        } else {
            options.targetStates.split(",")
        }
        ugly("Santa is sending data to these nice states: $states")

        sendersToTestWith.forEach { sender ->
            ugly("Starting $name Test: send with ${sender.fullName}")
            val file = FileUtilities.createFakeCovidFile(
                metadata = metadata,
                settings = settings,
                schemaName = (sender as CovidSender).schemaName,
                count = states.size,
                format = if (sender.format == MimeFormat.CSV) MimeFormat.CSV else MimeFormat.HL7_BATCH,
                directory = System.getProperty("java.io.tmpdir"),
                targetStates = states.joinToString(","),
                targetCounties = null
            )
            echo("Created datafile $file")
            // Now send it to ReportStream.
            val (responseCode, json) =
                HttpUtilities.postReportFile(
                    environment, file, sender, options.asyncProcessMode, options.key,
                    payloadName = "$name ${status.description}",
                )
            if (responseCode != HttpURLConnection.HTTP_CREATED) {
                return bad("***$name Test FAILED***:  response code $responseCode")
            } else {
                good("Posting of report succeeded with response code $responseCode")
            }
            echo(json)
            val tree = jacksonObjectMapper.readTree(json)
            val reportId = getReportIdFromResponse(json)
                ?: return bad("***$name Test FAILED***: A report ID came back as null")
            val destinations = tree["destinations"]
            if (destinations != null && destinations.size() > 0) {
                val receivers = mutableListOf<Receiver>()
                destinations.forEach { destination ->
                    if (destination != null && destination.has("service")) {
                        val receiverName = destination["service"].textValue()
                        val organizationId = destination["organization_id"].textValue()
                        receivers.addAll(
                            settings.receivers.filter {
                                it.organizationName == organizationId && it.name == receiverName
                            }
                        )
                    }
                }
                if (!receivers.isNullOrEmpty()) {
                    passed = passed and pollForLineageResults(
                        reportId = reportId,
                        receivers = receivers,
                        totalItems = receivers.size,
                        filterOrgName = true,
                        silent = false,
                        asyncProcessMode = options.asyncProcessMode
                    )
                }
            }
        }
        return passed
    }

    /**
     * Executes the 'block' function parameter and
     * evaluates its boolean result. If it's false it
     * retries the execution in 1 second, at most the
     * quantity of retries indicated by parameter.
     *
     * @param retries Max times to repeat the execution of @param[block] while it returns false
     * @param block Function to evaluate its result
     * @param callback Returns the state of the last execution and the retry count
     *
     * @return true if the @param[block] returns true, false otherwise or if the retry count reaches to its limit
     */
    private fun waitWithConditionalRetry(
        retries: Int,
        block: () -> Boolean,
        callback: (succeed: Boolean, retryCount: Int) -> Unit,
    ): Boolean {
        var retriesCopy = retries

        while (retriesCopy > 0) {
            val blockSuccess = block()
            callback(blockSuccess, abs(retriesCopy - retries))
            if (blockSuccess) {
                return true
            }
            retriesCopy--
            sleep(1000)
        }

        return false
    }

    private fun createBad(message: String): Boolean = bad("***$name Test FAILED***: $message")
}

class OtcProctored : CoolTest() {
    override val name = "otcproctored"
    override val description = "Verify that otc/proctored flags are working as expected on api response"
    override val status = TestStatus.SMOKE
    val failures = mutableListOf<String>()

    override suspend fun run(environment: Environment, options: CoolTestOptions): Boolean {
        val otcPairs = listOf(
            Pair("BinaxNOW COVID-19 Antigen Self Test_Abbott Diagnostics Scarborough, Inc.", "OTC_PROCTORED_YYY"),
            Pair("10811877011337", "OTC_PROCTORED_NYY"),
            Pair("00810055970001", "OTC_PROCTORED_NUNKUNK"),
        )
        for (pair in otcPairs) {
            ugly(
                "Starting Otc Test: submitting a file containing a device_id:" +
                    " ${pair.first} should match receiver ${pair.second}."
            )
            val reFile = FileUtilities.replaceText(
                "./src/test/csv_test_files/input/otc-template.csv",
                "replaceMe",
                "${pair.first}"
            )

            if (!reFile.exists()) {
                error("Unable to find file ${reFile.absolutePath} to do otc test")
            }
            val (responseCode, json) = HttpUtilities.postReportFile(
                environment,
                reFile,
                watersSender,
                options.asyncProcessMode,
                options.key
            )

            echo("Response to POST: $responseCode")
            if (examinePostResponse(json, !options.asyncProcessMode)) {
                // if testing async, verify the process result was generated
                if (options.asyncProcessMode) {
                    val reportId = getReportIdFromResponse(json)
                    if (reportId != null) {
                        // gets back the id of the internal report
                        val internalReportId = getSingleChildReportId(reportId)
                            ?: return bad("***otcproctored FAILED***: Internal report id null")

                        val processResults = pollForStepResult(internalReportId, TaskAction.process)
                        // verify each result is valid
                        for (result in processResults.values) {
                            if (!examineProcessResponse(result)) {
                                bad("*** otcproctored FAILED***: Process result invalid")
                            }
                        }
                    }
                }
                good("Test PASSED: ${pair.first}")
            } else {
                bad("Test FAILED: ${pair.first}")
                failures.add("${pair.first}")
            }
        }

        if (failures.size == 0) {
            return true
        } else {
            return bad("Tests FAILED: $failures")
        }
    }
}