package myoidcprovider.core.request.introspection

/**
 * イントロスペクションリクエストのエラー。
 */
interface IntrospectionRequestError {
    /**
     * エラーレスポンス
     */
    data class ErrorResponse(
        /**
         * error (REQUIRED)
         *
         * エラー（必須）
         */
        val error: IntrospectionErrorCode,
        /**
         * error_description (OPTIONAL)
         *
         * エラーの追加情報
         */
        val errorDescription: String? = null,
    ) : IntrospectionRequestError
}
