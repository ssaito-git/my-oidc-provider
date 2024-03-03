plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.9.0"
    id("io.ktor.plugin") version "2.3.8"
    `java-library`
    `maven-publish`
    `myoidcprovider-detekt`
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":my-oidc-provider-core"))
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
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
