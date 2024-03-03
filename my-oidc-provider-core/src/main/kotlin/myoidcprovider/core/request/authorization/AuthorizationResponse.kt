package myoidcprovider.core.request.authorization

import myoidcprovider.core.metadata.ResponseMode
import myoidcprovider.core.metadata.ResponseType
import myoidcprovider.core.metadata.AccessTokenType

/**
 * 認可レスポンス
 */
data class AuthorizationResponse(
    /**
     * `response_type` に `token` が指定された際に返されるパラメーター
     */
    val token: Token?,
    /**
     * `response_type` に `code` が指定された際に返されるパラメーター
     */
    val code: Code?,
    /**
     * `response_type` に `id_token` が指定された際に返されるパラメーター
     */
    val idToken: IdToken?,
    /**
     * リダイレクト URI
     */
    val redirectUri: String,
    /**
     * レスポンスタイプ
     */
    val responseType: List<ResponseType>,
    /**
     * レスポンスモード
     */
    val responseMode: ResponseMode?,
    /**
     * 認可リクエストの state
     */
    val state: String?,
) {
    /**
     * 認可リクエストの `response_type` に `token` が指定された際に認可レスポンスで返されるパラメーター
     */
    data class Token(
        /**
         * アクセストークン
         */
        val accessToken: String,
        /**
         * トークンタイプ
         */
        val tokenType: AccessTokenType,
        /**
         * 有効期間（秒）
         */
        val expiresIn: Long?,
        /**
         * スコープ
         */
        val scope: String?,
    )

    /**
     * 認可リクエストの `response_type` に `code` が指定された際に認可レスポンスで返されるパラメーター
     */
    data class Code(
        /**
         * 認可コード
         */
        val code: String,
    )

    /**
     * 認可リクエストの `response_type` に `id_token` が指定された際に認可レスポンスで返されるパラメーター
     */
    data class IdToken(
        /**
         * ID トークン
         */
        val idToken: String,
    )
}
