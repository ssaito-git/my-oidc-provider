package myoidcprovider.ktor.sample.idp.viewmodel

data class MfaFormViewModel(val codeInputError: String? = null, val verifyError: String? = null)