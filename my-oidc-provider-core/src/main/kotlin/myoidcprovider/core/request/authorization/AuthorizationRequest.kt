package myoidcprovider.core.request.authorization

import myoidcprovider.core.metadata.PKCECodeChallengeMethod
import myoidcprovider.core.metadata.ResponseMode
import myoidcprovider.core.metadata.ResponseType

/**
 * 認可リクエスト。
 */
data class AuthorizationRequest(
    /**
     * response_type (REQUIRED)
     */
    val responseType: List<ResponseType>,
    /**
     * client_id (REQUIRED)
     */
    val clientId: String,
    /**
     * redirect_uri (OPTIONAL)
     */
    val redirectUri: String,
    /**
     * scope (OPTIONAL)
     */
    val scope: List<String>?,
    /**
     * state (RECOMMENDED)
     */
    val state: String?,
    /**
     * response_mode (OPTIONAL)
     *
     * [OAuth 2.0 Multiple Response Type Encoding Practices](https://openid.net/specs/oauth-v2-multiple-response-types-1_0.html)
     */
    val responseMode: ResponseMode?,
    /**
     * code_challenge (OPTIONAL)
     *
     * [RFC 7636 - Proof Key for Code Exchange by OAuth Public Clients](https://datatracker.ietf.org/doc/html/rfc7636)
     */
    val codeChallenge: String?,
    /**
     * code_challenge_method (OPTIONAL)
     *
     * [RFC 7636 - Proof Key for Code Exchange by OAuth Public Clients](https://datatracker.ietf.org/doc/html/rfc7636)
     */
    val codeChallengeMethod: PKCECodeChallengeMethod?,
)
