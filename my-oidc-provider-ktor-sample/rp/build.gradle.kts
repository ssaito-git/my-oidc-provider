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

    // ktor client
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)

    // JWT
    implementation(libs.nimbus.jose.jwt)
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("myoidcprovider.ktor.sample.rp.MainKt")
}
