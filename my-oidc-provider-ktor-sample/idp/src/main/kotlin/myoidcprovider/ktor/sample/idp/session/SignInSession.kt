package myoidcprovider.ktor.sample.idp.session

import io.ktor.server.auth.Principal
import java.util.UUID

/**
 * サインインセッション
 *
 * @property subject サブジェクト
 */
data class SignInSession(val subject: String) : Principal
