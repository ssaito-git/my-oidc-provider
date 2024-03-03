package myoidcprovider.ktor.sample.idp.session

import io.ktor.server.auth.Principal

data class WebAuthnSignInSession(val challenge: String) : Principal