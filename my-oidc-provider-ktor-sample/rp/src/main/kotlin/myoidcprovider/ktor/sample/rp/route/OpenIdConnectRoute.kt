package myoidcprovider.ktor.sample.rp.route

import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.source.JWKSourceBuilder
import com.nimbusds.jose.proc.BadJOSEException
import com.nimbusds.jose.proc.JWSVerificationKeySelector
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.JWTParser
import com.nimbusds.oauth2.sdk.id.ClientID
import com.nimbusds.oauth2.sdk.id.Issuer
import com.nimbusds.openid.connect.sdk.validators.IDTokenValidator
import io.ktor.server.application.call
import io.ktor.server.auth.OAuthAccessTokenResponse
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import myoidcprovider.ktor.sample.rp.session.UserSession
import java.net.URL

/**
 * OpenID Connect
 */
fun Route.openIdConnect() {
    authenticate("auth-openid") {
        get("/login") { }
        get("/callback") {
            val idTokenString = call.principal<OAuthAccessTokenResponse.OAuth2>()?.extraParameters?.get("id_token")

            if (idTokenString == null) {
                call.application.environment.log.info("トークンレスポンスに ID トークンが含まれていません")
                call.respondRedirect("/error")
                return@get
            }

            val keySource = JWKSourceBuilder.create<SecurityContext>(URL("http://localhost:8080/jwks"))
                .retrying(true)
                .build()

            val validator = IDTokenValidator(
                Issuer("http://localhost:8080"),
                ClientID("sample-rp"),
                JWSVerificationKeySelector(JWSAlgorithm.ES256, keySource),
                null,
            )

            val idToken = JWTParser.parse(idTokenString)

            val claims = try {
                validator.validate(idToken, null)
            } catch (e: BadJOSEException) {
                call.application.environment.log.info("ID トークンが不正です", e)
                call.respondRedirect("/error")
                return@get
            } catch (e: JOSEException) {
                call.application.environment.log.info("ID トークンの検証に失敗しました", e)
                call.respondRedirect("/error")
                return@get
            }

            call.sessions.set(UserSession(claims.subject.value, claims.getStringClaim("name") ?: ""))
            call.respondRedirect("/")
        }
    }
}
