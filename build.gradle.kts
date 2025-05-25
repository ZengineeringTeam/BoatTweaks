import groovy.lang.Closure
import org.gradle.jvm.tasks.Jar
import xyz.wagyourtail.unimined.api.minecraft.task.AbstractRemapJarTask

plugins {
    java
    alias(catalog.plugins.git.version)

    alias(catalog.plugins.unmined)
}

val archive_name: String by rootProject.properties

group = "snownee.boattweaks"

val gitVersion: Closure<String> by extra
version = gitVersion()

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
    version(catalog.versions.minecraft.get())

    mappings {
        mojmap()
        parchment(mcVersion = catalog.versions.minecraft.get(), version = "2024.11.17")

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
