package myoidcprovider.ktor.sample.rp.config

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import myoidcprovider.ktor.sample.rp.session.UserSession

/**
 * セッションの設定
 */
fun Application.configureAuthentication() {
    install(Sessions) {
        cookie<UserSession>("rp_user_session")
    }
}
