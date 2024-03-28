package myoidcprovider.ktor.sample.idp.config.ktor

import io.ktor.server.application.Application
import io.ktor.server.application.install
import myoidcprovider.ktor.sample.idp.repository.WebAuthnCredentialRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

/**
 * Koin の設定
 */
fun Application.configureKoin() {
    val appModule = module {
        singleOf(::WebAuthnCredentialRepository)
    }

    install(Koin) {
        modules(appModule)
    }
}
