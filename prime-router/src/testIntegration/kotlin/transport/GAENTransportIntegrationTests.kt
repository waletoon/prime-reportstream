package gov.cdc.prime.router.transport

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import assertk.assertions.isTrue
import gov.cdc.prime.router.FileSettings
import gov.cdc.prime.router.GAENTransportType
import gov.cdc.prime.router.Metadata
import gov.cdc.prime.router.MimeFormat
import gov.cdc.prime.router.azure.ActionHistory
import gov.cdc.prime.router.azure.BlobAccess
import gov.cdc.prime.router.azure.WorkflowEngine
import gov.cdc.prime.router.azure.db.enums.TaskAction
import gov.cdc.prime.router.azure.db.tables.pojos.Task
import gov.cdc.prime.router.azure.observability.event.IReportStreamEventService
import gov.cdc.prime.router.credentials.UserApiKeyCredential
import gov.cdc.prime.router.report.ReportService
import io.ktor.http.HttpStatusCode
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.spyk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class GAENTransportIntegrationTests : TransportIntegrationTests() {
    private fun getMockUtil(
        url: String,
        status: HttpStatusCode,
        body: String,
    ): GAENTransport = GAENTransport(
            ApiMockEngineForIntegrationTests(
                url,
                status,
                body
            ).client()
        )

    private val metadata = Metadata.getInstance()
    private val settings = FileSettings(FileSettings.defaultSettingsDirectory)

    private val transportType = GAENTransportType("http://localhost:3000/")
    private val successJson = """
        {"padding":"-",
        "uuid":"50d5e748-ffb0-4ac2-8149-1b246c1e1696",
        "code":"58900711",
        "expiresAt":"Fri, 05 Nov 2021 18:15:13 UTC",
        "expiresAtTimestamp":1636136113,"longExpiresAt":"Sat, 06 Nov 2021 18:00:13 UTC",
        "longExpiresAtTimestamp":1636221613}
    """.trimIndent()
    private val maintenanceJson = """
        {
            "error": "The server is temporarily down for maintenance. Wait and retry later.",
            "errorCode": "maintenance_mode",
        }
    """.trimIndent()
    private val errorJson = """
        {
            "error": "The provided test or symptom date, was older or newer than the realm allows.",
            "errorCode": "invalid_date",
        }
    """.trimIndent()

    private val task = Task(
        reportId,
        TaskAction.send,
        null,
        "covid-19-gaen",
        "wa-phd.gaen",
        1,
        "",
        "",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    )

    private fun makeHeader(): WorkflowEngine.Header {
        val content = """
            abnormal_flag,message_id,illness_onset,date_result_released,patient_phone_number
            A,064545,2021-06-04,2021-06-03,+12896189225
        """.trimIndent()
        return WorkflowEngine.Header(
            task,
            reportFile,
            null,
            settings.findOrganization("ignore"),
            settings.findReceiver("ignore.GAEN_TEST"),
            metadata.findSchema("covid-19-gaen"),
            content = content.toByteArray(),
            true
        )
    }

    @BeforeEach
    fun setup() {
        mockkObject(BlobAccess)
        every {
            BlobAccess.uploadBody(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns BlobAccess.BlobInfo(MimeFormat.HL7, "", "".toByteArray())
    }

    @Test
    fun `test send happy path`() {
        val header = makeHeader()

        val actionHistory = ActionHistory(TaskAction.send)

        val transObj = getMockUtil(
            "/",
            HttpStatusCode.OK,
            body = successJson
        )

        val transMock = spyk(transObj)

        every { transMock.lookupCredentials(any()) }
            .returns(UserApiKeyCredential("rick", "xzy"))

        val retryItems = transMock.send(
            transportType,
            header,
            UUID.randomUUID(),
            "test",
            retryItems = null,
            context,
            actionHistory,
            mockk<IReportStreamEventService>(relaxed = true),
            mockk<ReportService>(relaxed = true),
            listOf(),
            ""
        )

        assertThat(retryItems).isNull()
        assertThat(actionHistory.action.actionName).isEqualTo(TaskAction.send)
    }

    @Test
    fun `test send and retry`() {
        val header = makeHeader()

        val actionHistory = ActionHistory(TaskAction.send)

        val transObj = getMockUtil(
            "/",
            HttpStatusCode.TooManyRequests,
            body = maintenanceJson
        )

        val transMock = spyk(transObj)

        every { transMock.lookupCredentials(any()) }
            .returns(UserApiKeyCredential("rick", "xzy"))

        val retryItems = transMock.send(
            transportType,
            header,
            UUID.randomUUID(),
            "test",
            retryItems = null,
            context,
            actionHistory,
            mockk<IReportStreamEventService>(relaxed = true),
            mockk<ReportService>(relaxed = true),
            listOf(),
            ""
        )

        assertThat(RetryToken.isAllItems(retryItems)).isTrue()
        assertThat(actionHistory.action.actionName).isEqualTo(TaskAction.send_warning)
    }

    @Test
    fun `test send and 400 error handling`() {
        val header = makeHeader()

        val actionHistory = ActionHistory(TaskAction.send)

        val transObj = getMockUtil(
            "/",
            HttpStatusCode.BadRequest,
            body = errorJson
        )

        val transMock = spyk(transObj)

        every { transMock.lookupCredentials(any()) }
            .returns(UserApiKeyCredential("rick", "xzy"))

        val retryItems = transMock.send(
            transportType,
            header,
            UUID.randomUUID(),
            "test",
            retryItems = null,
            context,
            actionHistory,
            mockk<IReportStreamEventService>(relaxed = true),
            mockk<ReportService>(relaxed = true),
            listOf(),
            ""
        )

        assertThat(retryItems).isNull()
        assertThat(actionHistory.action.actionName).isEqualTo(TaskAction.send)
        assertThat(actionHistory.action.actionResult).contains("""Successful exposure""")
    }

    @Test
    fun `test send and 409 error handling`() {
        val header = makeHeader()

        val actionHistory = ActionHistory(TaskAction.send)

        val transObj = getMockUtil(
            "/",
            HttpStatusCode.Conflict,
            body = errorJson
        )

        val transMock = spyk(transObj)

        every { transMock.lookupCredentials(any()) }
            .returns(UserApiKeyCredential("rick", "xzy"))

        val retryItems = transMock.send(
            transportType,
            header,
            UUID.randomUUID(),
            "test",
            retryItems = null,
            context,
            actionHistory,
            mockk<IReportStreamEventService>(relaxed = true),
            mockk<ReportService>(relaxed = true),
            listOf(),
            ""
        )

        assertThat(retryItems).isNull()
        assertThat(actionHistory.action.actionName).isEqualTo(TaskAction.send)
        assertThat(actionHistory.action.actionResult).contains("""Successful exposure""")
    }
}