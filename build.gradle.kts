import org.gradle.jvm.tasks.Jar
import xyz.wagyourtail.unimined.api.minecraft.task.RemapJarTask

plugins {
    java

    id("xyz.wagyourtail.unimined") version "1.3.14"

    id("me.shedaniel.unified-publishing") version "0.1.+"
}

val archive_name: String by rootProject.properties
val minecraft_version: String by rootProject.properties
val mod_version: String by rootProject.properties

group = "snownee.boattweaks"
version = "${minecraft_version}-Forge-${mod_version}"

var realVersion = "${mod_version}+forge"

base {
    archivesName = archive_name
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }

    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

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

    maven("https://maven.architectury.dev/")
}

unimined.minecraft {
    version(minecraft_version)

    mappings {
        mojmap()
        parchment(version = "2023.09.03")

        devFallbackNamespace("official")
    }

    if (sourceSet == sourceSets.main.get()) {
        minecraftForge {
            loader("47.4.1")
            mixinConfig("boattweaks.mixins.json")
        }
    }
}

val modImplementation by configurations.getting

dependencies {
//    annotationProcessor("io.github.llamalad7:mixinextras-common:0.4.1")
//    annotationProcessor("io.github.llamalad7:mixinextras-common:0.4.1")

    modImplementation("maven.modrinth:kiwi:11.8.31+forge")
    modImplementation("me.shedaniel.cloth:cloth-config-forge:11.1.136")

    modImplementation("dev.latvian.mods:kubejs-forge:2001.6.5-build.20")
}

tasks {
    named<Jar>("sourcesJar") {
        from(sourceSets.map { it.allSource })

        duplicatesStrategy = DuplicatesStrategy.WARN
    }

    withType<JavaCompile> {
        options.compilerArgs.add("-parameters")
    }

    withType<RemapJarTask> {
        mixinRemap {
            enableMixinExtra()
        }
    }
}

unifiedPublishing {
    project {
        displayName = "[Forge $project.supported_version] $project.mod_version"
        version = realVersion // Optional, Inferred from project by default
        changelog = if (file("CHANGELOG.md").exists()) file("CHANGELOG.md").readText() else "" // Optional, in markdown format
        releaseType = project.property("release_type").toString() // Optional, use "release", "beta" or "alpha"
        gameVersions = listOf("1.20.1")
        gameLoaders = listOf("neoforge", "forge")

        mainPublication(tasks.getByName("remapJar")) // Declares the publicated jar

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