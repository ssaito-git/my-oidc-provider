package myoidcprovider.core.request.authorization

/**
 * 認可リクエストパラメーター
 */
object AuthorizationRequestParameter {
    /**
     * response_type (REQUIRED)
     *
     * レスポンスタイプ
     */
    const val RESPONSE_TYPE = "response_type"

    /**
     * client_id (REQUIRED)
     *
     * クライアント ID
     */
    const val CLIENT_ID = "client_id"

    /**
     * redirect_uri (OPTIONAL)
     *
     * リダイレクト URI
     */
    const val REDIRECT_URI = "redirect_uri"

    /**
     * scope (OPTIONAL)
     *
     * スコープ
     */
    const val SCOPE = "scope"

    /**
     * state (RECOMMENDED)
     *
     * ステート
     */
    const val STATE = "state"

    /**
     * response_mode (OPTIONAL)
     *
     * レスポンスモード
     * [OAuth 2.0 Multiple Response Type Encoding Practices](https://openid.net/specs/oauth-v2-multiple-response-types-1_0.html)
     */
    const val RESPONSE_MODE = "response_mode"

    /**
     * code_challenge (PKCE) (REQUIRED*)
     *
     * コードチャレンジ
     * PKCE を使用する場合は必須。
     */
    const val CODE_CHALLENGE = "code_challenge"

    /**
     * code_challenge_method (PKCE) (OPTIONAL)
     *
     * コードチャレンジメソッド
     */
    const val CODE_CHALLENGE_METHOD = "code_challenge_method"

    /**
     * nonce
     *
     * （任意）
     */
    const val NONCE = "nonce"

    /**
     * display
     *
     * 認証および同意のための UI をどのように表示するか指定する（任意）
     */
    const val DISPLAY = "display"

    /**
     * prompt
     *
     * 再認証および同意を要求するかどうかを指定する（任意）
     */
    const val PROMPT = "prompt"

    /**
     * max_age
     *
     * エンドユーザーが明示的に認証されてからの経過時間の最大許容値（秒）（任意）
     */
    const val MAX_AGE = "max_age"

    /**
     * ui_locales
     *
     * UI の表示言語および文字種のリスト（任意）
     */
    const val UI_LOCALES = "ui_locales"

    /**
     * id_token_hint
     *
     * 認可サーバーが以前発行した ID Token（任意）
     */
    const val ID_TOKEN_HINT = "id_token_hint"

    /**
     * login_hint
     *
     * エンドユーザーのログイン識別子のヒント（任意）
     */
    const val LOGIN_HINT = "login_hint"

    /**
     * acr_values
     *
     * 認証コンテキストクラス参照（任意）
     */
    const val ACR_VALUES = "acr_values"
}
