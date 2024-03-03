package myoidcprovider.core.handler

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.toResultOr
import myoidcprovider.core.authentication.IdTokenGenerator
import myoidcprovider.core.authorization.AccessTokenGenerator
import myoidcprovider.core.authorization.AuthorizationCode
import myoidcprovider.core.authorization.RefreshTokenGenerator
import myoidcprovider.core.client.ClientConfig
import myoidcprovider.core.client.ClientType
import myoidcprovider.core.client.authentication.ClientAuthenticationError
import myoidcprovider.core.client.authentication.ClientAuthenticationManager
import myoidcprovider.core.http.HttpRequest
import myoidcprovider.core.issuer.IssuerConfig
import myoidcprovider.core.metadata.PKCECodeChallengeMethod
import myoidcprovider.core.request.authorization.AuthorizationRequest
import myoidcprovider.core.request.token.AuthorizationCodeGrantRequest
import myoidcprovider.core.request.token.AuthorizationCodeGrantRequestConverter
import myoidcprovider.core.request.token.TokenErrorCode
import myoidcprovider.core.request.token.TokenRequestError
import myoidcprovider.core.request.token.TokenResponse
import myoidcprovider.core.storage.AccessTokenStorage
import myoidcprovider.core.storage.AuthorizationCodeStorage
import myoidcprovider.core.storage.ClientConfigStorage
import myoidcprovider.core.storage.JWKConfigStorage
import myoidcprovider.core.storage.RefreshTokenStorage
import myoidcprovider.core.storage.UserClaimSetStorage
import myoidcprovider.core.util.Clock
import java.security.MessageDigest
import java.util.Base64

/**
 * 認可コードグラントのハンドラー。
 */
class AuthorizationCodeGrantHandler(
    /**
     * クライアント認証マネージャー
     */
    private val clientAuthenticationManager: ClientAuthenticationManager,
    /**
     * 認可コードストレージ
     */
    private val authorizationCodeStorage: AuthorizationCodeStorage,
    /**
     * クライアントストレージ
     */
    private val clientConfigStorage: ClientConfigStorage,
    /**
     * アクセストークンストレージ
     */
    private val accessTokenStorage: AccessTokenStorage,
    /**
     * リフレッシュトークンストレージ
     */
    private val refreshTokenStorage: RefreshTokenStorage,
    /**
     * JWK コンフィグストレージ
     */
    jwkConfigStorage: JWKConfigStorage,
    /**
     * ユーザークレームストレージ
     */
    userClaimSetStorage: UserClaimSetStorage,
    /**
     * クロック
     */
    private val clock: Clock,
) {
    private val authorizationCodeGrantRequestConverter = AuthorizationCodeGrantRequestConverter()
    private val accessTokenGenerator = AccessTokenGenerator(clock)
    private val refreshTokenGenerator = RefreshTokenGenerator(clock)
    private val idTokenGenerator = IdTokenGenerator(jwkConfigStorage, userClaimSetStorage, clock)

    /**
     * 認可コードグラントのリクエストを処理する。
     *
     * @param issuer Issuer
     * @param httpRequest HTTP リクエスト
     * @return 成功した場合は [TokenResponse]。失敗した場合は [TokenRequestError]。
     */
    fun handle(issuer: IssuerConfig, httpRequest: HttpRequest): Result<TokenResponse, TokenRequestError> = binding {
        val authenticatedClient = authenticateClient(issuer, httpRequest).bind()

        val authorizationCodeGrantRequest = authorizationCodeGrantRequestConverter.convert(issuer, httpRequest).bind()

        val authorizationCode = authorizationCodeStorage.findByCode(issuer.issuer, authorizationCodeGrantRequest.code)
            .toResultOr {
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_GRANT,
                    "Authorization code is invalid.",
                )
            }.bind()

        verifyAuthorizationCode(authorizationCodeGrantRequest, authorizationCode).bind()

        val client = verifyClient(
            issuer,
            authenticatedClient,
            authorizationCode.authorizationRequest,
            authorizationCodeGrantRequest,
        ).bind()

        verifyPKCE(authorizationCode.authorizationRequest, authorizationCodeGrantRequest).bind()

        val accessToken = accessTokenGenerator.generate(
            issuer,
            client,
            authorizationCode.authorizationRequest.scope,
            authorizationCode.subject,
        )

        accessTokenStorage.save(accessToken)

        val refreshToken = if (authorizationCode.authorizationRequest.scope?.contains("offline_access") == true) {
            val refreshToken = refreshTokenGenerator.generate(
                issuer,
                client,
                authorizationCode.authorizationRequest.scope,
                authorizationCode.subject,
            )

            refreshTokenStorage.save(refreshToken)

            refreshToken
        } else {
            null
        }

        val idToken = authorizationCode.authenticationRequest?.let {
            idTokenGenerator.generate(
                issuer,
                client,
                it,
                accessToken,
                authorizationCode.code,
                authorizationCode.subject,
            )
        }

        TokenResponse(
            accessToken.token,
            accessToken.tokenType,
            accessToken.expiresIn,
            refreshToken?.token,
            authorizationCode.authorizationRequest.scope,
            idToken,
        )
    }

    private fun authenticateClient(
        issuerConfig: IssuerConfig,
        httpRequest: HttpRequest,
    ): Result<ClientConfig?, TokenRequestError> {
        return clientAuthenticationManager.authenticate(issuerConfig, httpRequest).fold({ Ok(it) }) {
            when (it) {
                is ClientAuthenticationError.InvalidCredentials -> Err(
                    TokenRequestError.ErrorResponse(
                        TokenErrorCode.INVALID_CLIENT,
                        "Invalid credentials.",
                    ),
                )

                is ClientAuthenticationError.InvalidRequest -> Err(
                    TokenRequestError.ErrorResponse(
                        TokenErrorCode.INVALID_REQUEST,
                        it.errorDescription,
                    ),
                )

                ClientAuthenticationError.UnmatchedAuthenticationMethod -> Ok(null)
            }
        }
    }

    private fun verifyAuthorizationCode(
        authorizationCodeGrantRequest: AuthorizationCodeGrantRequest,
        authorizationCode: AuthorizationCode,
    ): Result<Unit, TokenRequestError> = binding {
        if (authorizationCode.expiresAt < clock.getEpochSecond()) {
            Err(
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_GRANT,
                    "Authorization code expired.",
                ),
            ).bind<Unit>()
        }

        if (authorizationCodeGrantRequest.redirectUri != authorizationCode.authorizationRequest.redirectUri) {
            Err(
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_GRANT,
                    "Redirect URI is invalid.",
                ),
            ).bind<Unit>()
        }
    }

    private fun verifyClient(
        issuerConfig: IssuerConfig,
        authenticatedClient: ClientConfig?,
        authorizationRequest: AuthorizationRequest,
        authorizationCodeGrantRequest: AuthorizationCodeGrantRequest,
    ): Result<ClientConfig, TokenRequestError> = binding {
        val client = clientConfigStorage.findById(issuerConfig.issuer, authorizationRequest.clientId)
            .toResultOr {
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_GRANT,
                    "Unknown client.",
                )
            }.bind()

        if (authenticatedClient != null && authenticatedClient.id != client.id) {
            Err(
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_GRANT,
                    "Authorization code is invalid.",
                ),
            ).bind<Unit>()
        }

        when (client.type) {
            ClientType.CONFIDENTIAL -> {
                if (authenticatedClient == null) {
                    Err(
                        TokenRequestError.ErrorResponse(
                            TokenErrorCode.INVALID_CLIENT,
                            "Client authentication required.",
                        ),
                    ).bind<Unit>()
                }
            }
            ClientType.PUBLIC -> {
                if (authorizationCodeGrantRequest.clientId == null) {
                    Err(
                        TokenRequestError.ErrorResponse(
                            TokenErrorCode.INVALID_CLIENT,
                            "'client_id' is required.",
                        ),
                    ).bind<Unit>()
                }

                if (authorizationCodeGrantRequest.clientId != client.id) {
                    Err(
                        TokenRequestError.ErrorResponse(
                            TokenErrorCode.INVALID_CLIENT,
                            "Invalid client.",
                        ),
                    ).bind<Unit>()
                }
            }
        }

        client
    }

    private fun verifyPKCE(
        authorizationRequest: AuthorizationRequest,
        authorizationCodeGrantRequest: AuthorizationCodeGrantRequest,
    ): Result<Unit, TokenRequestError> {
        if (authorizationRequest.codeChallenge == null) {
            return Ok(Unit)
        }

        if (authorizationCodeGrantRequest.codeVerifier == null) {
            return Err(
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_REQUEST,
                    "'code_challenge' is required.",
                ),
            )
        }

        val result = when (authorizationRequest.codeChallengeMethod) {
            PKCECodeChallengeMethod.PLAIN, null -> {
                authorizationCodeGrantRequest.codeVerifier == authorizationRequest.codeChallenge
            }
            PKCECodeChallengeMethod.S256 -> {
                val hash = MessageDigest.getInstance(
                    "SHA-256",
                ).digest(authorizationCodeGrantRequest.codeVerifier.toByteArray())
                val codeChallenge = Base64.getUrlEncoder().withoutPadding().encodeToString(hash)

                codeChallenge == authorizationRequest.codeChallenge
            }
        }

        return if (result) {
            Ok(Unit)
        } else {
            Err(
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_GRANT,
                    "'code_challenge' is invalid.",
                ),
            )
        }
    }
}
