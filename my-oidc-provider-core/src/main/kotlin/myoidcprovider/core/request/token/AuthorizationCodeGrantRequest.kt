package myoidcprovider.core.request.token

import myoidcprovider.core.metadata.GrantType

/**
 * 認可コードグラントのリクエスト。
 */
data class AuthorizationCodeGrantRequest(
    /**
     * grant_type (REQUIRED)
     *
     * グラントタイプ
     */
    val grantType: GrantType,

    /**
     * code (REQUIRED)
     *
     * 認可コード
     */
    val code: String,

    /**
     * redirect_uri
     *
     * リダイレクト URI（認可リクエストのパラメーターに `redirect_uri` が含まれている場合は必須）
     */
    val redirectUri: String?,

    /**
     * client_id
     *
     * クライアント ID（クライアントが認証されていなければ必須）
     */
    val clientId: String?,

    /**
     * code_verifier (PKCE)
     *
     * コードベリファイア（PKCE が有効な場合、必須）
     */
    val codeVerifier: String?,
)