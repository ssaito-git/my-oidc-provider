package myoidcprovider.core.metadata

/**
 * トークンタイプヒント。
 */
enum class TokenTypeHint(
    /**
     * パラメーター値。
     */
    val value: String,
) {
    /**
     * access_token
     *
     * アクセストークン
     */
    ACCESS_TOKEN("access_token"),

    /**
     * refresh_token
     *
     * リフレッシュトークン
     */
    REFRESH_TOKEN("refresh_token"),
}