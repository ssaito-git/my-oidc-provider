plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.serialization)
    `java-library`
    `maven-publish`
    `myoidcprovider-detekt`
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":my-oidc-provider-core"))

    // Ktor
    implementation(platform(libs.ktor.bom))
    implementation(libs.ktor.server.core.jvm)
    implementation(libs.ktor.serialization.kotlinx.json)
}

kotlin {
    jvmToolchain(17)
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "myoidcprovider"
            artifactId = "ktor"
            version = "0.0.1"

            from(components["java"])
        }
    }
}
