package myoidcprovider.core.config

/**
 * エンドポイント。
 */
data class Endpoint(
    /**
     * 認可エンドポイント。
     */
    val authorizationEndpoint: String = "/auth",
    /**
     * トークンエンドポイント。
     */
    val tokenEndpoint: String = "/token",
    /**
     * JWKS エンドポイント。
     */
    val jwksEndpoint: String = "/jwks",
    /**
     * トークンイントロスペクションエンドポイント。
     */
    val introspectionEndpoint: String = "/introspection",
    /**
     * トークン無効化エンドポイント。
     */
    val revocationEndpoint: String = "/revocation",
    /**
     * アクセストークン作成エンドポイント。
     */
    val createAccessTokenEndpoint: String = "/token/new",
    /**
     * OAuth 2.0 Authorization Server Metadata のエンドポイント。
     */
    val authorizationServerMetadataEndpoint: String = "/.well-known/oauth-authorization-server",
    /**
     * OpenID Connect Discovery のエンドポイント。
     */
    val openIdProviderConfigurationEndpoint: String = "/.well-known/openid-configuration",
    /**
     * ログイン画面のエンドポイント。
     */
    val loginEndpoint: String = "/login",
    /**
     * エラー画面のエンドポイント。
     */
    val errorEndpoint: String = "/error",
)
