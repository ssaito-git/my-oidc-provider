package myoidcprovider.core.request.authorization

/**
 * 認可レスポンスのエラー。
 */
sealed interface AuthorizationResponseError {
    /**
     * エラーレスポンス。
     */
    data class ErrorResponse(
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
    ) : AuthorizationResponseError

    /**
     * リクエストが不正。
     */
    data object InvalidRequest : AuthorizationResponseError
}
