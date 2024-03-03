package myoidcprovider.core.request.token

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.binding
import myoidcprovider.core.http.HttpRequest
import myoidcprovider.core.issuer.IssuerConfig
import myoidcprovider.core.metadata.GrantType
import myoidcprovider.core.request.authorization.optional
import myoidcprovider.core.request.authorization.required
import myoidcprovider.core.request.authorization.single
import myoidcprovider.core.request.authorization.singleOrNull

/**
 * 認可コードグラントのリクエストコンバーター。
 */
class AuthorizationCodeGrantRequestConverter {
    /**
     * HTTP リクエストを認可コードグラントリクエストに変換します。
     *
     * @param issuerConfig Issuer
     * @param httpRequest HTTP rクエスト
     * @return 変換に成功した場合は [AuthorizationCodeGrantRequest]。失敗した場合は [TokenRequestError]。
     */
    fun convert(
        issuerConfig: IssuerConfig,
        httpRequest: HttpRequest,
    ): Result<AuthorizationCodeGrantRequest, TokenRequestError> = binding {
        val grantType = convertGrantType(issuerConfig, httpRequest.formParameters).bind()
        val code = convertCode(httpRequest.formParameters).bind()
        val redirectUri = convertRedirectUri(httpRequest.formParameters).bind()
        val clientId = convertClientId(httpRequest.formParameters).bind()
        val codeVerifier = convertCodeVerifier(httpRequest.formParameters).bind()

        AuthorizationCodeGrantRequest(
            grantType,
            code,
            redirectUri,
            clientId,
            codeVerifier,
        )
    }

    private fun convertGrantType(
        issuerConfig: IssuerConfig,
        parameters: Map<String, List<String>>,
    ): Result<GrantType, TokenRequestError> {
        return parameters[TokenRequestParameter.GRANT_TYPE]
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
                    GrantType.AUTHORIZATION_CODE.value -> Ok(GrantType.AUTHORIZATION_CODE)
                    else -> Err(
                        TokenRequestError.ErrorResponse(
                            TokenErrorCode.UNSUPPORTED_GRANT_TYPE,
                            "'grant_type' value is unknown.",
                        ),
                    )
                }
            }.andThen {
                if (issuerConfig.supportedGrantTypes.contains(it)) {
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

    private fun convertCode(
        parameters: Map<String, List<String>>,
    ): Result<String, TokenRequestError> {
        return parameters[TokenRequestParameter.CODE]
            .required {
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_REQUEST,
                    "'code' is required.",
                )
            }
            .single {
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_REQUEST,
                    "'code' is duplicated.",
                )
            }
    }

    private fun convertRedirectUri(
        parameters: Map<String, List<String>>,
    ): Result<String?, TokenRequestError> {
        return parameters[TokenRequestParameter.REDIRECT_URI]
            .optional()
            .singleOrNull {
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_REQUEST,
                    "'code' is duplicated.",
                )
            }
    }

    private fun convertClientId(
        parameters: Map<String, List<String>>,
    ): Result<String?, TokenRequestError> {
        return parameters[TokenRequestParameter.CLIENT_ID]
            .optional()
            .singleOrNull {
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_REQUEST,
                    "'client_id' is duplicated.",
                )
            }
    }

    private fun convertCodeVerifier(
        parameters: Map<String, List<String>>,
    ): Result<String?, TokenRequestError> {
        return parameters[TokenRequestParameter.CODE_VERIFIER]
            .optional()
            .singleOrNull {
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_REQUEST,
                    "'code_verifier' is duplicated.",
                )
            }
    }
}
