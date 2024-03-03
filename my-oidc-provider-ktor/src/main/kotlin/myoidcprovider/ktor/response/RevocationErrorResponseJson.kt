package myoidcprovider.ktor.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import myoidcprovider.core.request.revocation.RevocationRequestError

/**
 * リヴォケーションエラーレスポンス
 *
 * @property error エラー（必須）
 * @property errorDescription エラーの追加情報
 * @property errorUri エラーに関する web ページの URI
 */
@Serializable
data class RevocationErrorResponseJson(
    @SerialName("error") val error: String,
    @SerialName("error_description") val errorDescription: String?,
    @SerialName("error_uri") val errorUri: String?,
) {
    companion object {
        /**
         * エラーレスポンスからレスポンスを生成する。
         *
         * @param errorResponse エラーレスポンス
         * @return リヴォケーションエラーレスポンス
         */
        fun from(errorResponse: RevocationRequestError.ErrorResponse) =
            RevocationErrorResponseJson(
                errorResponse.error.value,
                errorResponse.errorDescription,
                errorResponse.errorUri,
            )
    }
}
