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
import myoidcprovider.core.metadata.PKCECodeChallengeMethod
import myoidcprovider.core.metadata.ResponseMode
import myoidcprovider.core.metadata.ResponseType
import myoidcprovider.core.storage.ClientConfigStorage

/**
 * 認可リクエストのコンバーター。
 */
class AuthorizationRequestConverter(
    private val clientConfigStorage: ClientConfigStorage,
) {
    /**
     * HTTP リクエストを認可リクエストに変換します。
     *
     * @param issuer Issuer
     * @param httpRequest HTTP リクエスト
     * @return 変換に成功した場合は [AuthorizationRequest]。失敗した場合は [AuthorizationRequestError]。
     */
    fun convert(
        issuer: IssuerConfig,
        httpRequest: HttpRequest,
    ): Result<AuthorizationRequest, AuthorizationRequestError> = binding {
        val parameters = when (httpRequest.method) {
            HttpMethod.GET -> httpRequest.queryParameters
            HttpMethod.POST -> httpRequest.formParameters
        }

        val client = convertClient(issuer, parameters).bind()
        val redirectUri = convertRedirectUri(client, parameters).bind()
        val state = convertState(redirectUri, parameters).bind()
        val responseType = convertResponseType(issuer, redirectUri, state, parameters).bind()
        val scope = convertScope(issuer, client, redirectUri, state, parameters).bind()
        val responseMode = convertResponseMode(redirectUri, state, parameters).bind()
        val codeChallenge = convertCodeChallenge(issuer, redirectUri, state, parameters).bind()
        val codeChallengeMethod = convertCodeChallengeMethod(issuer, redirectUri, state, parameters).bind()

        AuthorizationRequest(
            responseType,
            client.id,
            redirectUri,
            scope,
            state,
            responseMode,
            codeChallenge,
            codeChallengeMethod,
        )
    }

    private fun convertClient(
        issuer: IssuerConfig,
        parameters: Map<String, List<String>>,
    ): Result<ClientConfig, AuthorizationRequestError> {
        return parameters[AuthorizationRequestParameter.CLIENT_ID]
            .required {
                InvalidClient("'client_id' is required.")
            }.single {
                InvalidClient("'client_id' is duplicated.")
            }.andThen {
                clientConfigStorage.findById(issuer.issuer, it)
                    .toResultOr { InvalidClient("Client does not exist.") }
            }
    }

    private fun convertRedirectUri(
        client: ClientConfig,
        parameters: Map<String, List<String>>,
    ): Result<String, AuthorizationRequestError> {
        return parameters[AuthorizationRequestParameter.REDIRECT_URI]
            .required {
                InvalidRedirectUri("'redirect_uri' is required.")
            }.single {
                InvalidRedirectUri("'redirect_uri' is duplicated.")
            }.andThen {
                if (client.redirectUris.contains(it)) {
                    Ok(it)
                } else {
                    Err(InvalidRedirectUri("'redirect_uri' value is not registered."))
                }
            }
//        return parameters[AuthorizationRequestParameter.REDIRECT_URI]
//            .optional()
//            .singleOrNull {
//                InvalidRedirectUri("'redirect_uri' is duplicated.")
//            }.andThen {
//                if (it != null) {
//                    if (!client.redirectUris.contains(it)) {
//                        Err(InvalidRedirectUri("'redirect_uri' value is not registered."))
//                    } else {
//                        Ok(it)
//                    }
//                } else {
//                    if (client.type == ClientType.PUBLIC) {
//                        Err(InvalidRedirectUri("'redirect_uri' is required."))
//                    } else {
//                        client.redirectUris.singleOrNull()
//                            .toResultOr { InvalidRedirectUri("Multiple 'redirect_uri' are registered.") }
//                    }
//                }
//            }
    }

    private fun convertState(
        redirectUri: String,
        parameters: Map<String, List<String>>,
    ): Result<String?, AuthorizationRequestError> {
        return parameters[AuthorizationRequestParameter.STATE]
            .optional()
            .singleOrNull {
                AuthorizationErrorResponse(
                    redirectUri,
                    AuthorizationErrorCode.INVALID_REQUEST,
                    "'state' is duplicated.",
                )
            }
    }

    private fun convertResponseType(
        issuer: IssuerConfig,
        redirectUri: String,
        state: String?,
        parameters: Map<String, List<String>>,
    ): Result<List<ResponseType>, AuthorizationRequestError> {
        return parameters[AuthorizationRequestParameter.RESPONSE_TYPE]
            .required {
                AuthorizationErrorResponse(
                    redirectUri,
                    AuthorizationErrorCode.INVALID_REQUEST,
                    "'response_type' is required.",
                    state = state,
                )
            }.single {
                AuthorizationErrorResponse(
                    redirectUri,
                    AuthorizationErrorCode.INVALID_REQUEST,
                    "'response_type' is duplicated.",
                    state = state,
                )
            }.andThen {
                val responseType = it.split(" ").map { value ->
                    when (value) {
                        ResponseType.TOKEN.value -> ResponseType.TOKEN
                        ResponseType.CODE.value -> ResponseType.CODE
                        ResponseType.ID_TOKEN.value -> ResponseType.ID_TOKEN
                        else -> return Err(
                            AuthorizationErrorResponse(
                                redirectUri,
                                AuthorizationErrorCode.UNSUPPORTED_RESPONSE_TYPE,
                                "'response_type' value is unknown.",
                                state = state,
                            ),
                        )
                    }
                }

                if (!issuer.supportedResponseTypes.containsAll(responseType)) {
                    return Err(
                        AuthorizationErrorResponse(
                            redirectUri,
                            AuthorizationErrorCode.UNSUPPORTED_RESPONSE_TYPE,
                            "'response_type' value not supported.",
                            state = state,
                        ),
                    )
                }

                Ok(responseType)
            }
    }

    private fun convertScope(
        issuer: IssuerConfig,
        client: ClientConfig,
        redirectUri: String,
        state: String?,
        parameters: Map<String, List<String>>,
    ): Result<List<String>?, AuthorizationRequestError> {
        return parameters[AuthorizationRequestParameter.SCOPE]
            .optional()
            .singleOrNull {
                AuthorizationErrorResponse(
                    redirectUri,
                    AuthorizationErrorCode.INVALID_REQUEST,
                    "'scope' is duplicated.",
                    state = state,
                )
            }.andThen {
                if (it == null) {
                    return Ok(null)
                }

                val scope = it.split(" ")

                if (!client.scopes.containsAll(scope)) {
                    return Err(
                        AuthorizationErrorResponse(
                            redirectUri,
                            AuthorizationErrorCode.INVALID_REQUEST,
                            "'scope' value not supported.",
                            state = state,
                        ),
                    )
                }

                if (!issuer.scopes.containsAll(scope)) {
                    return Err(
                        AuthorizationErrorResponse(
                            redirectUri,
                            AuthorizationErrorCode.INVALID_REQUEST,
                            "'scope' value not supported.",
                            state = state,
                        ),
                    )
                }

                Ok(scope)
            }
    }

    private fun convertResponseMode(
        redirectUri: String,
        state: String?,
        parameters: Map<String, List<String>>,
    ): Result<ResponseMode?, AuthorizationRequestError> {
        return parameters[AuthorizationRequestParameter.RESPONSE_MODE]
            .optional()
            .singleOrNull {
                AuthorizationErrorResponse(
                    redirectUri,
                    AuthorizationErrorCode.INVALID_REQUEST,
                    "'response_mode' is duplicated.",
                    state = state,
                )
            }.andThen {
                if (it == null) {
                    return Ok(null)
                }

                when (it) {
                    ResponseMode.QUERY.value -> Ok(ResponseMode.QUERY)
                    ResponseMode.FRAGMENT.value -> Ok(ResponseMode.FRAGMENT)
                    ResponseMode.FORM_POST.value -> Ok(ResponseMode.FORM_POST)
                    else -> Err(
                        AuthorizationErrorResponse(
                            redirectUri,
                            AuthorizationErrorCode.INVALID_REQUEST,
                            "'response_mode' value is unknown.",
                            state = state,
                        ),
                    )
                }
            }
    }

    private fun convertCodeChallenge(
        issuer: IssuerConfig,
        redirectUri: String,
        state: String?,
        parameters: Map<String, List<String>>,
    ): Result<String?, AuthorizationRequestError> {
        return parameters[AuthorizationRequestParameter.CODE_CHALLENGE]
            .optional()
            .singleOrNull {
                AuthorizationErrorResponse(
                    redirectUri,
                    AuthorizationErrorCode.INVALID_REQUEST,
                    "'code_challenge' is duplicated.",
                    state = state,
                )
            }.andThen {
                if (issuer.requiredPKCE && it == null) {
                    return Err(
                        AuthorizationErrorResponse(
                            redirectUri,
                            AuthorizationErrorCode.INVALID_REQUEST,
                            "'code_challenge' is required.",
                            state = state,
                        ),
                    )
                }

                Ok(it)
            }
    }

    private fun convertCodeChallengeMethod(
        issuer: IssuerConfig,
        redirectUri: String,
        state: String?,
        parameters: Map<String, List<String>>,
    ): Result<PKCECodeChallengeMethod?, AuthorizationRequestError> {
        return parameters[AuthorizationRequestParameter.CODE_CHALLENGE_METHOD]
            .optional()
            .singleOrNull {
                AuthorizationErrorResponse(
                    redirectUri,
                    AuthorizationErrorCode.INVALID_REQUEST,
                    "'code_challenge_method' is duplicated.",
                    state = state,
                )
            }.andThen {
                if (it == null) {
                    return Ok(null)
                }

                val codeChallengeMethod = when (it) {
                    PKCECodeChallengeMethod.PLAIN.value -> PKCECodeChallengeMethod.PLAIN
                    PKCECodeChallengeMethod.S256.value -> PKCECodeChallengeMethod.S256
                    else -> return Err(
                        AuthorizationErrorResponse(
                            redirectUri,
                            AuthorizationErrorCode.INVALID_REQUEST,
                            "'code_challenge_method' value is unknown.",
                            state = state,
                        ),
                    )
                }

                if (!issuer.supportedCodeChallengeMethods.contains(codeChallengeMethod)) {
                    return Err(
                        AuthorizationErrorResponse(
                            redirectUri,
                            AuthorizationErrorCode.INVALID_REQUEST,
                            "'code_challenge_method' value not supported.",
                            state = state,
                        ),
                    )
                }

                Ok(codeChallengeMethod)
            }
    }
}

fun <E> List<String>?.required(failure: () -> E): Result<List<String>, E> =
    when (this?.size) {
        0 -> Err(failure())
        null -> Err(failure())
        else -> Ok(this)
    }

fun List<String>?.optional() = Ok(this)

fun <E> Result<List<String>, E>.single(failure: () -> E): Result<String, E> {
    return this.andThen {
        when (it.size) {
            1 -> Ok(it[0])
            else -> Err(failure())
        }
    }
}

fun <E> Result<List<String>?, E>.singleOrNull(failure: () -> E): Result<String?, E> {
    return this.andThen {
        it?.let {
            when (it.size) {
                0 -> Ok(null)
                1 -> Ok(it[0])
                else -> Err(failure())
            }
        } ?: Ok(null)
    }
}
