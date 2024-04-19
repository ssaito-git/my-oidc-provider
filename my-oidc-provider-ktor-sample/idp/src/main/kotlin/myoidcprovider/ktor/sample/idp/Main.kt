package myoidcprovider.ktor.sample.idp

import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import myoidcprovider.ktor.sample.idp.config.ktor.configureAuthentication
import myoidcprovider.ktor.sample.idp.config.ktor.configureFreeMarker
import myoidcprovider.ktor.sample.idp.config.ktor.configureKoin
import myoidcprovider.ktor.sample.idp.config.ktor.configureOidcProvider
import myoidcprovider.ktor.sample.idp.config.ktor.configureRouting
import myoidcprovider.ktor.sample.idp.config.ktor.configureSerialization
import myoidcprovider.ktor.sample.idp.config.ktor.configureWebAuthnRouting

/**
 * メイン関数
 */
fun main(args: Array<String>) {
    val env = commandLineEnvironment(args) {
        module {
            configureKoin()
            configureFreeMarker()
            configureSerialization()
            configureOidcProvider()
            configureAuthentication()
            configureRouting()
            configureWebAuthnRouting()
        }
    }

    embeddedServer(Netty, environment = env).start(wait = true)
}

/**
 * ユーザー
 *
 * @property subject サブジェクト
 * @property name 名前
 * @property password パスワード
 */
data class User(val subject: String, val name: String, val password: String?)

/**
 * ユーザーリスト
 */
val users = mutableMapOf<String, MutableList<User>>()
