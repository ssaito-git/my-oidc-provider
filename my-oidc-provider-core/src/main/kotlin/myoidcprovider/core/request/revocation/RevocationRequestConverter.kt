package myoidcprovider.core.request.revocation

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
 * リヴォケーションリクエストのコンバーター。
 */
class RevocationRequestConverter {
    /**
     * HTTP リクエストをリヴォケーションリクエストに変換します。
     *
     * @param httpRequest HTTP リクエスト
     * @return 変換に成功した場合は [RevocationRequest]。失敗した場合は [RevocationRequestError]。
     */
    fun convert(httpRequest: HttpRequest): Result<RevocationRequest, RevocationRequestError> = binding {
        val token = convertToken(httpRequest).bind()
        val tokenTypeHint = convertTokenTypeHint(httpRequest).bind()

        RevocationRequest(
            token,
            tokenTypeHint,
        )
    }

    private fun convertToken(httpRequest: HttpRequest): Result<String, RevocationRequestError> {
        return httpRequest.formParameters[RevocationRequestParameter.TOKEN]
            .required {
                RevocationRequestError.ErrorResponse(
                    RevocationErrorCode.INVALID_REQUEST,
                    "'token' is required.",
                )
            }.single {
                RevocationRequestError.ErrorResponse(
                    RevocationErrorCode.INVALID_REQUEST,
                    "'token' is duplicated.",
                )
            }
    }

    private fun convertTokenTypeHint(httpRequest: HttpRequest): Result<TokenTypeHint?, RevocationRequestError> {
        return httpRequest.formParameters[RevocationRequestParameter.TOKEN_TYPE_HINT]
            .optional()
            .singleOrNull {
                RevocationRequestError.ErrorResponse(
                    RevocationErrorCode.INVALID_REQUEST,
                    "'token_type_hint' is duplicated.",
                )
            }.andThen {
                when (it) {
                    TokenTypeHint.ACCESS_TOKEN.value -> Ok(TokenTypeHint.ACCESS_TOKEN)
                    TokenTypeHint.REFRESH_TOKEN.value -> Ok(TokenTypeHint.REFRESH_TOKEN)
                    null -> Ok(null)
                    else -> Err(
                        RevocationRequestError.ErrorResponse(
                            RevocationErrorCode.INVALID_REQUEST,
                            "'token_type_hint' value is unknown.",
                        ),
                    )
                }
            }
    }
}
