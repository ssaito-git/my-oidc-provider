package myoidcprovider.core.request.authorization

/**
 * 認証リクエストのエラーレスポンスのエラーコード
 *
 * [OpenID Connect Core 1.0 - 3.1.2.6. Authentication Error Response](https://openid.net/specs/openid-connect-core-1_0.html#AuthError)
 */
enum class AuthenticationErrorCode(
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

    /**
     * interaction_required
     *
     * エンドユーザーに UI を表示する必要があるが、`prompt` に `none` が指定されている。
     */
    INTERACTION_REQUIRED("interaction_required"),

    /**
     * login_required
     *
     * エンドユーザーの認証のために UI を表示する必要があるが、`prompt` に `none` が指定されている。
     */
    LOGIN_REQUIRED("login_required"),

    /**
     * account_selection_required
     *
     * エンドユーザーがセッションを選択するために UI を表示する必要があるが、`prompt` に `none` が指定されている。
     */
    ACCOUNT_SELECTION_REQUIRED("account_selection_required"),

    /**
     * consent_required
     *
     * エンドユーザーが同意するために UI を表示する必要があるが、`prompt` に `none` が指定されている。
     */
    CONSENT_REQUIRED("consent_required"),

    /**
     * invalid_request_uri
     *
     * `request_uri` がエラーを返すか無効なデータを含む。
     */
    INVALID_REQUEST_URI("invalid_request_uri"),

    /**
     * invalid_request_object
     *
     * `request` が無効な Request Object を含む。
     */
    INVALID_REQUEST_OBJECT("invalid_request_object"),

    /**
     * request_not_supported
     *
     * `request` をサポートしていない。
     */
    REQUEST_NOT_SUPPORTED("request_not_supported"),

    /**
     * request_uri_not_supported
     *
     * `request_uri` をサポートしていない。
     */
    REQUEST_URI_NOT_SUPPORTED("request_uri_not_supported"),

    /**
     * registration_not_supported
     *
     * `registration` をサポートしていない。
     */
    REGISTRATION_NOT_SUPPORTED("registration_not_supported"),
}
