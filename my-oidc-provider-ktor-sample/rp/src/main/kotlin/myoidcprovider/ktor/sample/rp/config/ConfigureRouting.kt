package myoidcprovider.ktor.sample.rp.config

import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import myoidcprovider.ktor.sample.rp.route.errorPage
import myoidcprovider.ktor.sample.rp.route.homePage
import myoidcprovider.ktor.sample.rp.route.openIdConnect
import myoidcprovider.ktor.sample.rp.route.signOut

/**
 * ルーティングの設定
 */
fun Application.configureRouting() {
    routing {
        homePage()
        errorPage()
        signOut()
        openIdConnect()
    }
}
