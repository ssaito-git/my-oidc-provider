package myoidcprovider.core.client.authentication

/**
 * クライアント認証エラー
 */
sealed interface ClientAuthenticationError {
    /**
     * 認証方式が一致しない
     */
    data object UnmatchedAuthenticationMethod : ClientAuthenticationError

    /**
     * 無効なリクエスト
     *
     * リクエストに複数のクライアント認証方式のパラメーターが含まれるなど。
     */
    data class InvalidRequest(
        /**
         * エラーの追加情報
         */
        val errorDescription: String? = null,
    ) : ClientAuthenticationError

    /**
     * 無効なクレデンシャル
     */
    data object InvalidCredentials : ClientAuthenticationError
}
