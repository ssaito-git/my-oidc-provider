plugins {
    id("io.gitlab.arturbosch.detekt")
}

detekt {
    config.setFrom("$rootDir/config/detekt.yml")
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.1")
}
