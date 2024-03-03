package myoidcprovider.core.metadata

/**
 * OAuth 2.0 Authorization Server Metadata
 *
 * [RFC8414](https://datatracker.ietf.org/doc/html/rfc8414)
 */
data class OAuth2Metadata(
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
     * jwks_uri
     *
     * JWKs エンドポイント（任意）
     */
    val jwksUri: String? = null,
    /**
     * registration_endpoint
     *
     * OAuth 2.0 動的クライアント登録エンドポイント RFC7591（任意）
     */
    val registrationEndpoint: String? = null,
    /**
     * scopes_supported
     *
     * 認可サーバーがサポートするスコープ（推奨）
     */
    val scopesSupported: List<String>? = null,
    /**
     * response_types_supported
     *
     * 認可サーバーがサポートする response_type のリスト（必須）
     */
    val responseTypesSupported: List<ResponseType>,
    /**
     * response_modes_supported
     *
     * 認可サーバーがサポートする response_mode のリスト（任意）
     */
    val responseModesSupported: List<ResponseMode>? = null,
    /**
     * grant_types_supported
     *
     * 認可サーバーがサポートする許可タイプのリスト（任意）
     */
    val grantTypesSupported: List<GrantType>? = null,
    /**
     * token_endpoint_auth_methods_supported
     *
     * トークンエンドポイントでサポートするクライアント認証方法のリスト（任意）
     */
    val tokenEndpointAuthMethodsSupported: List<TokenEndpointAuthMethod>? = null,
    /**
     * token_endpoint_auth_signing_alg_values_supported
     *
     * トークンエンドポイントがサポートする認証方法（private_key_jwt, client_secret_jwt）で使用する署名アルゴリズム（任意）
     */
    val tokenEndpointAuthSigningAlgValuesSupported: List<SigningAlgorithms>? = null,
    /**
     * service_documentation
     *
     * 認可サーバーが公開しているドキュメントの URL（任意）
     */
    val serviceDocumentation: String? = null,
    /**
     * ui_locales_supported
     *
     * ユーザーインタフェースでサポートする言語のリスト（任意）
     */
    val uiLocalesSupported: List<String>? = null,
    /**
     * op_policy_uri
     *
     * 認可サーバーがクライアントを登録する人に提供する URL（任意）
     */
    val opPolicyUri: String? = null,
    /**
     * op_tos_uri
     *
     * 認可サーバーがクライアントを登録する人に提供する利用規約の URL（任意）
     */
    val opTosUri: String? = null,
    /**
     * revocation_endpoint
     *
     * トークン無効化エンドポイント（任意）
     */
    val revocationEndpoint: String? = null,
    /**
     * revocation_endpoint_auth_methods_supported
     *
     * トークン無効化エンドポイントでサポートするクライアント認証方法のリスト（任意）
     */
    val revocationEndpointAuthMethodsSupported: List<TokenEndpointAuthMethod>? = null,
    /**
     * revocation_endpoint_auth_signing_alg_values_supported
     *
     * トークン無効化エンドポイントがサポートする認証方法（private_key_jwt, client_secret_jwt）で使用する署名アルゴリズム（任意）
     */
    val revocationEndpointAuthSigningAlgValuesSupported: List<SigningAlgorithms>? = null,
    /**
     * introspection_endpoint
     *
     * イントロスペクションエンドポイント（任意）
     */
    val introspectionEndpoint: String? = null,
    /**
     * introspection_endpoint_auth_methods_supported
     *
     * イントロスペクションエンドポイントでサポートするクライアント認証方法のリスト（任意）
     */
    val introspectionEndpointAuthMethodsSupported: List<TokenEndpointAuthMethod>? = null,
    /**
     * introspection_endpoint_auth_signing_alg_values_supported
     *
     * イントロスペクションエンドポイントがサポートする認証方法（private_key_jwt, client_secret_jwt）で使用する署名アルゴリズム（任意）
     */
    val introspectionEndpointAuthSigningAlgValuesSupported: List<SigningAlgorithms>? = null,
    /**
     * code_challenge_methods_supported
     *
     * PKCE でサポートするコードチャレンジメソッドのリスト（任意）
     */
    val codeChallengeMethodsSupported: List<PKCECodeChallengeMethod>? = null,
)
