package myoidcprovider.core.request.token

/**
 * トークンリクエストパラメーター
 */
object TokenRequestParameter {
    /**
     * grant_type (REQUIRED)
     *
     * グラントタイプ
     */
    const val GRANT_TYPE = "grant_type"

    /**
     * code (OPTIONAL)
     *
     * 認可コード
     */
    const val CODE = "code"

    /**
     * redirect_uri (OPTIONAL)
     *
     * リダイレクト URI
     */
    const val REDIRECT_URI = "redirect_uri"

    /**
     * client_id
     *
     * クライアント ID（コンフィデンシャルクライアントの場合、必須）
     */
    const val CLIENT_ID = "client_id"

    /**
     * refresh_token
     *
     * リフレッシュトークン（リフレッシュトークングラントの場合、必須）
     */
    const val REFRESH_TOKEN = "refresh_token"

    /**
     * scope (OPTIONAL)
     *
     * スコープ
     */
    const val SCOPE = "scope"

    /**
     * code_verifier (PKCE)
     *
     * コードベリファイア（PKCE が有効な場合、必須）
     */
    const val CODE_VERIFIER = "code_verifier"

    /**
     * client_secret
     *
     * クライアントシークレット。クライアント認証の `client_secret_post` で使用する。
     */
    const val CLIENT_SECRET = "client_secret"
}
