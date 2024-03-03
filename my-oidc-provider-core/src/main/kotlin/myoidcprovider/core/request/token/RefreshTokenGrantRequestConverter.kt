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
 * リフレッシュトークングラントリクエストのコンバーター。
 */
class RefreshTokenGrantRequestConverter {
    /**
     * HTTP リクエストをリフレッシュトークングラントリクエストに変換します。
     *
     * @param issuer Issuer
     * @param httpRequest HTTP リクエスト
     * @return 変換に成功した場合は [RefreshTokenGrantRequest]。失敗した場合は [TokenRequestError]。
     */
    fun convert(
        issuer: IssuerConfig,
        httpRequest: HttpRequest,
    ): Result<RefreshTokenGrantRequest, TokenRequestError> = binding {
        val grantType = convertGrantType(issuer, httpRequest.formParameters).bind()
        val refreshToken = convertRefreshToken(httpRequest.formParameters).bind()
        val scope = convertScope(issuer, httpRequest.formParameters).bind()

        RefreshTokenGrantRequest(
            grantType,
            refreshToken,
            scope,
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
                    GrantType.REFRESH_TOKEN.value -> Ok(GrantType.AUTHORIZATION_CODE)
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

    private fun convertRefreshToken(
        parameters: Map<String, List<String>>,
    ): Result<String, TokenRequestError> {
        return parameters[TokenRequestParameter.REFRESH_TOKEN]
            .required {
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_REQUEST,
                    "'refresh_token' is required.",
                )
            }.single {
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_REQUEST,
                    "'refresh_token' is duplicated.",
                )
            }
    }

    private fun convertScope(
        issuerConfig: IssuerConfig,
        parameters: Map<String, List<String>>,
    ): Result<List<String>?, TokenRequestError> {
        return parameters[TokenRequestParameter.SCOPE]
            .optional()
            .singleOrNull {
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_REQUEST,
                    "'scope' is duplicated.",
                )
            }.andThen { value ->
                value?.split(" ")?.let {
                    if (issuerConfig.scopes.containsAll(it)) {
                        Ok(it)
                    } else {
                        Err(
                            TokenRequestError.ErrorResponse(
                                TokenErrorCode.INVALID_REQUEST,
                                "'scope' value not supported.",
                            ),
                        )
                    }
                } ?: Ok(null)
            }
    }
}
