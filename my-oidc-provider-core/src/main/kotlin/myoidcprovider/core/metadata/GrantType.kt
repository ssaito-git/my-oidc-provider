package myoidcprovider.core.metadata

/**
 * Grant Type
 */
enum class GrantType(
    /**
     * パラメーター値。
     */
    val value: String,
) {
    /**
     * 認可コードグラント
     */
    AUTHORIZATION_CODE("authorization_code"),

    /**
     * インプリシットグラント
     */
    IMPLICIT("implicit"),

    /**
     * リソースオーナーパスワードクレデンシャルズ
     */
    PASSWORD("password"),

    /**
     * クライアントクレデンシャルズグラント
     */
    CLIENT_CREDENTIALS("client_credentials"),

    /**
     * リフレッシュトークングラント
     */
    REFRESH_TOKEN("refresh_token"),

    /**
     * JWT 認可グラント
     */
    JWT_BEARER("jwt_bearer"),

    /**
     * SAML 2.0 認可グラント
     */
    SAML2_BEARER("saml2_bearer"),

    /**
     * OAuth 2.0 Token Exchange
     * [RFC 8693 - OAuth 2.0 Token Exchange](https://datatracker.ietf.org/doc/html/rfc8693)
     */
    TOKEN_EXCHANGE("urn:ietf:params:oauth:grant-type:token-exchange"),
}
