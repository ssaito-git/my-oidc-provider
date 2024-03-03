package myoidcprovider.ktor.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import myoidcprovider.core.request.introspection.IntrospectionRequestError

/**
 * イントロスペクションエラーレスポンス
 *
 * @property error エラー（必須）
 * @property errorDescription エラーの追加情報
 */
@Serializable
data class IntrospectionErrorResponseJson(
    @SerialName("error") val error: String,
    @SerialName("error_description") val errorDescription: String?,
) {
    companion object {
        /**
         * エラーレスポンスからレスポンスを生成する。
         *
         * @param errorResponse エラーレスポンス
         * @return イントロスペクションエラーレスポンス
         */
        fun from(errorResponse: IntrospectionRequestError.ErrorResponse) =
            IntrospectionErrorResponseJson(
                errorResponse.error.value,
                errorResponse.errorDescription,
            )
    }
}
