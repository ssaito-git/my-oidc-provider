package myoidcprovider.ktor.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import myoidcprovider.core.request.token.TokenRequestError

/**
 * トークンエラーレスポンス
 *
 * @property error エラー（必須）
 * @property errorDescription エラーの追加情報（任意）
 * @property errorUri エラーに関する web ページの URI（任意）
 */
@Serializable
data class TokenErrorResponseJson(
    @SerialName("error") val error: String,
    @SerialName("error_description") val errorDescription: String?,
    @SerialName("error_uri") val errorUri: String?,
) {
    companion object {
        /**
         * エラーレスポンスからレスポンスを生成する。
         *
         * @param errorResponse エラーレスポンス
         * @return トークンエラーレスポンス
         */
        fun from(errorResponse: TokenRequestError.ErrorResponse) =
            TokenErrorResponseJson(
                errorResponse.error.value,
                errorResponse.errorDescription,
                errorResponse.errorUri,
            )
    }
}
