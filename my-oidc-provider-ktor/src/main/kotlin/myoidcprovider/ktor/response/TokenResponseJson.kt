package myoidcprovider.ktor.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import myoidcprovider.core.request.token.TokenResponse

/**
 * トークンレスポンス
 *
 * @property accessToken アクセストークン
 * @property tokenType トークンタイプ
 * @property expiresIn アクセストークン有効期限
 * @property refreshToken リフレッシュトークン
 * @property scope スコープ（認可リクエストのスコープと異なる場合は必須。それ以外は任意）
 * @property idToken ID トークン（場合、必須）
 */
@Serializable
data class TokenResponseJson(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Long?,
    @SerialName("refresh_token") val refreshToken: String?,
    @SerialName("scope") val scope: String?,
    @SerialName("id_token") val idToken: String?,
) {
    companion object {
        /**
         * トークンレスポンスからレスポンスを生成する。
         *
         * @param tokenResponse トークンレスポンス
         * @return トークンレスポンス
         */
        fun from(tokenResponse: TokenResponse) =
            TokenResponseJson(
                tokenResponse.accessToken,
                tokenResponse.tokenType.value,
                tokenResponse.expiresIn,
                tokenResponse.refreshToken,
                tokenResponse.scope?.joinToString(" "),
                tokenResponse.idToken,
            )
    }
}
