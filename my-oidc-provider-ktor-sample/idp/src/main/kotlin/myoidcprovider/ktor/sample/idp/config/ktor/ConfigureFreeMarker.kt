package myoidcprovider.ktor.sample.idp.config.ktor

import freemarker.cache.ClassTemplateLoader
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.freemarker.FreeMarker

/**
 * FreeMarker の設定
 */
fun Application.configureFreeMarker() {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }
}
