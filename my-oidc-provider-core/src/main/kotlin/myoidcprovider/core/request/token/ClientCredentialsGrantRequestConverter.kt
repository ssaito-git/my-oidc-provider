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
import myoidcprovider.core.request.authorization.optional
import myoidcprovider.core.request.authorization.required
import myoidcprovider.core.request.authorization.single
import myoidcprovider.core.request.authorization.singleOrNull

/**
 * クライアントクレデンシャルグラントリクエストのハンドラー。
 */
class ClientCredentialsGrantRequestConverter {
    /**
     * HTTP リクエストをクライアントクレデンシャルグラントリクエストに変換します。
     *
     * @param issuerConfig Issuer
     * @param clientConfig Client
     * @param httpRequest HTTP リクエスト
     * @return 変換に成功した場合は [ClientCredentialsGrantRequest]。失敗した場合は [TokenRequestError]。
     */
    fun convert(
        issuerConfig: IssuerConfig,
        clientConfig: ClientConfig,
        httpRequest: HttpRequest,
    ): Result<ClientCredentialsGrantRequest, TokenRequestError> = binding {
        val grantType = convertGrantType(issuerConfig, clientConfig, httpRequest.formParameters).bind()
        val scope = convertScope(issuerConfig, clientConfig, httpRequest.formParameters).bind()

        ClientCredentialsGrantRequest(grantType, scope)
    }

    private fun convertGrantType(
        issuerConfig: IssuerConfig,
        clientConfig: ClientConfig,
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
                    GrantType.CLIENT_CREDENTIALS.value -> Ok(GrantType.CLIENT_CREDENTIALS)
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

    private fun convertScope(
        issuerConfig: IssuerConfig,
        clientConfig: ClientConfig,
        parameters: Map<String, List<String>>,
    ): Result<List<String>?, TokenRequestError> {
        return parameters[TokenRequestParameter.SCOPE]
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

                if (clientConfig.scopes.containsAll(scope) && issuerConfig.scopes.containsAll(scope)) {
                    Ok(scope)
                } else {
                    Err(
                        TokenRequestError.ErrorResponse(
                            TokenErrorCode.INVALID_REQUEST,
                            "'scope' value not supported.",
                        ),
                    )
                }
            }
    }
}
