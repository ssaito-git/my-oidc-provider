package myoidcprovider.core.issuer

import myoidcprovider.core.metadata.GrantType
import myoidcprovider.core.metadata.PKCECodeChallengeMethod
import myoidcprovider.core.metadata.ResponseType

/**
 * イシュアーの設定
 */
data class IssuerConfig(
    /**
     * イシュアー。
     */
    val issuer: String,
    /**
     * 許可されているスコープ。
     */
    val scopes: List<String>,
    /**
     * サポートしているレスポンスタイプ。
     */
    val supportedResponseTypes: List<ResponseType>,
    /**
     * サポートしているグラントタイプ。
     */
    val supportedGrantTypes: List<GrantType>,
    /**
     * サポートしている PKCE コードチャレンジメソッド。
     */
    val supportedCodeChallengeMethods: List<PKCECodeChallengeMethod>,
    /**
     * PKCE 利用の必須有無。
     */
    val requiredPKCE: Boolean,
    /**
     * 認可リクエスト有効期間（秒）。
     */
    val authorizationRequestDataDuration: Long,
    /**
     * 認可コード有効期間（秒）。
     */
    val authorizationCodeDuration: Long,
    /**
     * アクセストークン有効期間（秒）。
     */
    val accessTokenDuration: Long,
    /**
     * リフレッシュトークン有効期間（秒）。
     */
    val refreshTokenDuration: Long,
    /**
     * ID トークン有効期間（秒）。
     */
    val idTokenDuration: Long,
)
