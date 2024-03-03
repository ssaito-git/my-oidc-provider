plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.9.0"
    id("io.ktor.plugin") version "2.3.8"
    `myoidcprovider-detekt`
}

repositories {
    mavenCentral()
}

dependencies {
    // ktor server
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-sessions-jvm")
    implementation("io.ktor:ktor-server-freemarker")
    implementation("io.ktor:ktor-server-freemarker-jvm")
    implementation("io.ktor:ktor-server-auth")
    implementation("ch.qos.logback:logback-classic:1.4.14")

    // ktor client
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-client-content-negotiation")

    // JWT
    implementation("com.nimbusds:nimbus-jose-jwt:9.37.3")
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("myoidcprovider.ktor.sample.rp.MainKt")
}
