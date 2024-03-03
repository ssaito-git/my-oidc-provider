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
    api("com.nimbusds:nimbus-jose-jwt:9.34")
    api("com.michael-bull.kotlin-result:kotlin-result:1.1.18")
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
