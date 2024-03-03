package myoidcprovider.ktor.util

import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.httpMethod
import io.ktor.server.request.receiveParameters
import io.ktor.util.toMap
import myoidcprovider.core.http.HttpMethod
import myoidcprovider.core.http.HttpRequest

/**
 * [ApplicationCall] から [HttpRequest] を生成する。
 *
 * @return [HttpRequest]
 */
suspend fun ApplicationCall.toHttpRequest(): HttpRequest {
    val httpMethod = when (this.request.httpMethod) {
        io.ktor.http.HttpMethod.Companion.Get -> HttpMethod.GET
        io.ktor.http.HttpMethod.Companion.Post -> HttpMethod.POST
        else -> error("Unsupported http method.")
    }

    val formParameters = when (httpMethod) {
        HttpMethod.POST -> this.receiveParameters().toMap()
        else -> emptyMap()
    }

    return HttpRequest(
        httpMethod,
        this.request.headers.toMap(),
        formParameters,
        this.request.queryParameters.toMap(),
    )
}
