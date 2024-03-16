import org.gradle.kotlin.dsl.java

plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
    `myoidcprovider-detekt`
}

repositories {
    mavenCentral()
}

dependencies {
    api(libs.nimbus.jose.jwt)
    api(libs.kotlin.result)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
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
            artifactId = "core"
            version = "0.0.1"

            from(components["java"])
        }
    }
}
