package myoidcprovider.core.request.token

/**
 * トークンリクエストのエラー。
 */
sealed interface TokenRequestError {
    /**
     * エラーレスポンス
     */
    data class ErrorResponse(
        /**
         * error (REQUIRED)
         *
         * エラー（必須）
         */
        val error: TokenErrorCode,
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
    ) : TokenRequestError
}
