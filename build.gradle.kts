import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.nio.file.Path


plugins {
    kotlin("jvm") version "2.2.0"
    id("com.gradleup.shadow") version "9.0.0-rc1"
}

group = "org.bezsahara"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.test {
    systemProperty("compiled_folder", project.file("compiled/temp").absolutePath)
}

tasks.register("sss") {
    println("Gradle version: ${gradle.gradleVersion}")
}

val fatJar = tasks.register<ShadowJar>("fatJar") {
    archiveClassifier.set("all")
    mergeServiceFiles()
    from(sourceSets.main.get().output)
}

val sourcesJar = tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

tasks.register("publishJar") {
    group = "build"
    description = "Builds fatJar and sourcesJar"

    dependsOn(fatJar, sourcesJar)
}

tasks.withType<JavaExec>().configureEach {
    jvmArgs("-DmyKey=myValue")
}

dependencies {
    implementation("org.ow2.asm:asm:9.8")
    implementation("org.ow2.asm:asm-util:9.8")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.2.0")
//    implementation(kotlin("reflect"))
    implementation("org.eclipse.collections:eclipse-collections:11.1.0")

    testImplementation(kotlin("test"))
    // https://mvnrepository.com/artifact/org.testng/testng
//    testImplementation("org.testng:testng:7.11.0")
}

tasks.test {
//    useTestNG()
    useJUnitPlatform()

    // feed JUnit‑Platform properties via the JVM
    systemProperty("junit.jupiter.execution.parallel.enabled", "true")
    systemProperty("junit.jupiter.execution.parallel.mode.default", "concurrent")
    systemProperty("junit.jupiter.execution.parallel.config.strategy", "fixed")
    systemProperty("junit.jupiter.execution.parallel.config.fixed.parallelism",
        Runtime.getRuntime().availableProcessors().toString())
}
kotlin {
    this.compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
    jvmToolchain(23)
}