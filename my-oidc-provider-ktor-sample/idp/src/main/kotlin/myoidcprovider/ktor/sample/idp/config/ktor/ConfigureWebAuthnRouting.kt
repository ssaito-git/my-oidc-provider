package myoidcprovider.ktor.sample.idp.config.ktor

import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper
import com.webauthn4j.WebAuthnManager
import com.webauthn4j.authenticator.AuthenticatorImpl
import com.webauthn4j.converter.AttestedCredentialDataConverter
import com.webauthn4j.converter.exception.DataConversionException
import com.webauthn4j.converter.util.ObjectConverter
import com.webauthn4j.data.AuthenticationParameters
import com.webauthn4j.data.AuthenticatorTransport
import com.webauthn4j.data.RegistrationParameters
import com.webauthn4j.data.RegistrationRequest
import com.webauthn4j.data.client.Origin
import com.webauthn4j.data.client.challenge.DefaultChallenge
import com.webauthn4j.server.ServerProperty
import com.webauthn4j.validator.exception.ValidationException
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.application
import io.ktor.server.application.call
import io.ktor.server.application.plugin
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import myoidcprovider.ktor.plugin.OidcProvider
import myoidcprovider.ktor.sample.idp.session.SignInSession
import myoidcprovider.ktor.sample.idp.User
import myoidcprovider.ktor.sample.idp.session.WebAuthnSignInSession
import myoidcprovider.ktor.sample.idp.session.WebAuthnSignUpSession
import myoidcprovider.ktor.sample.idp.users
import myoidcprovider.ktor.sample.idp.webAuthnCredentials
import myoidcprovider.ktor.sample.idp.webauthn.AttestationStatementEnvelope
import myoidcprovider.ktor.sample.idp.webauthn.WebAuthnCredential
import java.nio.ByteBuffer
import java.util.Base64
import java.util.UUID

/**
 * WebAuthn ルーティングの設定
 */
fun Application.configureWebAuthnRouting() {
    routing {
        get("/webauthn/signInRequest") {
            val challenge = Base64.getEncoder().encodeToString(DefaultChallenge().value)
            val rpId = "localhost"
            val userVerification = "required"
            val content = """
                {
                    "rpId": "$rpId",
                    "challenge": "$challenge",
                    "userVerification": "$userVerification"
                }
            """.trimIndent()

            call.sessions.set(WebAuthnSignInSession(challenge))
            call.respondText(content, ContentType.Application.Json)
        }
        post("/webauthn/signIn") {
            val webAuthnSignInSession = call.sessions.get<WebAuthnSignInSession>()

            if (webAuthnSignInSession == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val parameters = call.receiveParameters()
            val credentialId = Base64.getUrlDecoder().decode(parameters["credentialId"])
            val userHandle = Base64.getDecoder().decode(parameters["userHandle"])
            val authenticatorData = Base64.getDecoder().decode(parameters["authenticatorData"])
            val clientDataJSON = Base64.getDecoder().decode(parameters["clientDataJSON"])
            val signature = Base64.getDecoder().decode(parameters["signature"])

            val authenticationRequest = com.webauthn4j.data.AuthenticationRequest(
                credentialId,
                userHandle,
                authenticatorData,
                clientDataJSON,
                signature,
            )

            val handle = ByteBuffer.wrap(userHandle).let {
                UUID(it.getLong(), it.getLong())
            }

            val (index, webAuthnCredential) = webAuthnCredentials.indexOfFirst {
                it.handle == handle && it.credentialId.contentEquals(credentialId)
            }.let {
                if (it == -1) {
                    Pair(it, null)
                } else {
                    Pair(it, webAuthnCredentials[it])
                }
            }

            if (webAuthnCredential == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val objectConverter = ObjectConverter()
            val attestedCredentialData = AttestedCredentialDataConverter(
                objectConverter,
            ).convert(webAuthnCredential.attestedCredentialData)
            val attestationStatement = CBORMapper().readValue(
                webAuthnCredential.attestationStatement,
                AttestationStatementEnvelope::class.java,
            )
            val transports = objectConverter.jsonConverter.readValue(
                webAuthnCredential.transports,
                Array<AuthenticatorTransport>::class.java,
            )?.toSet()
            val authenticator = AuthenticatorImpl(
                attestedCredentialData,
                attestationStatement.attestationStatement,
                webAuthnCredential.signCount,
                transports,
            )

            val origin = Origin("http://localhost:8080")
            val rpId = "localhost"
            val challenge = DefaultChallenge(Base64.getDecoder().decode(webAuthnSignInSession.challenge))
            val serverProperty = ServerProperty(origin, rpId, challenge, null)

            val allowCredentials: List<ByteArray>? = null
            val userVerificationRequired = false
            val userPresenceRequired = true

            val authenticationParameters = AuthenticationParameters(
                serverProperty,
                authenticator,
                allowCredentials,
                userVerificationRequired,
                userPresenceRequired,
            )

            val webAuthnManager = WebAuthnManager.createNonStrictWebAuthnManager()

            val authenticationData = try {
                webAuthnManager.parse(authenticationRequest)
            } catch (e: DataConversionException) {
                System.err.println(e)
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            try {
                webAuthnManager.validate(authenticationData, authenticationParameters)
            } catch (e: ValidationException) {
                System.err.println(e)
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val plugin = application.plugin(OidcProvider)
            val issuer = plugin.issuerResolver(call)
            val user = users[issuer]?.firstOrNull { it.subject == webAuthnCredential.userId }

            if (user == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            webAuthnCredentials[index] = webAuthnCredential.copy(
                signCount = authenticationData.authenticatorData?.signCount ?: 0,
            )

            call.sessions.clear<WebAuthnSignInSession>()
            call.sessions.set(SignInSession(user.subject))
            call.respond(HttpStatusCode.OK)
        }
    }

    routing {
        get("/sign-up") {
            call.respond(FreeMarkerContent("SignUpForm.ftl", null))
        }
        get("/webauthn/signUpRequest") {
            val rpName = "WebAuthn localhost"
            val rpId = "localhost"
            val handle = UUID.randomUUID()
            val userId = ByteBuffer.allocate(16).let {
                it.putLong(handle.mostSignificantBits)
                it.putLong(handle.leastSignificantBits)
            }.let {
                Base64.getEncoder().encodeToString(it.array())
            }
            val challenge = Base64.getEncoder().encodeToString(DefaultChallenge().value)
            val content = """
                {
                    "rpName": "$rpName",
                    "rpId": "$rpId",
                    "userId": "$userId",
                    "challenge": "$challenge"
                }
            """.trimIndent()

            call.sessions.set(WebAuthnSignUpSession(handle, challenge))
            call.respondText(content, ContentType.Application.Json)
        }
        post("/webauthn/signUp") {
            val webAuthnSignUpSession = call.sessions.get<WebAuthnSignUpSession>()

            if (webAuthnSignUpSession == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val parameters = call.receiveParameters()
            val username = parameters["username"]

            if (username.isNullOrEmpty()) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val attestationObject = Base64.getDecoder().decode(parameters["attestationObject"])
            val clientDataJSON = Base64.getDecoder().decode(parameters["clientDataJSON"])
            val clientExtensionJSON = null
            val transports = null
            val registrationRequest =
                RegistrationRequest(attestationObject, clientDataJSON, clientExtensionJSON, transports)

            val origin = Origin("http://localhost:8080")
            val rpId = "localhost"
            val challenge = DefaultChallenge(Base64.getDecoder().decode(webAuthnSignUpSession.challenge))
            val tokenBindingId = null
            val serverProperty = ServerProperty(origin, rpId, challenge, tokenBindingId)

            val userVerificationRequired = true
            val userPresenceRequired = true
            val registrationParameters = RegistrationParameters(
                serverProperty,
                null,
                userVerificationRequired,
                userPresenceRequired,
            )

            val webAuthnManager = WebAuthnManager.createNonStrictWebAuthnManager()

            val registrationData = try {
                webAuthnManager.validate(registrationRequest, registrationParameters)
            } catch (e: ValidationException) {
                System.err.println(e)
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val credentialId =
                registrationData.attestationObject?.authenticatorData?.attestedCredentialData?.credentialId

            if (credentialId == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val signCount = registrationData.attestationObject?.authenticatorData?.signCount

            if (signCount == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val attestedCredentialData =
                registrationData.attestationObject?.authenticatorData?.attestedCredentialData?.let {
                    AttestedCredentialDataConverter(
                        ObjectConverter(),
                    ).convert((it))
                }

            if (attestedCredentialData == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val attestationStatement = registrationData.attestationObject?.attestationStatement?.let {
                CBORMapper().writeValueAsBytes(AttestationStatementEnvelope(it))
            }

            if (attestationStatement == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val serializedTransports = ObjectConverter().jsonConverter.writeValueAsString(registrationData.transports)

            val plugin = application.plugin(OidcProvider)
            val issuer = plugin.issuerResolver(call)
            val user = User(UUID.randomUUID().toString(), username, null)
            users[issuer]?.add(user)

            val webAuthnCredential = WebAuthnCredential(
                user.subject,
                webAuthnSignUpSession.handle,
                credentialId,
                attestedCredentialData,
                attestationStatement,
                signCount,
                serializedTransports,
            )
            webAuthnCredentials.add(webAuthnCredential)

            call.sessions.clear<WebAuthnSignUpSession>()
            call.sessions.set(SignInSession(user.subject))
            call.respond(HttpStatusCode.OK)
        }
    }
}
