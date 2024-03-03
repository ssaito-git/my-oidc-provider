package myoidcprovider.core.handler

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import myoidcprovider.core.authentication.IdTokenGenerator
import myoidcprovider.core.authorization.AccessTokenGenerator
import myoidcprovider.core.authorization.AuthorizationCodeGenerator
import myoidcprovider.core.issuer.IssuerConfig
import myoidcprovider.core.metadata.ResponseType
import myoidcprovider.core.request.authorization.AuthorizationErrorCode
import myoidcprovider.core.request.authorization.AuthorizationResponse
import myoidcprovider.core.request.authorization.AuthorizationResponseError
import myoidcprovider.core.storage.AccessTokenStorage
import myoidcprovider.core.storage.AuthorizationCodeStorage
import myoidcprovider.core.storage.AuthorizationRequestDataStorage
import myoidcprovider.core.storage.ClientConfigStorage
import myoidcprovider.core.storage.JWKConfigStorage
import myoidcprovider.core.storage.UserClaimSetStorage
import myoidcprovider.core.util.Clock

/**
 * 認可リクエストの認証後のハンドラー。
 */
class AuthorizationRequestPostProcessHandler(
    private val clientConfigStorage: ClientConfigStorage,
    private val authorizationRequestDataStorage: AuthorizationRequestDataStorage,
    private val accessTokenStorage: AccessTokenStorage,
    private val authorizationCodeStorage: AuthorizationCodeStorage,
    jwkConfigStorage: JWKConfigStorage,
    userClaimSetStorage: UserClaimSetStorage,
    private val clock: Clock,
) {
    private val accessTokenGenerator = AccessTokenGenerator(clock)
    private val authorizationCodeGenerator = AuthorizationCodeGenerator(clock)
    private val idTokenGenerator = IdTokenGenerator(jwkConfigStorage, userClaimSetStorage, clock)

    /**
     * リクエストを処理する。
     *
     * @param issuer Issuer
     * @param subject 識別子
     * @param authorizationRequestDataKey 認可リクエストのキー
     * @param consent 認可リクエストの同意有無
     * @return 成功した場合は [AuthorizationResponse]。失敗した場合は [AuthorizationResponseError]。
     */
    fun handle(
        issuer: IssuerConfig,
        subject: String,
        authorizationRequestDataKey: String,
        consent: Boolean,
    ): Result<AuthorizationResponse, AuthorizationResponseError> = binding {
        val authorizationRequestData = authorizationRequestDataStorage.findByKey(
            issuer.issuer,
            authorizationRequestDataKey,
        ).toResultOr {
            AuthorizationResponseError.InvalidRequest
        }.bind()

        if (authorizationRequestData.expiresAt < clock.getEpochSecond()) {
            Err(AuthorizationResponseError.InvalidRequest).bind<Unit>()
        }

        val client = clientConfigStorage.findById(issuer.issuer, authorizationRequestData.authorizationRequest.clientId)
            .toResultOr {
                AuthorizationResponseError.InvalidRequest
            }.bind()

        if (!consent) {
            authorizationRequestDataStorage.delete(authorizationRequestData)

            Err(
                AuthorizationResponseError.ErrorResponse(
                    authorizationRequestData.authorizationRequest.redirectUri,
                    AuthorizationErrorCode.ACCESS_DENIED,
                    "",
                    null,
                    authorizationRequestData.authorizationRequest.state,
                ),
            ).bind<Unit>()
        }

        val accessToken = if (authorizationRequestData.authorizationRequest.responseType.contains(ResponseType.TOKEN)) {
            val accessToken = accessTokenGenerator.generate(
                issuer,
                client,
                authorizationRequestData.authorizationRequest.scope,
                subject,
            )

            accessTokenStorage.save(accessToken)

            accessToken
        } else {
            null
        }

        val token = accessToken?.let {
            AuthorizationResponse.Token(
                accessToken.token,
                accessToken.tokenType,
                accessToken.expiresIn,
                accessToken.scope?.joinToString(" "),
            )
        }

        val authorizationCode = if (authorizationRequestData.authorizationRequest.responseType.contains(
                ResponseType.CODE,
            )
        ) {
            val authorizationCode = authorizationCodeGenerator.generate(
                issuer,
                client,
                authorizationRequestData.authorizationRequest,
                authorizationRequestData.authenticationRequest,
                subject,
            )

            authorizationCodeStorage.save(authorizationCode)

            authorizationCode
        } else {
            null
        }

        val code = authorizationCode?.let {
            AuthorizationResponse.Code(authorizationCode.code)
        }

        val idToken =
            if (authorizationRequestData.authorizationRequest.responseType.contains(ResponseType.ID_TOKEN) &&
                authorizationCode?.authenticationRequest != null
            ) {
                AuthorizationResponse.IdToken(
                    idTokenGenerator.generate(
                        issuer,
                        client,
                        authorizationCode.authenticationRequest,
                        accessToken,
                        authorizationCode.code,
                        subject,
                    ),
                )
            } else {
                null
            }

        authorizationRequestDataStorage.delete(authorizationRequestData)

        AuthorizationResponse(
            token,
            code,
            idToken,
            authorizationRequestData.authorizationRequest.redirectUri,
            authorizationRequestData.authorizationRequest.responseType,
            authorizationRequestData.authorizationRequest.responseMode,
            authorizationRequestData.authorizationRequest.state,
        )
    }
}
