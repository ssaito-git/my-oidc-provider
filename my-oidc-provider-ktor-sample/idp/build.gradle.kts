plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktor)
    `myoidcprovider-detekt`
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":my-oidc-provider-ktor"))

    // ktor server
    implementation(libs.ktor.server.core.jvm)
    implementation(libs.ktor.server.netty.jvm)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.sessions.jvm)
    implementation(libs.ktor.server.freemarker)
    implementation(libs.ktor.server.freemarker.jvm)
    implementation(libs.ktor.server.auth)
    implementation(libs.logback.classic)

    // webauthn
    implementation(libs.webauthn4j.core)

    // jackson
    implementation(libs.jackson.databind)
    implementation(libs.jackson.dataformat.cbor)
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("myoidcprovider.ktor.sample.idp.MainKt")
}
