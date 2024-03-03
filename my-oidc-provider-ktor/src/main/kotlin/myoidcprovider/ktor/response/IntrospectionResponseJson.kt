package myoidcprovider.ktor.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import myoidcprovider.core.request.introspection.IntrospectionResponse

/**
 * イントロスペクションレスポンス
 *
 * @property active トークンの状態（必須）
 * @property scope スコープ（任意）
 * @property clientId トークンを要求したクライアントの ID（任意）
 * @property username トークンを承認したリソースオーナーの名前（任意）
 * @property tokenType トークンタイプ（任意）
 * @property exp トークンの有効期限（秒）（任意）
 * @property iat トークンを発行した日時（秒）（任意）
 * @property nbf トークンが有効になる日時（秒）（任意）
 * @property sub トークンを承認したリソースオーナーの識別子（任意）
 * @property aud トークンの利用者の識別子（任意）
 * @property iss トークンの発行者（任意）
 * @property jti トークンの一意な識別子（任意）
 */
@Serializable
data class IntrospectionResponseJson(
    @SerialName("active") val active: Boolean,
    @SerialName("scope") val scope: String?,
    @SerialName("client_id") val clientId: String?,
    @SerialName("username") val username: String?,
    @SerialName("token_type") val tokenType: String?,
    @SerialName("exp") val exp: Long?,
    @SerialName("iat") val iat: Long?,
    @SerialName("nbf") val nbf: Long?,
    @SerialName("sub") val sub: String?,
    @SerialName("aud") val aud: String?,
    @SerialName("iss") val iss: String?,
    @SerialName("jti") val jti: String?,
) {
    companion object {
        /**
         * イントロスペクションレスポンスからレスポンスを生成する。
         *
         * @param introspectionResponse イントロスペクションレスポンス
         * @return イントロスペクションレスポンス
         */
        fun from(introspectionResponse: IntrospectionResponse) =
            IntrospectionResponseJson(
                introspectionResponse.active,
                introspectionResponse.scope?.joinToString(" "),
                introspectionResponse.clientId,
                introspectionResponse.username,
                introspectionResponse.tokenType?.value,
                introspectionResponse.exp,
                introspectionResponse.iat,
                introspectionResponse.nbf,
                introspectionResponse.sub,
                introspectionResponse.aud,
                introspectionResponse.iss,
                introspectionResponse.jti,
            )
    }
}
