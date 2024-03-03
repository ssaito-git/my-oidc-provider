package myoidcprovider.core.metadata

/**
 * Response Type
 */
enum class ResponseType(
    /**
     * パラメーター値。
     */
    val value: String,
) {
    /**
     * code
     */
    CODE("code"),

    /**
     * token
     */
    TOKEN("token"),

    /**
     * id_token
     */
    ID_TOKEN("id_token"),
}
