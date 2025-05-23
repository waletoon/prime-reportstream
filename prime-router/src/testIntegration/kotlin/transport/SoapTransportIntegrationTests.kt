package gov.cdc.prime.router.transport

import assertk.assertThat
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import gov.cdc.prime.router.FileSettings
import gov.cdc.prime.router.Metadata
import gov.cdc.prime.router.MimeFormat
import gov.cdc.prime.router.SoapTransportType
import gov.cdc.prime.router.azure.ActionHistory
import gov.cdc.prime.router.azure.BlobAccess
import gov.cdc.prime.router.azure.WorkflowEngine
import gov.cdc.prime.router.azure.db.enums.TaskAction
import gov.cdc.prime.router.azure.db.tables.pojos.Task
import gov.cdc.prime.router.azure.observability.event.IReportStreamEventService
import gov.cdc.prime.router.credentials.UserPassCredential
import gov.cdc.prime.router.report.ReportService
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.spyk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import java.util.UUID
import kotlin.test.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SoapTransportIntegrationTests : TransportIntegrationTests() {
    private val metadata = Metadata.getInstance()
    private val settings = FileSettings(FileSettings.defaultSettingsDirectory)
    private val responseHeaders = headersOf("Content-Type" to listOf("text/xml"))
    private val mockClientOk = HttpClient(MockEngine) {
        engine {
            addHandler { _ ->
                respond(
                    ByteReadChannel(
                        "<response>hello there happy path</response>"
                    ),
                    HttpStatusCode.OK,
                    responseHeaders
                )
            }
        }
    }
    private val mockClientError = HttpClient(MockEngine) {
        engine {
            addHandler {
                respond(
                    ByteReadChannel("<error>This failed</error>"),
                    HttpStatusCode.InternalServerError,
                    responseHeaders
                )
            }
        }
    }
    private val actionHistory = ActionHistory(TaskAction.send)
    private val transportType = SoapTransportType(
        "my-end-point",
        "http://nedss.state.pa.us/2012/B01/elrwcf/IUploadFile/UploadFiles"
    )
    private val task = Task(
        reportId,
        TaskAction.send,
        null,
        "standard.standard-covid-19",
        "az-phd.elr-test",
        4,
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
        val content = "HL7|Stuff"
        return WorkflowEngine.Header(
            task, reportFile,
            null,
            settings.findOrganization("ignore"),
            settings.findReceiver("ignore.SOAP_TEST"),
            metadata.findSchema("covid-19"),
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
    fun `test connecting to mock service happy path`() {
        val header = makeHeader()
        val mockSoapTransport = spyk(SoapTransport(mockClientOk))
        every { mockSoapTransport.lookupCredentials(any()) }.returns(
            UserPassCredential(UUID.randomUUID().toString(), UUID.randomUUID().toString())
        )
        val retryItems = mockSoapTransport.send(
            transportType,
            header,
            reportId,
            "test",
            null,
            context,
            actionHistory,
            mockk<IReportStreamEventService>(relaxed = true),
            mockk<ReportService>(relaxed = true),
            listOf(),
            ""
        )
        assertThat(retryItems).isNull()
    }

    @Test
    fun `test connecting to mock service unhappy path`() {
        val header = makeHeader()
        val mockSoapTransport = spyk(SoapTransport(mockClientError))
        every { mockSoapTransport.lookupCredentials(any()) }.returns(
            UserPassCredential(UUID.randomUUID().toString(), UUID.randomUUID().toString())
        )
        val retryItems = mockSoapTransport.send(
            transportType,
            header,
            reportId,
            "test",
            null,
            context,
            actionHistory,
            mockk<IReportStreamEventService>(relaxed = true),
            mockk<ReportService>(relaxed = true),
            listOf(),
            ""
        )
        assertThat(retryItems).isNotNull()
    }
}