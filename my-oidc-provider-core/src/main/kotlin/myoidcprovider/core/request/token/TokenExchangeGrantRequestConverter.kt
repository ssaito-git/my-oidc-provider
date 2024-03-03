package myoidcprovider.core.request.token

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.binding
import myoidcprovider.core.client.ClientConfig
import myoidcprovider.core.http.HttpRequest
import myoidcprovider.core.issuer.IssuerConfig
import myoidcprovider.core.metadata.GrantType
import myoidcprovider.core.metadata.TokenType
import myoidcprovider.core.request.authorization.optional
import myoidcprovider.core.request.authorization.required
import myoidcprovider.core.request.authorization.single
import myoidcprovider.core.request.authorization.singleOrNull
import java.net.URI
import java.net.URISyntaxException

/**
 * トークンエクスチェンジグラントリクエストのコンバーター。
 */
class TokenExchangeGrantRequestConverter {
    /**
     * HTTP リクエストをクライアントクレデンシャルグラントリクエストに変換します。
     *
     * @param issuerConfig Issuer
     * @param clientConfig Client
     * @param httpRequest HTTP リクエスト
     * @return 変換に成功した場合は [TokenExchangeGrantRequest]。失敗した場合は [TokenRequestError]。
     */
    fun convert(
        issuerConfig: IssuerConfig,
        clientConfig: ClientConfig,
        httpRequest: HttpRequest,
    ): Result<TokenExchangeGrantRequest, TokenRequestError> = binding {
        val grantType = convertGrantType(issuerConfig, clientConfig, httpRequest.formParameters).bind()
        val resource = convertResource(httpRequest.formParameters).bind()
        val audience = convertAudience(httpRequest.formParameters).bind()
        val scope = convertScope(issuerConfig, clientConfig, httpRequest.formParameters).bind()
        val requestedTokenType = convertRequestedTokenType(httpRequest.formParameters).bind()
        val subjectToken = convertSubjectToken(httpRequest.formParameters).bind()
        val subjectTokeType = convertSubjectTokenType(httpRequest.formParameters).bind()
        val actorToken = convertActorToken(httpRequest.formParameters).bind()
        val actorTokenType = convertActorTokenType(actorToken, httpRequest.formParameters).bind()

        TokenExchangeGrantRequest(
            grantType,
            resource,
            audience,
            scope,
            requestedTokenType,
            subjectToken,
            subjectTokeType,
            actorToken,
            actorTokenType,
        )
    }

    private fun convertGrantType(
        issuerConfig: IssuerConfig,
        clientConfig: ClientConfig,
        parameters: Map<String, List<String>>,
    ): Result<GrantType, TokenRequestError> {
        return parameters[TokenExchangeRequestParameter.GRANT_TYPE]
            .required {
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_REQUEST,
                    "'grant_type' is required.",
                )
            }.single {
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_REQUEST,
                    "'grant_type' is duplicated.",
                )
            }.andThen {
                when (it) {
                    GrantType.TOKEN_EXCHANGE.value -> Ok(GrantType.TOKEN_EXCHANGE)
                    else -> Err(
                        TokenRequestError.ErrorResponse(
                            TokenErrorCode.UNSUPPORTED_GRANT_TYPE,
                            "'grant_type' value is unknown.",
                        ),
                    )
                }
            }.andThen {
                if (clientConfig.supportedGrantTypes.contains(it) && issuerConfig.supportedGrantTypes.contains(it)) {
                    Ok(it)
                } else {
                    Err(
                        TokenRequestError.ErrorResponse(
                            TokenErrorCode.UNSUPPORTED_GRANT_TYPE,
                            "Unsupported grant type.",
                        ),
                    )
                }
            }
    }

    private fun convertResource(
        parameters: Map<String, List<String>>,
    ): Result<List<String>?, TokenRequestError> {
        return parameters[TokenExchangeRequestParameter.RESOURCE]
            .optional()
            .andThen {
                val includesInvalidUri = it?.any { uriString ->
                    try {
                        val uri = URI(uriString)
                        !(uri.isAbsolute && uri.fragment.isEmpty())
                    } catch (_: URISyntaxException) {
                        true
                    }
                }

                if (includesInvalidUri == true) {
                    Err(
                        TokenRequestError.ErrorResponse(
                            TokenErrorCode.INVALID_REQUEST,
                            "'resource' is invalid.",
                        ),
                    )
                } else {
                    Ok(it)
                }
            }
    }

    private fun convertAudience(
        parameters: Map<String, List<String>>,
    ): Result<List<String>?, TokenRequestError> {
        return parameters[TokenExchangeRequestParameter.RESOURCE]
            .optional()
    }

    private fun convertScope(
        issuer: IssuerConfig,
        client: ClientConfig,
        parameters: Map<String, List<String>>,
    ): Result<List<String>?, TokenRequestError> {
        return parameters[TokenExchangeRequestParameter.SCOPE]
            .optional()
            .singleOrNull {
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_REQUEST,
                    "'scope' is duplicated.",
                )
            }.andThen {
                if (it == null) {
                    return Ok(null)
                }

                val scope = it.split(" ")

                if (!client.scopes.containsAll(scope)) {
                    return Err(
                        TokenRequestError.ErrorResponse(
                            TokenErrorCode.INVALID_REQUEST,
                            "'scope' value not supported.",
                        ),
                    )
                }

                if (!issuer.scopes.containsAll(scope)) {
                    return Err(
                        TokenRequestError.ErrorResponse(
                            TokenErrorCode.INVALID_REQUEST,
                            "'scope' value not supported.",
                        ),
                    )
                }

                Ok(scope)
            }
    }

    private fun convertRequestedTokenType(
        parameters: Map<String, List<String>>,
    ): Result<TokenType?, TokenRequestError> {
        return parameters[TokenExchangeRequestParameter.REQUESTED_TOKEN_TYPE]
            .optional()
            .singleOrNull {
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_REQUEST,
                    "'requested_token_type' is duplicated.",
                )
            }.andThen {
                when (it) {
                    TokenType.ACCESS_TOKEN.value -> Ok(TokenType.ACCESS_TOKEN)
                    TokenType.REFRESH_TOKEN.value -> Ok(TokenType.REFRESH_TOKEN)
                    TokenType.ID_TOKEN.value -> Ok(TokenType.ID_TOKEN)
                    TokenType.SAML1.value -> Ok(TokenType.SAML1)
                    TokenType.SAML2.value -> Ok(TokenType.SAML2)
                    TokenType.JWT.value -> Ok(TokenType.JWT)
                    null -> Ok(null)
                    else -> Err(
                        TokenRequestError.ErrorResponse(
                            TokenErrorCode.INVALID_REQUEST,
                            "'requested_token_type' value is unknown.",
                        ),
                    )
                }
            }
    }

    private fun convertSubjectToken(
        parameters: Map<String, List<String>>,
    ): Result<String, TokenRequestError> {
        return parameters[TokenExchangeRequestParameter.SUBJECT_TOKEN]
            .required {
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_REQUEST,
                    "'subject_token' is required.",
                )
            }.single {
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_REQUEST,
                    "'subject_token' is duplicated.",
                )
            }
    }

    private fun convertSubjectTokenType(
        parameters: Map<String, List<String>>,
    ): Result<TokenType, TokenRequestError> {
        return parameters[TokenExchangeRequestParameter.SUBJECT_TOKEN]
            .required {
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_REQUEST,
                    "'subject_token_type' is required.",
                )
            }.single {
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_REQUEST,
                    "'subject_token_type' is duplicated.",
                )
            }.andThen {
                when (it) {
                    TokenType.ACCESS_TOKEN.value -> Ok(TokenType.ACCESS_TOKEN)
                    TokenType.REFRESH_TOKEN.value -> Ok(TokenType.REFRESH_TOKEN)
                    TokenType.ID_TOKEN.value -> Ok(TokenType.ID_TOKEN)
                    TokenType.SAML1.value -> Ok(TokenType.SAML1)
                    TokenType.SAML2.value -> Ok(TokenType.SAML2)
                    TokenType.JWT.value -> Ok(TokenType.JWT)
                    else -> Err(
                        TokenRequestError.ErrorResponse(
                            TokenErrorCode.INVALID_REQUEST,
                            "'subject_token_type' value is unknown.",
                        ),
                    )
                }
            }
    }

    private fun convertActorToken(
        parameters: Map<String, List<String>>,
    ): Result<String?, TokenRequestError> {
        return parameters[TokenExchangeRequestParameter.REQUESTED_TOKEN_TYPE]
            .optional()
            .singleOrNull {
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_REQUEST,
                    "'actor_token' is duplicated.",
                )
            }
    }

    private fun convertActorTokenType(
        actorToken: String?,
        parameters: Map<String, List<String>>,
    ): Result<TokenType?, TokenRequestError> {
        return parameters[TokenExchangeRequestParameter.REQUESTED_TOKEN_TYPE]
            .optional()
            .singleOrNull {
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_REQUEST,
                    "'actor_token_type' is duplicated.",
                )
            }.andThen {
                if (actorToken != null && it == null) {
                    Err(
                        TokenRequestError.ErrorResponse(
                            TokenErrorCode.INVALID_REQUEST,
                            "'actor_token_type' is required.",
                        ),
                    )
                } else {
                    Ok(it)
                }
            }.andThen {
                when (it) {
                    TokenType.ACCESS_TOKEN.value -> Ok(TokenType.ACCESS_TOKEN)
                    TokenType.REFRESH_TOKEN.value -> Ok(TokenType.REFRESH_TOKEN)
                    TokenType.ID_TOKEN.value -> Ok(TokenType.ID_TOKEN)
                    TokenType.SAML1.value -> Ok(TokenType.SAML1)
                    TokenType.SAML2.value -> Ok(TokenType.SAML2)
                    TokenType.JWT.value -> Ok(TokenType.JWT)
                    null -> Ok(null)
                    else -> Err(
                        TokenRequestError.ErrorResponse(
                            TokenErrorCode.INVALID_REQUEST,
                            "'actor_token_type' value is unknown.",
                        ),
                    )
                }
            }
    }
}