package myoidcprovider.ktor.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import myoidcprovider.core.metadata.OAuth2Metadata

/**
 * OAuth 2.0 Authorization Server Metadata レスポンス
 *
 * @property issuer 発行者（必須）
 * @property authorizationEndpoint 認可エンドポイント（必須）
 * @property tokenEndpoint トークンエンドポイント（必須）
 * @property jwksUri JWKs エンドポイント（任意）
 * @property registrationEndpoint OAuth 2.0 動的クライアント登録エンドポイント RFC7591（任意）
 * @property scopesSupported 認可サーバーがサポートするスコープ（推奨）
 * @property responseTypesSupported 認可サーバーがサポートする response_type のリスト（必須）
 * @property responseModesSupported 認可サーバーがサポートする response_mode のリスト（任意）
 * @property grantTypesSupported 認可サーバーがサポートする許可タイプのリスト（任意）
 * @property tokenEndpointAuthMethodsSupported トークンエンドポイントでサポートするクライアント認証方法のリスト（任意）
 * @property tokenEndpointAuthSigningAlgValuesSupported
 * トークンエンドポイントがサポートする認証方法（private_key_jwt, client_secret_jwt）で使用する署名アルゴリズム（任意）
 * @property serviceDocumentation 認可サーバーが公開しているドキュメントの URL（任意）
 * @property uiLocalesSupported ユーザーインタフェースでサポートする言語のリスト（任意）
 * @property opPolicyUri 認可サーバーがクライアントを登録する人に提供する URL（任意）
 * @property opTosUri 認可サーバーがクライアントを登録する人に提供する利用規約の URL（任意）
 * @property revocationEndpoint トークン無効化エンドポイント（任意）
 * @property revocationEndpointAuthMethodsSupported トークン無効化エンドポイントでサポートするクライアント認証方法のリスト（任意）
 * @property revocationEndpointAuthSigningAlgValuesSupported
 * トークン無効化エンドポイントがサポートする認証方法（private_key_jwt, client_secret_jwt）で使用する署名アルゴリズム（任意）
 * @property introspectionEndpoint イントロスペクションエンドポイント（任意）
 * @property introspectionEndpointAuthMethodsSupported イントロスペクションエンドポイントでサポートするクライアント認証方法のリスト（任意）
 * @property introspectionEndpointAuthSigningAlgValuesSupported
 * イントロスペクションエンドポイントがサポートする認証方法（private_key_jwt, client_secret_jwt）で使用する署名アルゴリズム（任意）
 * @property codeChallengeMethodsSupported PKCE でサポートするコードチャレンジメソッドのリスト（任意）
 */
@Serializable
data class OAuth2MetadataJson(
    @SerialName("issuer") val issuer: String,
    @SerialName("authorization_endpoint") val authorizationEndpoint: String,
    @SerialName("token_endpoint") val tokenEndpoint: String,
    @SerialName("jwks_uri") val jwksUri: String? = null,
    @SerialName("registration_endpoint") val registrationEndpoint: String?,
    @SerialName("scopes_supported") val scopesSupported: List<String>?,
    @SerialName("response_types_supported") val responseTypesSupported: List<String>,
    @SerialName("response_modes_supported") val responseModesSupported: List<String>?,
    @SerialName("grant_types_supported") val grantTypesSupported: List<String>?,
    @SerialName("token_endpoint_auth_methods_supported") val tokenEndpointAuthMethodsSupported: List<String>?,
    @SerialName(
        "token_endpoint_auth_signing_alg_values_supported",
    ) val tokenEndpointAuthSigningAlgValuesSupported: List<String>?,
    @SerialName("service_documentation") val serviceDocumentation: String?,
    @SerialName("ui_locales_supported") val uiLocalesSupported: List<String>?,
    @SerialName("op_policy_uri") val opPolicyUri: String?,
    @SerialName("op_tos_uri") val opTosUri: String?,
    @SerialName("revocation_endpoint") val revocationEndpoint: String?,
    @SerialName("revocation_endpoint_auth_methods_supported") val revocationEndpointAuthMethodsSupported: List<String>?,
    @SerialName(
        "revocation_endpoint_auth_signing_alg_values_supported",
    ) val revocationEndpointAuthSigningAlgValuesSupported: List<String>?,
    @SerialName("introspection_endpoint") val introspectionEndpoint: String?,
    @SerialName(
        "introspection_endpoint_auth_methods_supported",
    ) val introspectionEndpointAuthMethodsSupported: List<String>?,
    @SerialName(
        "introspection_endpoint_auth_signing_alg_values_supported",
    ) val introspectionEndpointAuthSigningAlgValuesSupported: List<String>?,
    @SerialName("code_challenge_methods_supported") val codeChallengeMethodsSupported: List<String>?,
) {
    companion object {
        /**
         * OAuth 2.0 Authorization Server Metadata からレスポンスを生成する。
         *
         * @param metadata OAuth 2.0 Authorization Server Metadata
         * @return OAuth 2.0 Authorization Server Metadata レスポンス
         */
        fun from(metadata: OAuth2Metadata) =
            OAuth2MetadataJson(
                metadata.issuer,
                metadata.authorizationEndpoint,
                metadata.tokenEndpoint,
                metadata.jwksUri,
                metadata.registrationEndpoint,
                metadata.scopesSupported,
                metadata.responseTypesSupported.map { it.value },
                metadata.responseModesSupported?.map { it.value },
                metadata.grantTypesSupported?.map { it.value },
                metadata.tokenEndpointAuthMethodsSupported?.map { it.value },
                metadata.tokenEndpointAuthSigningAlgValuesSupported?.map { it.value },
                metadata.serviceDocumentation,
                metadata.uiLocalesSupported,
                metadata.opPolicyUri,
                metadata.opTosUri,
                metadata.revocationEndpoint,
                metadata.revocationEndpointAuthMethodsSupported?.map { it.value },
                metadata.revocationEndpointAuthSigningAlgValuesSupported?.map { it.value },
                metadata.introspectionEndpoint,
                metadata.introspectionEndpointAuthMethodsSupported?.map { it.value },
                metadata.introspectionEndpointAuthSigningAlgValuesSupported?.map { it.value },
                metadata.codeChallengeMethodsSupported?.map { it.value },
            )
    }
}
