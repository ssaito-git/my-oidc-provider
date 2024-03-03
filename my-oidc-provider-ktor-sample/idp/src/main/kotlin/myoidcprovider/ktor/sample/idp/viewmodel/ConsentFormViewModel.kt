package myoidcprovider.ktor.sample.idp.viewmodel

data class ConsentFormViewModel(val clientName: String, val scopes: List<ConsentFormScopeViewModel>)