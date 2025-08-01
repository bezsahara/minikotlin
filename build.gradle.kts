import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.bezsahara.minikotlin.gradle.MiniKotlinRunMode

plugins {
    kotlin("jvm") version "2.2.0"
    id("org.bezsahara.minikotlin")
    id("com.gradleup.shadow") version "9.0.0-rc1"
}

group = "org.bezsahara"
version = "1.0-SNAPSHOT"



minikotlin {
    // MANUAL - wire tasks manually
    // AUTO - plugin wires generateMiniKotlin after classes
    // default is AUTO
    runMode = MiniKotlinRunMode.AUTO

    // Sets the path to stubs folder, set it outside the build folder.
    // default is <buildDir>/minikotlin-stubs
    stubFolder.set(project.layout.projectDirectory.dir("mk-custom-stubs"))

    // Sets the path to implementation folder.
    // default is <buildDir>/minikotlin-gen
    generateFolder.set(project.layout.buildDirectory.dir("mk-custom-gen"))
}

repositories {
    mavenCentral()
}

tasks.test {
    systemProperty("compiled_folder", project.file("compiled/temp").absolutePath)
}

val fatJar = tasks.register<ShadowJar>("thinJar") {
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

    dependsOn(fatJar, sourcesJar, tasks.named("shadowJar"))
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