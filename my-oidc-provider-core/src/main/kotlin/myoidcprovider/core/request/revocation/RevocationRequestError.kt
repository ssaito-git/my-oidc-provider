package myoidcprovider.core.request.revocation

/**
 * リヴォケーションリクエストのエラー。
 */
interface RevocationRequestError {
    /**
     * エラーレスポンス
     */
    data class ErrorResponse(
        /**
         * error (REQUIRED)
         *
         * エラー（必須）
         */
        val error: RevocationErrorCode,
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
    ) : RevocationRequestError
}
