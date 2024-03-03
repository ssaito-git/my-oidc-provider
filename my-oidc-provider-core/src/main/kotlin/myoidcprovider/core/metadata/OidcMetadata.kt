package myoidcprovider.core.metadata

/**
 * OpenID Provider Metadata.
 *
 * [OpenID Connect Discovery 1.0](https://openid.net/specs/openid-connect-discovery-1_0.html)
 */
data class OidcMetadata(
    /**
     * issuer
     *
     * 発行者（必須）
     */
    val issuer: String,
    /**
     * authorization_endpoint
     *
     * 認可エンドポイント（必須）
     */
    val authorizationEndpoint: String,
    /**
     * token_endpoint
     *
     * トークンエンドポイント（必須）
     */
    val tokenEndpoint: String,
    /**
     * userinfo_endpoint
     *
     * UserInfo エンドポイント（推奨）
     */
    val userinfoEndpoint: String?,
    /**
     * jwks_uri
     *
     * JWKs エンドポイント（必須）
     */
    val jwksUri: String,
    /**
     * registration_endpoint
     *
     * OpenID Connect 動的クライアント登録エンドポイント（推奨）
     */
    val registrationEndpoint: String?,
    /**
     * scopes_supported
     *
     * OpenID Provider がサポートするスコープ（推奨）
     */
    val scopesSupported: List<String>?,
    /**
     * response_types_supported
     *
     * OpenID Provider がサポートする response_type のリスト（必須）
     */
    val responseTypesSupported: List<List<ResponseType>>,
    /**
     * response_modes_supported
     *
     * OpenID Provider がサポートする response_mode のリスト（任意）
     */
    val responseModesSupported: List<ResponseMode>?,
    /**
     * grant_types_supported
     *
     * OpenID Provider がサポートする許可タイプのリスト（任意）
     */
    val grantTypesSupported: List<GrantType>?,
    /**
     * acr_values_supported
     *
     * OpenID Provider がサポートする認証コンテキストクラスリファレンスのリスト（任意）
     */
    val acrValuesSupported: List<String>?,
    /**
     * subject_types_supported
     *
     * OpenID Provider がサポートする Subject Identifier Types のリスト（必須）
     */
    val subjectTypesSupported: List<SubjectIdentifierTypes>,
    /**
     * id_token_signing_alg_values_supported
     *
     * OpenID Provider がサポートする JWS 署名アルゴリズム（alg）のリスト（必須）
     */
    val idTokenSigningAlgValuesSupported: List<SigningAlgorithms>,
    /**
     * id_token_encryption_alg_values_supported
     *
     * OpenID Provider がサポートする JWE 暗号化アルゴリズム（alg）のリスト（任意）
     */
    val idTokenEncryptionAlgValuesSupported: List<EncryptionAlgorithms>?,
    /**
     * id_token_encryption_enc_values_supported
     *
     * OpenID Provider がサポートする JWE 暗号化アルゴリズム（enc）のリスト（任意）
     */
    val idTokenEncryptionEncValuesSupported: List<EncryptionAlgorithmsENC>?,
    /**
     * userinfo_signing_alg_values_supported
     *
     * UserInfo エンドポイントがサポートする JWS 署名アルゴリズム（alg）のリスト（任意）
     */
    val userinfoSigningAlgValuesSupported: List<SigningAlgorithms>?,
    /**
     * userinfo_encryption_alg_values_supported
     *
     * UserInfo エンドポイントがサポートする JWE 暗号化アルゴリズム（alg）のリスト（任意）
     */
    val userinfoEncryptionAlgValuesSupported: List<EncryptionAlgorithms>?,
    /**
     * userinfo_encryption_enc_values_supported
     *
     * UserInfo エンドポイントがサポートする JWE 暗号化アルゴリズム（enc）のリスト（任意）
     */
    val userinfoEncryptionEncValuesSupported: List<EncryptionAlgorithmsENC>?,
    /**
     * request_object_signing_alg_values_supported
     *
     * 認可リクエストのリクエストオブジェクトでサポートする JWS 署名アルゴリズム（alg）のリスト（任意）
     */
    val requestObjectSigningAlgValuesSupported: List<SigningAlgorithms>?,
    /**
     * request_object_encryption_alg_values_supported
     *
     * 認可リクエストのリクエストオブジェクトでサポートする JWE 暗号化アルゴリズム（alg）のリスト（任意）
     */
    val requestObjectEncryptionAlgValuesSupported: List<EncryptionAlgorithms>?,
    /**
     * request_object_encryption_enc_values_supported
     *
     * 認可リクエストのリクエストオブジェクトでサポートする JWE 暗号化アルゴリズム（enc）のリスト（任意）
     */
    val requestObjectEncryptionEncValuesSupported: List<EncryptionAlgorithmsENC>?,
    /**
     * token_endpoint_auth_methods_supported
     *
     * トークンエンドポイントでサポートするクライアント認証方法のリスト（任意）
     */
    val tokenEndpointAuthMethodsSupported: List<TokenEndpointAuthMethod>?,
    /**
     * token_endpoint_auth_signing_alg_values_supported
     *
     * トークンエンドポイントがサポートする認証方法（private_key_jwt, client_secret_jwt）で使用する署名アルゴリズム（任意）
     */
    val tokenEndpointAuthSigningAlgValuesSupported: List<SigningAlgorithms>?,
    /**
     * display_values_supported
     *
     * OpenID Provider がサポートする認証および同意のためのユーザーインタフェースのリスト（任意）
     */
    val displayValuesSupported: List<Display>?,
    /**
     * claim_types_supported
     *
     * OpenID Provider がサポートする Claim タイプのリスト（任意）
     */
    val claimTypesSupported: List<ClaimTypes>?,
    /**
     * claims_supported
     *
     * OpenID Provider がサポートする Claim のリスト（推奨）
     */
    val claimsSupported: List<String>?,
    /**
     * service_documentation
     *
     * OpenID Provider が公開しているドキュメントの URL（任意）
     */
    val serviceDocumentation: String?,
    /**
     * claims_locales_supported
     *
     * Claim の値でサポートする言語のリスト（任意）
     */
    val claimsLocalesSupported: List<String>?,
    /**
     * ui_locales_supported
     *
     * ユーザーインタフェースでサポートする言語のリスト（任意）
     */
    val uiLocalesSupported: List<String>?,
    /**
     * claims_parameter_supported
     *
     * OpenID Provider が認証リクエストの `claim` パラメーターをサポートするかどうかを指定するブール値（任意）
     */
    val claimsParameterSupported: Boolean?,
    /**
     * request_parameter_supported
     *
     * OpenID Provider が認可リクエストの `request` パラメーターをサポートするかどうかを指定するブール値（任意）
     */
    val requestParameterSupported: Boolean?,
    /**
     * request_uri_parameter_supported
     *
     * OpenID Provider が認可リクエストの `request_uri` パラメーターをサポートするかどうかを指定するブール値（任意）
     */
    val requestUriParameterSupported: Boolean?,
    /**
     * require_request_uri_registration
     *
     * 事前登録済みの `request_uri` 以外の利用をサポートするかどうかを指定するブール値（任意）
     */
    val requireRequestUriRegistration: Boolean?,
    /**
     * op_policy_uri
     *
     * クライアントに対するポリシーのドキュメントの URL（任意）
     */
    val opPolicyUri: String?,
    /**
     * op_tos_uri
     *
     * クライアントに対する利用規約のドキュメントの URL（任意）
     */
    val opTosUri: String?,
)
