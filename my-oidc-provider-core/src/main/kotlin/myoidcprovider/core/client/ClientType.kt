package myoidcprovider.core.client

/**
 * クライアントタイプ。
 */
enum class ClientType(
    /**
     * パラメーター値。
     */
    val value: String,
) {
    /**
     * confidential
     */
    CONFIDENTIAL("confidential"),

    /**
     * public
     */
    PUBLIC("public"),
}
