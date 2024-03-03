package myoidcprovider.core.metadata

/**
 * Proof Key for Code Exchange (PKCE) でサポートされるコードチャレンジメソッド。
 */
enum class PKCECodeChallengeMethod(
    /**
     * パラメーター値。
     */
    val value: String,
) {
    /**
     * plain
     *
     * プレーン
     */
    PLAIN("plain"),

    /**
     * S256
     *
     * SHA-256
     */
    S256("S256"),
}
