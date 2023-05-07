/*
 * This file was generated by the Gradle 'init' task.
 *
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    id("multiproject.kotlin-application-conventions")
    kotlin("plugin.serialization") version "1.8.10"
}

dependencies {
    implementation("org.apache.commons:commons-text")
    implementation(project(":lib"))
//    implementation(":lib:jar")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("io.insert-koin:koin-core:3.3.3")
    implementation("de.brudaswen.kotlinx.serialization:kotlinx-serialization-csv:2.0.0")
    implementation(project(mapOf("path" to ":client")))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0-RC")
}

application {
    // Define the main class for the application.
    mainClass.set("multiproject.resolver.AppKt")
}

tasks {
    val mkjar = register<Jar>("mkjar") {
        dependsOn.addAll(listOf("compileJava", "compileKotlin", "processResources", ":lib:jar"))
        archiveClassifier.set("standalone")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest { attributes(mapOf("Main-Class" to application.mainClass)) }
        val sourcesMain = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get()
            .map { if (it.isDirectory) it else zipTree(it) } +
                sourcesMain.output
        from(contents)
    }
    build {
        dependsOn(mkjar)
    }
}