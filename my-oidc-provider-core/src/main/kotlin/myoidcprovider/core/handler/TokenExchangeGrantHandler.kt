package myoidcprovider.core.handler

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.toResultOr
import myoidcprovider.core.authorization.AccessToken
import myoidcprovider.core.authorization.RefreshToken
import myoidcprovider.core.authorization.SecurityTokenGenerator
import myoidcprovider.core.authorization.TokenExchangeRequestData
import myoidcprovider.core.client.authentication.ClientAuthenticationError
import myoidcprovider.core.client.authentication.ClientAuthenticationManager
import myoidcprovider.core.http.HttpRequest
import myoidcprovider.core.issuer.IssuerConfig
import myoidcprovider.core.metadata.TokenType
import myoidcprovider.core.request.token.TokenErrorCode
import myoidcprovider.core.request.token.TokenExchangeGrantRequest
import myoidcprovider.core.request.token.TokenExchangeGrantRequestConverter
import myoidcprovider.core.request.token.TokenExchangeRequestParameter
import myoidcprovider.core.request.token.TokenRequestError
import myoidcprovider.core.request.token.TokenResponse
import myoidcprovider.core.storage.AccessTokenStorage
import myoidcprovider.core.storage.RefreshTokenStorage
import myoidcprovider.core.util.Clock

/**
 * トークンエクスチェンジグラントリクエスト
 */
class TokenExchangeGrantHandler(
    /**
     * クライアント認証マネージャー
     */
    private val clientAuthenticationManager: ClientAuthenticationManager,
    /**
     * アクセストークンストレージ
     */
    private val accessTokenStorage: AccessTokenStorage,
    /**
     * リフレッシュトークンストレージ
     */
    private val refreshTokenStorage: RefreshTokenStorage,
    /**
     * セキュリティトークンジェネレーター
     */
    private val securityTokenGenerator: SecurityTokenGenerator,
    /**
     * クロック
     */
    private val clock: Clock,
) {
    private val tokenExchangeGrantRequestConverter = TokenExchangeGrantRequestConverter()

    /**
     * トークンエクスチェンジグラントのリクエストを処理する。
     *
     * @param issuer Issuer
     * @param httpRequest HTTP リクエスト
     * @return 成功した場合は [TokenResponse]。失敗した場合は [TokenRequestError]。
     */
    fun handle(
        issuer: IssuerConfig,
        httpRequest: HttpRequest,
    ): Result<TokenResponse, TokenRequestError> = binding {
        val authenticatedClient = clientAuthenticationManager.authenticate(issuer, httpRequest).mapError {
            when (it) {
                is ClientAuthenticationError.InvalidCredentials,
                ClientAuthenticationError.UnmatchedAuthenticationMethod,
                -> TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_CLIENT,
                    "Invalid client.",
                )

                is ClientAuthenticationError.InvalidRequest -> TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_REQUEST,
                    it.errorDescription,
                )
            }
        }.bind()

        val tokenExchangeGrantRequest = tokenExchangeGrantRequestConverter.convert(
            issuer,
            authenticatedClient,
            httpRequest,
        ).bind()
        val subjectToken = validateSubjectToken(issuer, tokenExchangeGrantRequest).bind()
        val actorToken = validateActorToken(issuer, tokenExchangeGrantRequest).bind()
        val requestData = TokenExchangeRequestData(
            authenticatedClient,
            tokenExchangeGrantRequest,
            subjectToken,
            actorToken,
        )

        // リクエストの検証
        // - resource (OPTIONAL)
        //   セキュリティトークンを使用する対象のサービスまたはリソースの URI。
        // - audience (OPTIONAL)
        //   セキュリティトークンを使用する対象のサービスまたはリソースの名前。
        // - scope (OPTIONAL)
        //   発行するセキュリティトークンのスコープ。
        // - requested_token_type (OPTIONAL)
        //   発行するセキュリティトークンのタイプ。
        // - subject_token (subject_token_type)
        // - actor_token (actor_token_type)

        // 1. subject_token の検証
        // 2. actor_token の検証
        // 3. resource の権限があるか検証
        // 4. audience の権限があるか検証
        // 5. scope の権限があるか検証

        // トークンの発行
        // - token
        //   - access_token
        //   - refresh_token
        //   - id_token
        //   - saml1
        //   - saml2
        //   - jwt
        // - token_type
        //   - Bearer
        //   - N_A

        // 1.

        securityTokenGenerator.generate(issuer, requestData).bind()
    }

    private fun validateSubjectToken(
        issuer: IssuerConfig,
        request: TokenExchangeGrantRequest,
    ): Result<SubjectToken, TokenRequestError> {
        return when (request.subjectTokenType) {
            TokenType.ACCESS_TOKEN -> {
                validateAccessToken(
                    issuer,
                    request.subjectToken,
                    TokenExchangeRequestParameter.SUBJECT_TOKEN,
                ).andThen {
                    Ok(SubjectToken.AccessToken(it))
                }
            }

            TokenType.REFRESH_TOKEN -> {
                validateRefreshToken(
                    issuer,
                    request.subjectToken,
                    TokenExchangeRequestParameter.SUBJECT_TOKEN,
                ).andThen {
                    Ok(SubjectToken.RefreshToken(it))
                }
            }

            TokenType.ID_TOKEN -> Ok(SubjectToken.IdToken(request.subjectToken))
            TokenType.SAML1 -> Ok(SubjectToken.Saml1(request.subjectToken))
            TokenType.SAML2 -> Ok(SubjectToken.Saml2(request.subjectToken))
            TokenType.JWT -> Ok(SubjectToken.Jwt(request.subjectToken))
        }
    }

    private fun validateActorToken(
        issuer: IssuerConfig,
        request: TokenExchangeGrantRequest,
    ): Result<ActorToken?, TokenRequestError> {
        if (request.actorToken == null) {
            return Ok(null)
        }

        return when (request.actorTokenType) {
            TokenType.ACCESS_TOKEN, null -> {
                validateAccessToken(
                    issuer,
                    request.subjectToken,
                    TokenExchangeRequestParameter.ACTOR_TOKEN,
                ).andThen {
                    Ok(ActorToken.AccessToken(it))
                }
            }

            TokenType.REFRESH_TOKEN -> {
                validateRefreshToken(
                    issuer,
                    request.subjectToken,
                    TokenExchangeRequestParameter.ACTOR_TOKEN,
                ).andThen {
                    Ok(ActorToken.RefreshToken(it))
                }
            }

            TokenType.ID_TOKEN -> Ok(ActorToken.IdToken(request.actorToken))
            TokenType.SAML1 -> Ok(ActorToken.Saml1(request.actorToken))
            TokenType.SAML2 -> Ok(ActorToken.Saml2(request.actorToken))
            TokenType.JWT -> Ok(ActorToken.Jwt(request.actorToken))
        }
    }

    private fun validateAccessToken(
        issuer: IssuerConfig,
        subjectToken: String,
        parameterName: String,
    ): Result<AccessToken, TokenRequestError> {
        return accessTokenStorage.findByToken(issuer.issuer, subjectToken)
            .toResultOr {
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_REQUEST,
                    "'$parameterName' is invalid.",
                )
            }.andThen {
                if (it.expiresAt < clock.getEpochSecond()) {
                    Err(
                        TokenRequestError.ErrorResponse(
                            TokenErrorCode.INVALID_REQUEST,
                            "'$parameterName' is expired.",
                        ),
                    )
                } else {
                    Ok(it)
                }
            }
    }

    private fun validateRefreshToken(
        issuer: IssuerConfig,
        subjectToken: String,
        parameterName: String,
    ): Result<RefreshToken, TokenRequestError> {
        return refreshTokenStorage.findByToken(issuer.issuer, subjectToken)
            .toResultOr {
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_REQUEST,
                    "'$parameterName' is invalid.",
                )
            }.andThen {
                if (it.expiresAt < clock.getEpochSecond()) {
                    Err(
                        TokenRequestError.ErrorResponse(
                            TokenErrorCode.INVALID_REQUEST,
                            "'$parameterName' is expired.",
                        ),
                    )
                } else {
                    Ok(it)
                }
            }
    }
}

/**
 * セキュリティトークンジェネレーターのデフォルト実装
 */
class DefaultSecurityTokenGenerator : SecurityTokenGenerator {
    override fun generate(
        issuer: IssuerConfig,
        requestData: TokenExchangeRequestData,
    ): Result<TokenResponse, TokenRequestError> {
        return Err(TokenRequestError.ErrorResponse(TokenErrorCode.UNSUPPORTED_GRANT_TYPE, "Unsupported grant type."))
    }
}

/**
 * サブジェクトトークン
 */
sealed interface SubjectToken {
    /**
     * アクセストークン
     */
    class AccessToken(token: myoidcprovider.core.authorization.AccessToken) : SubjectToken

    /**
     * リフレッシュトークン
     */
    class RefreshToken(token: myoidcprovider.core.authorization.RefreshToken) : SubjectToken

    /**
     * ID トークン
     */
    class IdToken(token: String) : SubjectToken

    /**
     * SAML 1.1 アサーション
     */
    class Saml1(token: String) : SubjectToken

    /**
     * SAML 2.0 アサーション
     */
    class Saml2(token: String) : SubjectToken

    /**
     * JWT
     */
    class Jwt(token: String) : SubjectToken
}

/**
 * アクタートークン
 */
sealed interface ActorToken {
    /**
     * アクセストークン
     */
    class AccessToken(token: myoidcprovider.core.authorization.AccessToken) : ActorToken

    /**
     * リフレッシュトークン
     */
    class RefreshToken(token: myoidcprovider.core.authorization.RefreshToken) : ActorToken

    /**
     * ID トークン
     */
    class IdToken(token: String) : ActorToken

    /**
     * SAML 1.1 アサーション
     */
    class Saml1(token: String) : ActorToken

    /**
     * SAML 2.0 アサーション
     */
    class Saml2(token: String) : ActorToken

    /**
     * JWT
     */
    class Jwt(token: String) : ActorToken
}
