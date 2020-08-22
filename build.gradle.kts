plugins {
    kotlin("jvm") version "1.4.0"
    id("org.jetbrains.dokka") version "1.4.0-rc"
    jacoco
    `maven-publish`
}

val kllvmVersion = "0.1.6-SNAPSHOT"

project.group = "me.tomassetti"
project.version = kllvmVersion

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
    maven("https://jitpack.io")
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    maven("https://kotlin.bintray.com/kotlinx")
}

dependencies {
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation("junit:junit:4.12")
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/bms-1984/kllvm")
            credentials {
                username = System.getenv("GHUSERNAME")
                password = System.getenv("GHTOKEN")
            }
        }
    }
    publications { create<MavenPublication>("kllvm") { from(components["java"]) } }
}

jacoco {
    toolVersion = "0.8.5"
}

tasks {
    jar {
        manifest {
            attributes["Implementation-Title"] = project.name
            attributes["Implementation-Version"] = project.version
        }
        from(configurations.compile.get().map {
            if (it.isDirectory) it else zipTree(it)
        })
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "13"
        kotlinOptions.suppressWarnings = true
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "13"
        kotlinOptions.suppressWarnings = true
    }
    processResources {
        filter { it.replace("%VERSION%", project.version.toString()) }
    }
    register("version") {
        doLast {
            println("Version $version")
        }
    }
}