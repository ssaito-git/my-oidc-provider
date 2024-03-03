package myoidcprovider.ktor.sample.idp.session

import io.ktor.server.auth.Principal
import java.util.UUID

data class WebAuthnSignUpSession(val handle: UUID, val challenge: String) : Principal