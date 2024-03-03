package myoidcprovider.core.client

import myoidcprovider.core.metadata.GrantType

/**
 * クライアントの設定。
 */
data class ClientConfig(
    /**
     * ID。
     */
    val id: String,
    /**
     * 名前。
     */
    val name: String,
    /**
     * クライアントシークレット。
     */
    val secret: String,
    /**
     * クライアントタイプ。
     */
    val type: ClientType,
    /**
     * 許可されているスコープ。
     */
    val scopes: List<String>,
    /**
     * サポートしているグラントタイプ。
     */
    val supportedGrantTypes: List<GrantType>,
    /**
     * 許可されているリダイレクト URI。
     */
    val redirectUris: List<String>,
    /**
     * 認可リクエスト有効期間（秒）。
     */
    val authorizationRequestDataDuration: Long?,
    /**
     * 認可コード有効期間（秒）。
     */
    val authorizationCodeDuration: Long?,
    /**
     * アクセストークン有効期間（秒）。
     */
    val accessTokenDuration: Long?,
    /**
     * リフレッシュトークン有効期間（秒）。
     */
    val refreshTokenDuration: Long?,
    /**
     * ID トークン有効期間（秒）。
     */
    val idTokenDuration: Long?,
)
