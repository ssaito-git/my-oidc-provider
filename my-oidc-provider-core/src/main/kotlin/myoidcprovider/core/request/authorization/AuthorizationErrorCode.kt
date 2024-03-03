package myoidcprovider.core.request.authorization

/**
 * 認可リクエストのエラーレスポンスのエラーコード
 *
 * [RFC6749 The OAuth 2.0 Authorization Framework - 5.2. Error Response](https://datatracker.ietf.org/doc/html/rfc6749#section-5.2)
 */
enum class AuthorizationErrorCode(
    /**
     * パラメーター値。
     */
    val value: String,
) {
    /**
     * invalid_request
     *
     * 必須パラメーターが不足している。無効なパラメーター値が含まれる。パラメーターが 2 回以上含まれている。その他の形式が誤っている。
     */
    INVALID_REQUEST("invalid_request"),

    /**
     * authorized_client
     *
     * クライアントが認可コードを許可されていない。
     */
    AUTHORIZED_CLIENT("authorized_client"),

    /**
     * access_denied
     *
     * リソース所有者または認可サーバーが要求を拒否した。
     */
    ACCESS_DENIED("access_denied"),

    /**
     * unsupported_response_type
     *
     * 認可サーバーは `response_type` で指定された値をサポートしていない。
     */
    UNSUPPORTED_RESPONSE_TYPE("unsupported_response_type"),

    /**
     * invalid_scope
     *
     * リクエストされたスコープが無効、不明、または不正な形式。
     */
    INVALID_SCOPE("invalid_scope"),

    /**
     * server_error
     *
     * 認可サーバーで予期しないエラーが発生した。
     */
    SERVER_ERROR("server_error"),

    /**
     * temporary_unavailable
     *
     * 認可サーバーがリクエストを処理できない。
     */
    TEMPORARY_UNAVAILABLE("temporary_unavailable"),
}
