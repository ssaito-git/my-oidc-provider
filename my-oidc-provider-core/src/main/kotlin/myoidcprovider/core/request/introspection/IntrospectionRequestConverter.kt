package myoidcprovider.core.request.introspection

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.binding
import myoidcprovider.core.http.HttpRequest
import myoidcprovider.core.metadata.TokenTypeHint
import myoidcprovider.core.request.authorization.optional
import myoidcprovider.core.request.authorization.required
import myoidcprovider.core.request.authorization.single
import myoidcprovider.core.request.authorization.singleOrNull

/**
 * イントロスペクションリクエストのリクエストコンバーター。
 */
class IntrospectionRequestConverter {
    /**
     * HTTP リクエストをイントロスペクションリクエストに変換します。
     *
     * @param httpRequest HTTP リクエスト
     * @return 変換に成功した場合は [IntrospectionRequest]。失敗した場合は [IntrospectionRequestError]。
     */
    fun convert(
        httpRequest: HttpRequest,
    ): Result<IntrospectionRequest, IntrospectionRequestError> = binding {
        val token = convertToken(httpRequest).bind()
        val tokenTypeHint = convertTokenTypeHint(httpRequest).bind()

        IntrospectionRequest(
            token,
            tokenTypeHint,
        )
    }

    private fun convertToken(httpRequest: HttpRequest): Result<String, IntrospectionRequestError> {
        return httpRequest.formParameters[IntrospectionRequestParameter.TOKEN]
            .required {
                IntrospectionRequestError.ErrorResponse(
                    IntrospectionErrorCode.INVALID_REQUEST,
                    "'token' is required.",
                )
            }.single {
                IntrospectionRequestError.ErrorResponse(
                    IntrospectionErrorCode.INVALID_REQUEST,
                    "'token' is duplicated.",
                )
            }
    }

    private fun convertTokenTypeHint(httpRequest: HttpRequest): Result<TokenTypeHint?, IntrospectionRequestError> {
        return httpRequest.formParameters[IntrospectionRequestParameter.TOKEN_TYPE_HINT]
            .optional()
            .singleOrNull {
                IntrospectionRequestError.ErrorResponse(
                    IntrospectionErrorCode.INVALID_REQUEST,
                    "'token_type_hint' is duplicated.",
                )
            }.andThen {
                when (it) {
                    TokenTypeHint.ACCESS_TOKEN.value -> Ok(TokenTypeHint.ACCESS_TOKEN)
                    TokenTypeHint.REFRESH_TOKEN.value -> Ok(TokenTypeHint.REFRESH_TOKEN)
                    null -> Ok(null)
                    else -> Err(
                        IntrospectionRequestError.ErrorResponse(
                            IntrospectionErrorCode.INVALID_REQUEST,
                            "'token_type_hint' value is unknown.",
                        ),
                    )
                }
            }
    }
}
