package myoidcprovider.ktor.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import myoidcprovider.core.metadata.OidcMetadata

/**
 * OpenID Provider Metadata レスポンス
 *
 * @property issuer 発行者（必須）
 * @property authorizationEndpoint 認可エンドポイント（必須）
 * @property tokenEndpoint トークンエンドポイント（必須）
 * @property userinfoEndpoint UserInfo エンドポイント（推奨）
 * @property jwksUri JWKs エンドポイント（必須）
 * @property registrationEndpoint OpenID Connect 動的クライアント登録エンドポイント（推奨）
 * @property scopesSupported OpenID Provider がサポートするスコープ（推奨）
 * @property responseTypesSupported OpenID Provider がサポートする response_type のリスト（必須）
 * @property responseModesSupported OpenID Provider がサポートする response_mode のリスト（任意）
 * @property grantTypesSupported OpenID Provider がサポートする許可タイプのリスト（任意）
 * @property acrValuesSupported OpenID Provider がサポートする認証コンテキストクラスリファレンスのリスト（任意）
 * @property subjectTypesSupported OpenID Provider がサポートする Subject Identifier Types のリスト（必須）
 * @property idTokenSigningAlgValuesSupported OpenID Provider がサポートする JWS 署名アルゴリズム（alg）のリスト（必須）
 * @property idTokenEncryptionAlgValuesSupported OpenID Provider がサポートする JWE 暗号化アルゴリズム（alg）のリスト（任意）
 * @property idTokenEncryptionEncValuesSupported OpenID Provider がサポートする JWE 暗号化アルゴリズム（enc）のリスト（任意）
 * @property userinfoSigningAlgValuesSupported UserInfo エンドポイントがサポートする JWS 署名アルゴリズム（alg）のリスト（任意）
 * @property userinfoEncryptionAlgValuesSupported UserInfo エンドポイントがサポートする JWE 暗号化アルゴリズム（alg）のリスト（任意）
 * @property userinfoEncryptionEncValuesSupported UserInfo エンドポイントがサポートする JWE 暗号化アルゴリズム（enc）のリスト（任意）
 * @property requestObjectSigningAlgValuesSupported 認可リクエストのリクエストオブジェクトでサポートする JWS 署名アルゴリズム（alg）のリスト（任意）
 * @property requestObjectEncryptionAlgValuesSupported 認可リクエストのリクエストオブジェクトでサポートする JWE 暗号化アルゴリズム（alg）のリスト（任意）
 * @property requestObjectEncryptionEncValuesSupported 認可リクエストのリクエストオブジェクトでサポートする JWE 暗号化アルゴリズム（enc）のリスト（任意）
 * @property tokenEndpointAuthMethodsSupported トークンエンドポイントでサポートするクライアント認証方法のリスト（任意）
 * @property tokenEndpointAuthSigningAlgValuesSupported
 * トークンエンドポイントがサポートする認証方法（private_key_jwt, client_secret_jwt）で使用する署名アルゴリズム（任意）
 * @property displayValuesSupported OpenID Provider がサポートする認証および同意のためのユーザーインタフェースのリスト（任意）
 * @property claimTypesSupported OpenID Provider がサポートする Claim タイプのリスト（任意）
 * @property claimsSupported OpenID Provider がサポートする Claim のリスト（推奨）
 * @property serviceDocumentation OpenID Provider が公開しているドキュメントの URL（任意）
 * @property claimsLocalesSupported Claim の値でサポートする言語のリスト（任意）
 * @property uiLocalesSupported ユーザーインタフェースでサポートする言語のリスト（任意）
 * @property claimsParameterSupported OpenID Provider が認証リクエストの `claim` パラメーターをサポートするかどうかを指定するブール値（任意）
 * @property requestParameterSupported OpenID Provider が認可リクエストの `request` パラメーターをサポートするかどうかを指定するブール値（任意）
 * @property requestUriParameterSupported OpenID Provider が認可リクエストの `request_uri` パラメーターをサポートするかどうかを指定するブール値（任意）
 * @property requireRequestUriRegistration 事前登録済みの `request_uri` 以外の利用をサポートするかどうかを指定するブール値（任意）
 * @property opPolicyUri クライアントに対するポリシーのドキュメントの URL（任意）
 * @property opTosUri クライアントに対する利用規約のドキュメントの URL（任意）
 */
@Serializable
data class OidcMetadataJson(
    @SerialName("issuer") val issuer: String,
    @SerialName("authorization_endpoint") val authorizationEndpoint: String,
    @SerialName("token_endpoint") val tokenEndpoint: String,
    @SerialName("userinfo_endpoint") val userinfoEndpoint: String?,
    @SerialName("jwks_uri") val jwksUri: String,
    @SerialName("registration_endpoint") val registrationEndpoint: String?,
    @SerialName("scopes_supported") val scopesSupported: List<String>?,
    @SerialName("response_types_supported") val responseTypesSupported: List<String>,
    @SerialName("response_modes_supported") val responseModesSupported: List<String>?,
    @SerialName("grant_types_supported") val grantTypesSupported: List<String>?,
    @SerialName("acr_values_supported") val acrValuesSupported: List<String>?,
    @SerialName("subject_types_supported") val subjectTypesSupported: List<String>,
    @SerialName("id_token_signing_alg_values_supported") val idTokenSigningAlgValuesSupported: List<String>,
    @SerialName("id_token_encryption_alg_values_supported") val idTokenEncryptionAlgValuesSupported: List<String>?,
    @SerialName("id_token_encryption_enc_values_supported") val idTokenEncryptionEncValuesSupported: List<String>?,
    @SerialName("userinfo_signing_alg_values_supported") val userinfoSigningAlgValuesSupported: List<String>?,
    @SerialName("userinfo_encryption_alg_values_supported") val userinfoEncryptionAlgValuesSupported: List<String>?,
    @SerialName("userinfo_encryption_enc_values_supported") val userinfoEncryptionEncValuesSupported: List<String>?,
    @SerialName("request_object_signing_alg_values_supported") val requestObjectSigningAlgValuesSupported:
    List<String>?,
    @SerialName("request_object_encryption_alg_values_supported") val requestObjectEncryptionAlgValuesSupported:
    List<String>?,
    @SerialName("request_object_encryption_enc_values_supported") val requestObjectEncryptionEncValuesSupported:
    List<String>?,
    @SerialName("token_endpoint_auth_methods_supported") val tokenEndpointAuthMethodsSupported: List<String>?,
    @SerialName(
        "l tokenEndpointAuthMethodsSupported: List<TokenEndpointAuthMethod>?,",
    ) val tokenEndpointAuthSigningAlgValuesSupported:
    List<String>?,
    @SerialName("token_endpoint_auth_signing_alg_values_supported") val displayValuesSupported: List<String>?,
    @SerialName("display_values_supported") val claimTypesSupported: List<String>?,
    @SerialName("claim_types_supported") val claimsSupported: List<String>?,
    @SerialName("claims_supported") val serviceDocumentation: String?,
    @SerialName("service_documentation") val claimsLocalesSupported: List<String>?,
    @SerialName("claims_locales_supported") val uiLocalesSupported: List<String>?,
    @SerialName("ui_locales_supported") val claimsParameterSupported: Boolean?,
    @SerialName("claims_parameter_supported") val requestParameterSupported: Boolean?,
    @SerialName("request_parameter_supported") val requestUriParameterSupported: Boolean?,
    @SerialName("request_uri_parameter_supported") val requireRequestUriRegistration: Boolean?,
    @SerialName("require_request_uri_registration") val opPolicyUri: String?,
    @SerialName("op_policy_uri") val opTosUri: String?,
) {
    companion object {
        /**
         * OpenID Provider Metadata からレスポンスを生成する。
         *
         * @param metadata OpenID Provider Metadata
         * @return OpenID Provider Metadata レスポンス
         */
        fun from(metadata: OidcMetadata) =
            OidcMetadataJson(
                metadata.issuer,
                metadata.authorizationEndpoint,
                metadata.tokenEndpoint,
                metadata.userinfoEndpoint,
                metadata.jwksUri,
                metadata.registrationEndpoint,
                metadata.scopesSupported,
                metadata.responseTypesSupported.map { it.joinToString(" ") { responseType -> responseType.value } },
                metadata.responseModesSupported?.map { it.value },
                metadata.grantTypesSupported?.map { it.value },
                metadata.acrValuesSupported,
                metadata.subjectTypesSupported.map { it.value },
                metadata.idTokenSigningAlgValuesSupported.map { it.value },
                metadata.idTokenEncryptionAlgValuesSupported?.map { it.value },
                metadata.idTokenEncryptionEncValuesSupported?.map { it.value },
                metadata.userinfoSigningAlgValuesSupported?.map { it.value },
                metadata.userinfoEncryptionAlgValuesSupported?.map { it.value },
                metadata.userinfoEncryptionEncValuesSupported?.map { it.value },
                metadata.requestObjectSigningAlgValuesSupported?.map { it.value },
                metadata.requestObjectEncryptionAlgValuesSupported?.map { it.value },
                metadata.requestObjectEncryptionEncValuesSupported?.map { it.value },
                metadata.tokenEndpointAuthMethodsSupported?.map { it.value },
                metadata.tokenEndpointAuthSigningAlgValuesSupported?.map { it.value },
                metadata.displayValuesSupported?.map { it.value },
                metadata.claimTypesSupported?.map { it.value },
                metadata.claimsSupported,
                metadata.serviceDocumentation,
                metadata.claimsLocalesSupported,
                metadata.uiLocalesSupported,
                metadata.claimsParameterSupported,
                metadata.requestParameterSupported,
                metadata.requestUriParameterSupported,
                metadata.requireRequestUriRegistration,
                metadata.opPolicyUri,
                metadata.opTosUri,
            )
    }
}
