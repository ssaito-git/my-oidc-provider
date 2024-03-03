package myoidcprovider.core.request.token

/**
 * トークンリクエストのエラーコード。
 */
enum class TokenErrorCode(
    /**
     * パラメーター値。
     */
    val value: String,
) {
    /**
     * invalid_request
     *
     * 必須パラメーターが不足している。無効なパラメーター値が含まれる。パラメーターが重複している。複数のクレデンシャルが含まれている。その他の形式が誤っている。
     */
    INVALID_REQUEST("invalid_request"),

    /**
     * invalid_client
     *
     * クライアント認証に失敗した。
     */
    INVALID_CLIENT("invalid_client"),

    /**
     * invalid_grant
     *
     * 認可コードまたはリフレッシュトークンが不正、有効期限切れ、失効している、認可リクエストのリダイレクト URI と一致しない。他のクライアントに発行されたもの。
     */
    INVALID_GRANT("invalid_grant"),

    /**
     * unauthorized_client
     *
     * クライアントが `grant_type` をサポートしていない。
     */
    UNAUTHORIZED_CLIENT("unauthorized_client"),

    /**
     * unsupported_grant_type
     *
     * 認可サーバーが `grant_type` をサポートしていない。
     */
    UNSUPPORTED_GRANT_TYPE("unsupported_grant_type"),

    /**
     * invalid_scope
     *
     * リクエストされたスコープが無効、不明、または不正な形式。リソースオーナーが許可した範囲を超えている。
     */
    INVALID_SCOPE("invalid_scope"),
}
