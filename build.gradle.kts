import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    mavenCentral()
    maven("https://packages.confluent.io/maven/")
}

dependencies {
    // Apache Beam
    val apacheBeamVersion: String by project
    implementation("org.apache.beam:beam-sdks-java-core:$apacheBeamVersion")
    implementation("org.apache.beam:beam-runners-direct-java:$apacheBeamVersion")
    implementation("org.apache.beam:beam-runners-google-cloud-dataflow-java:$apacheBeamVersion")
    implementation("org.apache.beam:beam-runners-portability-java:$apacheBeamVersion")
    implementation("org.apache.beam:beam-sdks-java-extensions-python:$apacheBeamVersion")

    // Logging
    val slf4jVersion: String by project
    implementation("org.slf4j:slf4j-log4j12:$slf4jVersion")
}

application {
    mainClass.set("pl.allegro.tech.jugtoberfest2022.MainKt")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.shadowJar {
    manifest {
        attributes("Main-Class" to application.mainClass.get())
    }
    archiveFileName.set("${project.name}-all.jar")
    mergeServiceFiles() // Merge instead of overwrite, see: https://stackoverflow.com/questions/57039951
    isZip64 = true
}

val buildDockerImages = tasks.register("buildDockerImages", Exec::class.java) { // Uses local Docker
    commandLine = listOf("docker-compose", "build")
    dependsOn(tasks.withType<ShadowJar>())
}

val pushWorkerImage = tasks.register("pushWorkerImage", Exec::class.java) {
    commandLine = listOf("docker-compose", "push", "python-worker")
    dependsOn(buildDockerImages)
}

tasks.named("build").configure { dependsOn(buildDockerImages) }

tasks.register("runPipeline", Exec::class.java) {
    commandLine = listOf("docker-compose", "up", "java-pipeline")
    dependsOn(buildDockerImages, pushWorkerImage)
}

tasks.wrapper {
    gradleVersion = "7.5.1"
}
