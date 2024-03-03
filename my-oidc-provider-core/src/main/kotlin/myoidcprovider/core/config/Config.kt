package myoidcprovider.core.config

import myoidcprovider.core.authorization.SecurityTokenGenerator
import myoidcprovider.core.client.authentication.ClientAuthenticationManager
import myoidcprovider.core.handler.DefaultSecurityTokenGenerator
import myoidcprovider.core.storage.AccessTokenStorage
import myoidcprovider.core.storage.AuthorizationCodeStorage
import myoidcprovider.core.storage.AuthorizationRequestDataStorage
import myoidcprovider.core.storage.ClientConfigStorage
import myoidcprovider.core.storage.IssuerConfigStorage
import myoidcprovider.core.storage.JWKConfigStorage
import myoidcprovider.core.storage.RefreshTokenStorage
import myoidcprovider.core.storage.UserClaimSetStorage
import myoidcprovider.core.util.Clock

/**
 * Provider の設定。
 */
data class Config(
    /**
     * イシュアーコンフィグストレージ。
     */
    val issuerConfigStorage: IssuerConfigStorage,
    /**
     * JWK コンフィグストレージ。
     */
    val jwkConfigStorage: JWKConfigStorage,
    /**
     * クライアントコンフィグストレージ。
     */
    val clientConfigStorage: ClientConfigStorage,
    /**
     * 認可リクエストストレージ。
     */
    val authorizationRequestDataStorage: AuthorizationRequestDataStorage,
    /**
     * 認可コードストレージ。
     */
    val authorizationCodeStorage: AuthorizationCodeStorage,
    /**
     * アクセストークンストレージ。
     */
    val accessTokenStorage: AccessTokenStorage,
    /**
     * リフレッシュトークンストレージ。
     */
    val refreshTokenStorage: RefreshTokenStorage,
    /**
     * ユーザークレームストレージ。
     */
    val userClaimSetStorage: UserClaimSetStorage,
    /**
     * クライアント認証マネージャー。
     */
    val clientAuthenticationManager: ClientAuthenticationManager,
    /**
     * セキュリティトークンジェネレーター。
     */
    val securityTokenGenerator: SecurityTokenGenerator = DefaultSecurityTokenGenerator(),
    /**
     * エンドポイント。
     */
    val endpoint: Endpoint = Endpoint(),
    /**
     * 現在時刻を提供するクロック。
     */
    val clock: Clock = Clock.SystemClock,
)
