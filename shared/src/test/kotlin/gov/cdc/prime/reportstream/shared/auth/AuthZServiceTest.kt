package gov.cdc.prime.reportstream.shared.auth

import gov.cdc.prime.reportstream.shared.auth.jwt.OktaGroupsJWTReader
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthZServiceTest {

    inner class Fixture {
        val oktaGroupsJWTReader: OktaGroupsJWTReader = mockk()
        val service = AuthZService(oktaGroupsJWTReader)
    }

    @Test
    fun `admin allowed access regardless of client_id`() {
        val f = Fixture()

        assertEquals(
            f.service.isSenderAuthorized("org.sender", listOf("DHPrimeAdmins")),
            true
        )
    }

    @Test
    fun `sender authorized`() {
        val f = Fixture()

        assertEquals(
            f.service.isSenderAuthorized("org.sender", listOf("DHSender_org")),
            true
        )
    }

    @Test
    fun `not a sender`() {
        val f = Fixture()

        assertEquals(
            f.service.isSenderAuthorized("org.sender", listOf("DHSomething_else")),
            false
        )
    }
}