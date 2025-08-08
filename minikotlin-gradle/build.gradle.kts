plugins {
    kotlin("jvm") version "2.2.0"
    id("com.gradle.plugin-publish") version "1.3.1"
    `kotlin-dsl`
}

group = "org.bezsahara"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}
gradlePlugin {
    website = "https://github.com/bezsahara/minikotlin"
    vcsUrl = "https://github.com/bezsahara/minikotlin.git"

    plugins {
        create("minikotlinPlugin", Action {
            displayName = "MiniKotlin Gradle Plugin"
            description = "Runs your MiniKotlin generators so that classes are produced during build."
            tags = listOf("kotlin", "codegen", "bytecode", "compiletime", "dsl")

            id = "org.bezsahara.minikotlin"
            implementationClass = "org.bezsahara.minikotlin.gradle.MiniKotlinPlugin"
        })
    }
}