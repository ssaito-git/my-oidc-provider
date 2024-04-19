package myoidcprovider.ktor.sample.rp

import io.ktor.server.engine.addShutdownHook
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import myoidcprovider.ktor.sample.rp.config.configureAuthentication
import myoidcprovider.ktor.sample.rp.config.configureFreeMarker
import myoidcprovider.ktor.sample.rp.config.configureOpenIdConnect
import myoidcprovider.ktor.sample.rp.config.configureRouting

/**
 * メイン関数
 */
fun main(args: Array<String>) {
    val env = commandLineEnvironment(args) {
        module {
            configureFreeMarker()
            configureAuthentication()
            configureOpenIdConnect()
            configureRouting()
        }
    }

    embeddedServer(Netty, environment = env).apply {
        addShutdownHook {
            applicationHttpClient.close()
        }
    }.start(wait = true)
}
