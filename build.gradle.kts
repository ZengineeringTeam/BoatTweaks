import org.gradle.jvm.tasks.Jar
import xyz.wagyourtail.unimined.api.minecraft.task.AbstractRemapJarTask

plugins {
    java

    id("xyz.wagyourtail.unimined") version "1.3.14"

    id("me.shedaniel.unified-publishing") version "0.1.+"
}

val archive_name: String by rootProject.properties
val minecraft_version: String by rootProject.properties
val mod_version: String by rootProject.properties

group = "snownee.boattweaks"
version = "${minecraft_version}-NeoForge-${mod_version}"

var realVersion = "${mod_version}+neoforge"

base {
    archivesName = archive_name
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

    withSourcesJar()
}

repositories {
    unimined.curseMaven()
    unimined.modrinthMaven()

    maven("https://maven.su5ed.dev/releases") {
        content {
            includeGroupAndSubgroups("dev.su5ed")
            includeGroupAndSubgroups("org.sinytra")
        }
    }

    maven("https://jitpack.io") {
        content {
            includeGroupAndSubgroups("com.github")
        }
    }

    maven("https://maven.shedaniel.me/") {
        content {
            includeGroup("me.shedaniel.cloth")
        }
    }

    maven("https://maven.latvian.dev/releases") {
        content {
            includeGroup("dev.latvian.mods")
            includeGroup("dev.latvian.apps")
        }
    }
}

unimined.minecraft {
    version(minecraft_version)

    mappings {
        mojmap()
        parchment(version = "2024.11.17")

        devFallbackNamespace("mojmap")
    }

    if (sourceSet == sourceSets.main.get()) {
        neoForge {
            loader("172")
        }
    }
}

val include by configurations.getting

dependencies {
    implementation("maven.modrinth:kiwi:15.5.2+neoforge")
    implementation("me.shedaniel.cloth:cloth-config-neoforge:15.0.140") {
        exclude(group = "net.fabricmc.fabric-api")
    }

    implementation("dev.latvian.mods:kubejs-neoforge:2101.7.2-build.233")
}

tasks {
    named<Jar>("sourcesJar") {
        from(sourceSets.map { it.allSource })

        duplicatesStrategy = DuplicatesStrategy.WARN
    }

    withType<JavaCompile> {
        options.compilerArgs.add("-parameters")
    }

    named<AbstractRemapJarTask>("remapJar") {
    }
}

unifiedPublishing {
    project {
        displayName = "[Forge $project.supported_version] $project.mod_version"
        version = realVersion // Optional, Inferred from project by default
        changelog = if (file("CHANGELOG.md").exists()) file("CHANGELOG.md").readText() else "" // Optional, in markdown format
        releaseType = project.property("release_type").toString() // Optional, use "release", "beta" or "alpha"
        gameVersions = listOf("1.21.1")
        gameLoaders = listOf("neoforge")

        mainPublication(tasks.jar.get()) // Declares the publicated jar

        if (System.getenv("CURSE_TOKEN") != null) {
            curseforge {
                token = System.getenv("CURSE_TOKEN")
                id = "866472" // Required, must be a string, ID of CurseForge project

                relations { // Optional, Inferred from the relations above by default
                    depends("kiwi")
                    optional("cloth-config")
                }
            }
        }

        if (System.getenv("MODRINTH_TOKEN") != null) {
            modrinth {
                token = System.getenv("MODRINTH_TOKEN")
                id = "boat-tweaks" // Required, must be a string, ID of Modrinth project

                relations { // Optional, Inferred from the relations above by default
                    depends("kiwi")
                    optional("cloth-config")
                }
            }
        }
    }
}