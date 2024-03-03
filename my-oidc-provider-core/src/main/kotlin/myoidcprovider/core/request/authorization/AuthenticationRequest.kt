package myoidcprovider.core.request.authorization

import myoidcprovider.core.metadata.Display
import myoidcprovider.core.metadata.Prompt
import myoidcprovider.core.metadata.ResponseMode
import myoidcprovider.core.metadata.ResponseType

/**
 * 認証リクエスト
 */
data class AuthenticationRequest(
    /**
     * scope (REQUIRED)
     */
    val scope: List<String>,
    /**
     * response_type (REQUIRED)
     */
    val responseType: List<ResponseType>,
    /**
     * client_id (REQUIRED)
     */
    val clientId: String,
    /**
     * redirect_uri (REQUIRED)
     */
    val redirectUri: String,
    /**
     * state (RECOMMENDED)
     */
    val state: String?,
    /**
     * response_mode (OPTIONAL)
     */
    val responseMode: ResponseMode?,
    /**
     * nonce (OPTIONAL)
     */
    val nonce: String?,
    /**
     * display (OPTIONAL)
     */
    val display: Display?,
    /**
     * prompt (OPTIONAL)
     */
    val prompt: List<Prompt>?,
    /**
     * max_age (OPTIONAL)
     */
    val maxAge: Long?,
    /**
     * ui_locales (OPTIONAL)
     */
    val uiLocales: String?,
    /**
     * id_token_hint (OPTIONAL)
     */
    val idTokenHint: String?,
    /**
     * login_hint (OPTIONAL)
     */
    val loginHint: String?,
    /**
     * acr_values (OPTIONAL)
     */
    val acrValues: String?,
)
