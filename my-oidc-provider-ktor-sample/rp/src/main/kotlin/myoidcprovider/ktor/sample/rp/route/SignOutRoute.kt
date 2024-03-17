package myoidcprovider.ktor.sample.rp.route

import io.ktor.server.application.call
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.sessions
import myoidcprovider.ktor.sample.rp.session.UserSession

/**
 * サインアウト
 */
fun Route.signOut() {
    get("/sign-out") {
        call.sessions.clear<UserSession>()
        call.respondRedirect("/")
    }
}
