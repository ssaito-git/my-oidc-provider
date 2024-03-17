package myoidcprovider.ktor.sample.rp.route

import io.ktor.server.application.call
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

/**
 * エラーページ
 */
fun Route.errorPage() {
    get("/error") {
        call.respond(FreeMarkerContent("Error.ftl", null))
    }
}
