package myoidcprovider.ktor.sample.idp.viewmodel

data class LoginFormViewModel (
    val username: String? = null,
    val usernameInputError: String? = null,
    val passwordInputError: String? = null,
    val loginError: String? = null,
)