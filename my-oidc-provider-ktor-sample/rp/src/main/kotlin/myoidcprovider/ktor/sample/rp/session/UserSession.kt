package myoidcprovider.ktor.sample.rp.session

/**
 * ユーザーセッション
 *
 * @property subject サブジェクト
 * @property name 名前
 */
data class UserSession(val subject: String, val name: String)
