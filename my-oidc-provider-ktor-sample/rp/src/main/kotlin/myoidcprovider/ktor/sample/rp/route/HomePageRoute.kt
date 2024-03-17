package myoidcprovider.ktor.sample.rp.route

import io.ktor.server.application.call
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import myoidcprovider.ktor.sample.rp.session.UserSession

/**
 * ホームページ
 */
fun Route.homePage() {
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
}

/**
 * ホームページのビューモデル
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
