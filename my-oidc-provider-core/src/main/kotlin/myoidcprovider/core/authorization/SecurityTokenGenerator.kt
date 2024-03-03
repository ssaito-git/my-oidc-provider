package myoidcprovider.core.authorization

import com.github.michaelbull.result.Result
import myoidcprovider.core.client.ClientConfig
import myoidcprovider.core.issuer.IssuerConfig
import myoidcprovider.core.request.token.TokenExchangeGrantRequest
import myoidcprovider.core.request.token.TokenRequestError
import myoidcprovider.core.request.token.TokenResponse

/**
 * セキュリティトークンジェネレーター
 */
interface SecurityTokenGenerator {
    /**
     * セキュリティトークンを作成する。
     *
     * @param issuer Issuer
     * @param requestData トークンエクスチェンジグラントリクエストデータ
     * @return セキュリティトークンの作成に成功した場合は [TokenExchangeGrantRequest]。失敗した場合は [TokenRequestError]。
     */
    fun generate(
        issuer: IssuerConfig,
        requestData: TokenExchangeRequestData,
    ): Result<TokenResponse, TokenRequestError>
}