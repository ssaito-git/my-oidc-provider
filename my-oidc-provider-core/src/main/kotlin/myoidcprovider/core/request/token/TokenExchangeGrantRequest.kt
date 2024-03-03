package myoidcprovider.core.request.token

import myoidcprovider.core.metadata.TokenType
import myoidcprovider.core.metadata.GrantType

/**
 * トークンエクスチェンジグラントリクエスト。
 */
data class TokenExchangeGrantRequest(
    /**
     * grant_type (REQUIRED)
     *
     * `urn:ietf:params:oauth:grant-type:token-exchange` の値固定。
     */
    val grantType: GrantType,
    /**
     * resource (OPTIONAL)
     *
     * セキュリティトークンを使用する対象のサービスまたはリソースの URI。
     * [RFC 8707 - Resource Indicators for OAuth 2.0](https://datatracker.ietf.org/doc/html/rfc8707)
     */
    val resource: List<String>?,
    /**
     * audience (OPTIONAL)
     *
     * セキュリティトークンを使用する対象のサービスまたはリソースの名前。
     */
    val audience: List<String>?,
    /**
     * scope (OPTIONAL)
     *
     * 発行するセキュリティトークンのスコープ。
     */
    val scope: List<String>?,
    /**
     * requested_token_type (OPTIONAL)
     *
     * 発行するセキュリティトークンのタイプ。
     * 指定されていない場合は認可サーバーによって決まる。
     */
    val requestedTokenType: TokenType?,
    /**
     * subject_token (REQUIRED)
     *
     * リクエストの当事者を表すセキュリティトークン。
     * 発行されるセキュリティトークンのサブジェクトになる。
     */
    val subjectToken: String,
    /**
     * subject_token_type (REQUIRED)
     *
     * `subject_token` のトークンタイプ。
     */
    val subjectTokenType: TokenType,
    /**
     * actor_token (OPTIONAL)
     *
     * 代行者を表すセキュリティトークン。
     */
    val actorToken: String?,
    /**
     * actor_token_type
     *
     * `actor_token` のトークンタイプ。
     * `actor_token` が含まれている場合は必須。それ以外は含めてはならない。
     */
    val actorTokenType: TokenType?,
)