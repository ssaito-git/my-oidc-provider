package myoidcprovider.core.request.introspection

/**
 * イントロスペクションリクエストのエラーコード。
 */
enum class IntrospectionErrorCode(
    /**
     * パラメーター値。
     */
    val value: String,
) {
    /**
     * invalid_request
     */
    INVALID_REQUEST("invalid_request"),

    /**
     * invalid_client
     */
    INVALID_CLIENT("invalid_client"),
}