package myoidcprovider.core.request.authorization

/**
 * 認可リクエストのエラー
 */
sealed interface AuthorizationRequestError

/**
 * 認可リクエストのエラーレスポンス
 *
 * [RFC6749 The OAuth 2.0 Authorization Framework - 4.1.2.1. Error Response](https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1)
 */
data class AuthorizationErrorResponse(
    /**
     * redirect_uri
     *
     * エラー通知先のリダイレクト URI。
     */
    val redirectUri: String,

    /**
     * error (REQUIRED)
     *
     * エラーコード。
     */
    val error: AuthorizationErrorCode,

    /**
     * error_description (OPTIONAL)
     *
     * エラーの追加情報。
     */
    val errorDescription: String? = null,

    /**
     * error_uri (OPTIONAL)
     *
     * エラーに関する web ページの URI。
     */
    val errorUri: String? = null,

    /**
     * state
     *
     * 認可リクエストの state パラメーター（state パラメーターが含まれる場合は必須）
     */
    val state: String? = null,
) : AuthorizationRequestError

/**
 * クライアントが不正
 */
data class InvalidClient(
    /**
     * error_description (OPTIONAL)
     *
     * エラーの追加情報。
     */
    val errorDescription: String? = null,
) : AuthorizationRequestError

/**
 * リダイレクト URI が不正
 */
data class InvalidRedirectUri(
    /**
     * error_description (OPTIONAL)
     *
     * エラーの追加情報。
     */
    val errorDescription: String? = null,
) : AuthorizationRequestError

/**
 * 認証リクエストのエラーレスポンス
 *
 * [OpenID Connect Core 1.0 - 3.1.2.6. Authentication Error Response](https://openid.net/specs/openid-connect-core-1_0.html#AuthError)
 */
data class AuthenticationErrorResponse(
    /**
     * redirect_uri
     */
    val redirectUri: String,

    /**
     * error (REQUIRED)
     *
     * エラーコード
     */
    val error: AuthenticationErrorCode,

    /**
     * error_description (OPTIONAL)
     *
     * エラーの追加情報
     */
    val errorDescription: String? = null,

    /**
     * error_uri (OPTIONAL)
     *
     * エラーに関する web ページの URI
     */
    val errorUri: String? = null,

    /**
     * state
     *
     * 認可リクエストの `state` パラメーター（`state` パラメーターが含まれる場合は必須）
     */
    val state: String? = null,
) : AuthorizationRequestError
