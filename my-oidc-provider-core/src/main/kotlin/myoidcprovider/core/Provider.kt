package myoidcprovider.core

import com.github.michaelbull.result.Result
import myoidcprovider.core.config.Config
import myoidcprovider.core.exception.NotFoundIssuerException
import myoidcprovider.core.handler.AuthorizationRequestPostProcessHandler
import myoidcprovider.core.handler.AuthorizationRequestPreProcessHandler
import myoidcprovider.core.handler.IntrospectionRequestHandler
import myoidcprovider.core.handler.JWKsRequestHandler
import myoidcprovider.core.handler.OAuth2MetadataRequestHandler
import myoidcprovider.core.handler.OidcMetadataRequestHandler
import myoidcprovider.core.handler.RevocationRequestHandler
import myoidcprovider.core.handler.TokenRequestHandler
import myoidcprovider.core.http.HttpRequest
import myoidcprovider.core.issuer.IssuerConfig
import myoidcprovider.core.metadata.OAuth2Metadata
import myoidcprovider.core.metadata.OidcMetadata
import myoidcprovider.core.request.authorization.AuthorizationRequestData
import myoidcprovider.core.request.authorization.AuthorizationRequestError
import myoidcprovider.core.request.authorization.AuthorizationResponse
import myoidcprovider.core.request.authorization.AuthorizationResponseError
import myoidcprovider.core.request.introspection.IntrospectionRequestError
import myoidcprovider.core.request.introspection.IntrospectionResponse
import myoidcprovider.core.request.revocation.RevocationRequestError
import myoidcprovider.core.request.token.TokenRequestError
import myoidcprovider.core.request.token.TokenResponse

/**
 * OpenID Connect Provider.
 */
class Provider(
    /**
     * コンフィグ。
     */
    val config: Config,
) {
    private val oauth2MetadataRequestHandler = OAuth2MetadataRequestHandler(config.endpoint)
    private val oidcMetadataRequestHandler = OidcMetadataRequestHandler(config.endpoint)
    private val jwksRequestHandler = JWKsRequestHandler(config.jwkConfigStorage)
    private val authorizationRequestPreProcessHandler =
        AuthorizationRequestPreProcessHandler(
            config.clientConfigStorage,
            config.authorizationRequestDataStorage,
            config.clock,
        )
    private val authorizationRequestPostProcessHandler = AuthorizationRequestPostProcessHandler(
        config.clientConfigStorage,
        config.authorizationRequestDataStorage,
        config.accessTokenStorage,
        config.authorizationCodeStorage,
        config.jwkConfigStorage,
        config.userClaimSetStorage,
        config.clock,
    )
    private val tokenRequestHandler = TokenRequestHandler(
        config.clientAuthenticationManager,
        config.authorizationCodeStorage,
        config.clientConfigStorage,
        config.accessTokenStorage,
        config.refreshTokenStorage,
        config.jwkConfigStorage,
        config.userClaimSetStorage,
        config.securityTokenGenerator,
        config.clock,
    )
    private val revocationRequestHandler = RevocationRequestHandler(
        config.clientAuthenticationManager,
        config.clientConfigStorage,
        config.accessTokenStorage,
        config.refreshTokenStorage,
    )
    private val introspectionRequestHandler = IntrospectionRequestHandler(
        config.clientAuthenticationManager,
        config.accessTokenStorage,
        config.refreshTokenStorage,
        config.clock,
    )

    /**
     * OAuth 2.0 Authorization Server Metadata リクエストのハンドラー。
     *
     * @param issuer Issuer
     * @return Issuer の [OAuth2Metadata]。
     * @throws NotFoundIssuerException [issuer] に紐づく [IssuerConfig] が存在しない場合にスローされる。
     */
    fun handleOAuth2MetadataRequest(issuer: String): OAuth2Metadata {
        val issuerConfig = retrieveIssuerConfig(issuer)
        return oauth2MetadataRequestHandler.handle(issuerConfig)
    }

    /**
     * OpenID Provider Metadata リクエストのハンドラー。
     *
     * @param issuer Issuer
     * @return Issuer の [OidcMetadata]。
     * @throws NotFoundIssuerException [issuer] に紐づく [IssuerConfig] が存在しない場合にスローされる。
     */
    fun handleOidcMetadataRequest(issuer: String): OidcMetadata {
        val issuerConfig = retrieveIssuerConfig(issuer)
        return oidcMetadataRequestHandler.handle(issuerConfig)
    }

    /**
     * JWKs リクエストのハンドラー。
     *
     * @param issuer Issuer
     * @return Issuer の JWKs（文字列）。
     * @throws NotFoundIssuerException [issuer] に紐づく [IssuerConfig] が存在しない場合にスローされる。
     */
    fun handleJWKsRequest(issuer: String): String {
        val issuerConfig = retrieveIssuerConfig(issuer)
        return jwksRequestHandler.handle(issuerConfig)
    }

    /**
     * 認可リクエストのハンドラー。
     *
     * @param issuer Issuer
     * @param httpRequest HTTP リクエスト
     * @return 成功した場合は [AuthorizationRequestData]。失敗した場合は [AuthorizationRequestError]。
     * @throws NotFoundIssuerException [issuer] に紐づく [IssuerConfig] が存在しない場合にスローされる。
     */
    fun handleAuthorizationRequestPreProcessHandler(
        issuer: String,
        httpRequest: HttpRequest,
    ): Result<AuthorizationRequestData, AuthorizationRequestError> {
        val issuerConfig = retrieveIssuerConfig(issuer)
        return authorizationRequestPreProcessHandler.handle(issuerConfig, httpRequest)
    }

    /**
     * 認可リクエストの認証後のハンドラー。
     *
     * @param issuer Issuer
     * @param subject 識別子
     * @param authorizationRequestDataKey 認可リクエストのキー
     * @param consent 認可リクエストの同意有無
     * @return 成功した場合は [AuthorizationResponse]。失敗した場合は [AuthorizationResponseError]。
     * @throws NotFoundIssuerException [issuer] に紐づく [IssuerConfig] が存在しない場合にスローされる。
     */
    fun handleAuthorizationRequestPostProcessHandler(
        issuer: String,
        subject: String,
        authorizationRequestDataKey: String,
        consent: Boolean,
    ): Result<AuthorizationResponse, AuthorizationResponseError> {
        val issuerConfig = retrieveIssuerConfig(issuer)
        return authorizationRequestPostProcessHandler.handle(
            issuerConfig,
            subject,
            authorizationRequestDataKey,
            consent,
        )
    }

    /**
     * トークンリクエストのハンドラー。
     *
     * @param issuer Issuer
     * @param httpRequest HTTP リクエスト
     * @return 成功した場合は [TokenResponse]。失敗した場合は [TokenRequestError]。
     * @throws NotFoundIssuerException [issuer] に紐づく [IssuerConfig] が存在しない場合にスローされる。
     */
    fun handleTokenRequest(
        issuer: String,
        httpRequest: HttpRequest,
    ): Result<TokenResponse, TokenRequestError> {
        val issuerConfig = retrieveIssuerConfig(issuer)
        return tokenRequestHandler.handle(issuerConfig, httpRequest)
    }

    /**
     * リヴォケーションリクエストのハンドラー。
     *
     * @param issuer Issuer
     * @param httpRequest HTTP リクエスト
     * @return 成功した場合は [Unit]。失敗した場合は [RevocationRequestError]。
     * @throws NotFoundIssuerException [issuer] に紐づく [IssuerConfig] が存在しない場合にスローされる。
     */
    fun handleRevocationRequest(
        issuer: String,
        httpRequest: HttpRequest,
    ): Result<Unit, RevocationRequestError> {
        val issuerConfig = retrieveIssuerConfig(issuer)
        return revocationRequestHandler.handle(issuerConfig, httpRequest)
    }

    /**
     * トークンイントロスペクションリクエストのハンドラー。
     *
     * @param issuer Issuer
     * @param httpRequest HTTP リクエスト
     * @return 成功した場合は [IntrospectionResponse]。失敗した場合は [IntrospectionRequestError]。
     * @throws NotFoundIssuerException [issuer] に紐づく [IssuerConfig] が存在しない場合にスローされる。
     */
    fun handleIntrospectionRequest(
        issuer: String,
        httpRequest: HttpRequest,
    ): Result<IntrospectionResponse, IntrospectionRequestError> {
        val issuerConfig = retrieveIssuerConfig(issuer)
        return introspectionRequestHandler.handle(issuerConfig, httpRequest)
    }

    private fun retrieveIssuerConfig(issuer: String): IssuerConfig {
        return config.issuerConfigStorage.findByIssuer(issuer)
            ?: throw NotFoundIssuerException("Not found issuer. [$issuer]")
    }
}
