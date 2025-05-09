package gov.cdc.prime.reportstream.shared

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import gov.cdc.prime.reportstream.shared.StringUtilities.base64Decode
import gov.cdc.prime.reportstream.shared.StringUtilities.base64Encode
import gov.cdc.prime.reportstream.shared.StringUtilities.toIntOrDefault
import gov.cdc.prime.reportstream.shared.StringUtilities.trimToNull
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StringUtilitiesTests {
    @Test
    fun `test trimToNull`() {
        assertThat(" foo ".trimToNull()).isEqualTo("foo")
        assertThat(" ".trimToNull()).isNull()
        assertThat("".trimToNull()).isNull()
        assertThat(" \t \r\n ".trimToNull()).isNull()
        val bar = null
        assertThat(bar.trimToNull()).isNull()
    }

    @Test
    fun `test toIntOrDefault`() {
        assertThat("10".toIntOrDefault()).isEqualTo(10)
        assertThat("".toIntOrDefault()).isEqualTo(0)
        assertThat("".toIntOrDefault(11)).isEqualTo(11)
        val bar = null
        assertThat(bar.toIntOrDefault()).isEqualTo(0)
        assertThat(bar.toIntOrDefault(42)).isEqualTo(42)
    }

    @Test
    fun `test base64 encoding and decoding`() {
        val testString = "test string!"

        val encoded = testString.base64Encode()

        assertEquals(
            encoded.base64Decode(),
            testString
        )
    }
}