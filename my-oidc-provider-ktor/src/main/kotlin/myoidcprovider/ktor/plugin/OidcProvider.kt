package myoidcprovider.ktor.plugin

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.BaseApplicationPlugin
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelineContext
import myoidcprovider.core.Provider
import myoidcprovider.core.request.authorization.AuthorizationRequestData
import myoidcprovider.core.request.authorization.AuthorizationRequestError

/**
 * OIDC プロバイダーのプラグイン。
 */
class OidcProvider(
    /**
     * コンフィグ
     */
    configuration: Configuration,
) {
    /**
     * OIDC プロバイダー
     */
    val provider = configuration.provider

    /**
     * Issuer リゾルバー
     */
    val issuerResolver = configuration.issuerResolver

    /**
     * 認可リクエストエラーハンドラー
     */
    val authorizationRequestErrorHandler = configuration.authorizationRequestErrorHandler

    /**
     * 認可リクエストサクセスハンドラー
     */
    val authorizationRequestSuccessHandler = configuration.authorizationRequestSuccessHandler

    /**
     * コンフィグ
     */
    class Configuration {
        /**
         * OIDC プロバイダー
         */
        lateinit var provider: Provider

        /**
         * Issuer リゾルバー
         */
        lateinit var issuerResolver: (ApplicationCall) -> String

        /**
         * 認可リクエストエラーハンドラー
         */
        lateinit var authorizationRequestErrorHandler:
            suspend PipelineContext<Unit, ApplicationCall>.(
                authorizationRequestError: AuthorizationRequestError,
            ) -> Unit

        /**
         * 認可リクエストサクセスハンドラー
         */
        lateinit var authorizationRequestSuccessHandler:
            suspend PipelineContext<Unit, ApplicationCall>.(authorizationRequestData: AuthorizationRequestData) -> Unit
    }

    companion object : BaseApplicationPlugin<Application, Configuration, OidcProvider> {
        override val key = AttributeKey<OidcProvider>("OidcProvider")

        override fun install(pipeline: Application, configure: Configuration.() -> Unit): OidcProvider {
            val configuration = Configuration().apply(configure)
            return OidcProvider(configuration)
        }
    }
}
