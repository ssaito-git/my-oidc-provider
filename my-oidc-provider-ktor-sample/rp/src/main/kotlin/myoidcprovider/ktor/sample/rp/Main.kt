package myoidcprovider.ktor.sample.rp

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.source.JWKSourceBuilder
import com.nimbusds.jose.proc.JWSVerificationKeySelector
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.JWTClaimNames
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import freemarker.cache.ClassTemplateLoader
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.OAuthAccessTokenResponse
import io.ktor.server.auth.OAuthServerSettings
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.oauth
import io.ktor.server.auth.principal
import io.ktor.server.engine.addShutdownHook
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.freemarker.FreeMarker
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import java.net.URL

/**
 * HTTP クライアント
 */
val applicationHttpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json()
    }
}

/**
 * メイン関数
 */
fun main(args: Array<String>) {
    val env = commandLineEnvironment(args) {
        module {
            configureFreeMarker()
            configureAuthentication()
            configureOpenId()
            configureRouting()
        }
        watchPaths = listOf("classes")
        developmentMode = true
    }

    embeddedServer(Netty, environment = env).start(wait = true).addShutdownHook {
        applicationHttpClient.close()
    }
}

/**
 * セッションの設定
 */
fun Application.configureAuthentication() {
    install(Sessions) {
        cookie<UserSession>("rp_user_session")
    }
}

/**
 * サインインセッション
 *
 * @property subject サブジェクト
 * @property name 名前
 */
data class UserSession(val subject: String, val name: String)

/**
 * FreeMarker の設定
 */
fun Application.configureFreeMarker() {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }
}

/**
 * OpenID の設定
 *
 * @param httpClient HTTP クライアント
 */
fun Application.configureOpenId(httpClient: HttpClient = applicationHttpClient) {
    install(Authentication) {
        oauth("auth-openid") {
            urlProvider = { "http://localhost:8081/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "id provider",
                    authorizeUrl = "http://localhost:8080/auth",
                    accessTokenUrl = "http://localhost:8080/token",
                    requestMethod = HttpMethod.Post,
                    clientId = "sample-rp",
                    clientSecret = "secret",
                    defaultScopes = listOf("openid"),
                )
            }
            client = httpClient
        }
    }
}

/**
 * ルーティングの設定
 */
fun Application.configureRouting() {
    routing {
        get("/") {
            val signInSession = call.sessions.get<UserSession>()

            if (signInSession != null) {
                call.respond(
                    FreeMarkerContent(
                        "Home.ftl",
                        mapOf("viewModel" to HomeViewModel(true, signInSession.subject, signInSession.name)),
                    ),
                )
            } else {
                call.respond(
                    FreeMarkerContent(
                        "Home.ftl",
                        mapOf("viewModel" to HomeViewModel(false, null, null)),
                    ),
                )
            }
        }
        get("/sign-out") {
            call.sessions.clear<UserSession>()
            call.respondRedirect("/")
        }
        get("/error") {
            call.respond(FreeMarkerContent("Error.ftl", null))
        }
        authenticate("auth-openid") {
            get("/login") { }
            get("/callback") {
                val idToken = call.principal<OAuthAccessTokenResponse.OAuth2>()?.extraParameters?.get("id_token")

                if (idToken == null) {
                    call.application.environment.log.info("トークンレスポンスに ID トークンが含まれていません")
                    call.respondRedirect("/error")
                    return@get
                }

                val keySource = JWKSourceBuilder
                    .create<SecurityContext>(URL("http://localhost:8080/jwks"))
                    .retrying(true)
                    .build()

                val jwtProcessor = DefaultJWTProcessor<SecurityContext>()
                jwtProcessor.jwsKeySelector = JWSVerificationKeySelector(JWSAlgorithm.ES256, keySource)
                jwtProcessor.jwtClaimsSetVerifier = DefaultJWTClaimsVerifier(
                    "sample-rp",
                    JWTClaimsSet.Builder().issuer("http://localhost:8080").build(),
                    setOf(JWTClaimNames.SUBJECT, JWTClaimNames.ISSUED_AT, JWTClaimNames.EXPIRATION_TIME),
                )

                val claimsSet = try {
                    jwtProcessor.process(idToken, null)
                } catch (e: Exception) {
                    call.application.environment.log.info("ID トークンの検証に失敗しました", e)
                    call.respondRedirect("/error")
                    return@get
                }

                call.sessions.set(UserSession(claimsSet.subject, claimsSet.getStringClaim("name") ?: ""))
                call.respondRedirect("/")
            }
        }
    }
}

/**
 * ホーム画面のビューモデル
 *
 * @property isSignIn サインインしているか
 * @property subject サブジェクト
 * @property name 名前
 */
data class HomeViewModel(
    val isSignIn: Boolean,
    val subject: String?,
    val name: String?,
)
