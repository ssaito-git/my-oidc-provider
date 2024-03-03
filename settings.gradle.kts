rootProject.name = "my-oidc-provider"
include(
    "my-oidc-provider-core",
    "my-oidc-provider-ktor",
    "my-oidc-provider-ktor-sample:idp",
    "my-oidc-provider-ktor-sample:rp",
)
