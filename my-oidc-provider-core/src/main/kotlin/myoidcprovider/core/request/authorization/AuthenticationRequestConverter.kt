package myoidcprovider.core.request.authorization

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import myoidcprovider.core.client.ClientConfig
import myoidcprovider.core.http.HttpMethod
import myoidcprovider.core.http.HttpRequest
import myoidcprovider.core.issuer.IssuerConfig
import myoidcprovider.core.metadata.Display
import myoidcprovider.core.metadata.Prompt
import myoidcprovider.core.storage.ClientConfigStorage

/**
 * 認証リクエストのコンバーター。
 */
class AuthenticationRequestConverter(
    private val clientConfigStorage: ClientConfigStorage,
) {
    /**
     * HTTP リクエストを認証リクエストに変換します。
     *
     * @param issuer Issuer
     * @param authorizationRequest 認可リクエスト
     * @param httpRequest HTTP リクエスト
     * @return `scope` に `openid` が含まれており、変換に成功した場合は [AuthenticationRequest]。`openid` が含まれていない場合は null。
     * 変換に失敗した場合は [AuthorizationRequestError]。
     */
    fun convert(
        issuer: IssuerConfig,
        authorizationRequest: AuthorizationRequest,
        httpRequest: HttpRequest,
    ): Result<AuthenticationRequest?, AuthorizationRequestError> = binding {
        val parameters = when (httpRequest.method) {
            HttpMethod.GET -> httpRequest.queryParameters
            HttpMethod.POST -> httpRequest.formParameters
        }

        if (authorizationRequest.scope?.contains("openid") == true) {
            val client = clientConfigStorage.findById(issuer.issuer, authorizationRequest.clientId)
                .toResultOr { InvalidClient("Client does not exist.") }.bind()
            val redirectUri = convertRedirectUri(client, parameters).bind()
            val nonce = convertNonce(redirectUri, authorizationRequest.state, parameters).bind()
            val display = convertDisplay(redirectUri, authorizationRequest.state, parameters).bind()
            val prompt = convertPrompt(redirectUri, authorizationRequest.state, parameters).bind()
            val maxAge = convertMaxAge(redirectUri, authorizationRequest.state, parameters).bind()
            val uiLocales = convertUiLocales(redirectUri, authorizationRequest.state, parameters).bind()
            val idTokenHint = convertIdTokenHint(redirectUri, authorizationRequest.state, parameters).bind()
            val loginHint = convertLoginHint(redirectUri, authorizationRequest.state, parameters).bind()
            val acrValues = convertAcrValues(redirectUri, authorizationRequest.state, parameters).bind()

            AuthenticationRequest(
                authorizationRequest.scope,
                authorizationRequest.responseType,
                client.id,
                redirectUri,
                authorizationRequest.state,
                authorizationRequest.responseMode,
                nonce,
                display,
                prompt,
                maxAge,
                uiLocales,
                idTokenHint,
                loginHint,
                acrValues,
            )
        } else {
            null
        }
    }

    private fun convertRedirectUri(
        client: ClientConfig,
        parameters: Map<String, List<String>>,
    ): Result<String, AuthorizationRequestError> {
        return parameters[AuthorizationRequestParameter.REDIRECT_URI].required {
            InvalidRedirectUri("'redirect_uri' is required.")
        }.single {
            InvalidRedirectUri("'redirect_uri' is duplicated.")
        }.andThen {
            if (!client.redirectUris.contains(it)) {
                Err(InvalidRedirectUri("'redirect_uri' value is not registered."))
            } else {
                Ok(it)
            }
        }
    }

    private fun convertNonce(
        redirectUri: String,
        state: String?,
        parameters: Map<String, List<String>>,
    ): Result<String?, AuthorizationRequestError> {
        return parameters[AuthorizationRequestParameter.NONCE]
            .optional()
            .singleOrNull {
                AuthenticationErrorResponse(
                    redirectUri,
                    AuthenticationErrorCode.INVALID_REQUEST,
                    "'nonce' is duplicated.",
                    state = state,
                )
            }
    }

    private fun convertDisplay(
        redirectUri: String,
        state: String?,
        parameters: Map<String, List<String>>,
    ): Result<Display?, AuthorizationRequestError> {
        return parameters[AuthorizationRequestParameter.DISPLAY]
            .optional()
            .singleOrNull {
                AuthenticationErrorResponse(
                    redirectUri,
                    AuthenticationErrorCode.INVALID_REQUEST,
                    "'display' is duplicated.",
                    state = state,
                )
            }
            .andThen {
                when (it) {
                    Display.PAGE.value -> Ok(Display.PAGE)
                    Display.POPUP.value -> Ok(Display.POPUP)
                    Display.TOUCH.value -> Ok(Display.TOUCH)
                    Display.WAP.value -> Ok(Display.WAP)
                    null -> Ok(null)
                    else -> Err(
                        AuthenticationErrorResponse(
                            redirectUri,
                            AuthenticationErrorCode.INVALID_REQUEST,
                            "'display' value is unknown.",
                            state = state,
                        ),
                    )
                }
            }
    }

    private fun convertPrompt(
        redirectUri: String,
        state: String?,
        parameters: Map<String, List<String>>,
    ): Result<List<Prompt>?, AuthorizationRequestError> {
        return parameters[AuthorizationRequestParameter.PROMPT]
            .optional()
            .singleOrNull {
                AuthenticationErrorResponse(
                    redirectUri,
                    AuthenticationErrorCode.INVALID_REQUEST,
                    "'prompt' is duplicated.",
                    state = state,
                )
            }.andThen {
                val prompt = it?.split(" ")?.map { value ->
                    when (value) {
                        Prompt.NONE.value -> Prompt.NONE
                        Prompt.LOGIN.value -> Prompt.LOGIN
                        Prompt.CONSENT.value -> Prompt.CONSENT
                        Prompt.SELECT_ACCOUNT.value -> Prompt.SELECT_ACCOUNT
                        else -> return Err(
                            AuthenticationErrorResponse(
                                redirectUri,
                                AuthenticationErrorCode.INVALID_REQUEST,
                                "'prompt' value is unknown.",
                                state = state,
                            ),
                        )
                    }
                }

                Ok(prompt)
            }
    }

    private fun convertMaxAge(
        redirectUri: String,
        state: String?,
        parameters: Map<String, List<String>>,
    ): Result<Long?, AuthorizationRequestError> {
        return parameters[AuthorizationRequestParameter.MAX_AGE]
            .optional()
            .singleOrNull {
                AuthenticationErrorResponse(
                    redirectUri,
                    AuthenticationErrorCode.INVALID_REQUEST,
                    "'max_age' is duplicated.",
                    state = state,
                )
            }.andThen {
                if (it == null) {
                    return Ok(null)
                }

                it.toLongOrNull().toResultOr {
                    AuthenticationErrorResponse(
                        redirectUri,
                        AuthenticationErrorCode.INVALID_REQUEST,
                        "'max_age' is invalid value.",
                        state = state,
                    )
                }
            }
    }

    private fun convertUiLocales(
        redirectUri: String,
        state: String?,
        parameters: Map<String, List<String>>,
    ): Result<String?, AuthorizationRequestError> {
        return parameters[AuthorizationRequestParameter.UI_LOCALES]
            .optional()
            .singleOrNull {
                AuthenticationErrorResponse(
                    redirectUri,
                    AuthenticationErrorCode.INVALID_REQUEST,
                    "'ui_locales' is duplicated.",
                    state = state,
                )
            }
            .andThen {
                Ok(it)
            }
    }

    private fun convertIdTokenHint(
        redirectUri: String,
        state: String?,
        parameters: Map<String, List<String>>,
    ): Result<String?, AuthorizationRequestError> {
        return parameters[AuthorizationRequestParameter.ID_TOKEN_HINT]
            .optional()
            .singleOrNull {
                AuthenticationErrorResponse(
                    redirectUri,
                    AuthenticationErrorCode.INVALID_REQUEST,
                    "'id_token_hint' is duplicated.",
                    state = state,
                )
            }
            .andThen {
                Ok(it)
            }
    }

    private fun convertLoginHint(
        redirectUri: String,
        state: String?,
        parameters: Map<String, List<String>>,
    ): Result<String?, AuthorizationRequestError> {
        return parameters[AuthorizationRequestParameter.LOGIN_HINT]
            .optional()
            .singleOrNull {
                AuthenticationErrorResponse(
                    redirectUri,
                    AuthenticationErrorCode.INVALID_REQUEST,
                    "'login_hint' is duplicated.",
                    state = state,
                )
            }
            .andThen {
                Ok(it)
            }
    }

    private fun convertAcrValues(
        redirectUri: String,
        state: String?,
        parameters: Map<String, List<String>>,
    ): Result<String?, AuthorizationRequestError> {
        return parameters[AuthorizationRequestParameter.ACR_VALUES]
            .optional()
            .singleOrNull {
                AuthenticationErrorResponse(
                    redirectUri,
                    AuthenticationErrorCode.INVALID_REQUEST,
                    "'acr_values' is duplicated.",
                    state = state,
                )
            }
            .andThen {
                Ok(it)
            }
    }
}
