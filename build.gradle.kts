import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

import java.nio.file.Files
import java.nio.file.StandardCopyOption

plugins {
  java
  `java-library`

  `maven-publish`

  id("io.papermc.paperweight.userdev") version "1.7.3"
  id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.2.0"
  id("xyz.jpenilla.run-paper") version "2.3.1"
  id("io.github.goooler.shadow") version "8.1.8"
  kotlin("jvm")
}

description = "A Minecraft Paper plugin that adds some SCPs to the game."

group = "dev.enderman"
version = "0.0.6"

val projectGroupString = group.toString()
val projectVersionString = version.toString()

val javaVersion = 21
val javaVersionEnumMember = JavaVersion.valueOf("VERSION_$javaVersion")

val paperApiMinecraftVersion = "1.21.1"
val paperApiVersion = "$paperApiMinecraftVersion-R0.1-SNAPSHOT"

java {
  sourceCompatibility = javaVersionEnumMember
  targetCompatibility = javaVersionEnumMember

  toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion))
}

repositories {
  mavenCentral()
  maven("https://www.jitpack.io")

  maven("https://repo.flyte.gg/releases")
}

dependencies {
  paperweight.paperDevBundle(paperApiVersion)

  implementation("dev.jorel", "commandapi-bukkit-shade-mojang-mapped", "9.5.3")
  implementation("net.lingala.zip4j", "zip4j", "2.11.5")
  implementation("com.github.esotericfoundation:utility.kt:1.1.1")
  implementation("com.github.esotericfoundation:plugin-library:0.8.3")
  implementation("gg.flyte:twilight:1.1.17")
  implementation(kotlin("stdlib-jdk8"))
}

tasks {
  build {
    dependsOn(shadowJar)
  }

  compileJava {
    options.release.set(javaVersion)
  }

  javadoc {
    options.encoding = Charsets.UTF_8.name()
  }
}

bukkitPluginYaml {
  name = "SCP"
  description = project.description

  authors = listOfNotNull("Esoteric Enderman")

  setVersion(project.version)

  apiVersion = paperApiMinecraftVersion
  main = "${project.group}.minecraft.plugins.scp.SCPPlugin"

  load = BukkitPluginYaml.PluginLoadOrder.STARTUP
}

publishing {
  publications {
    create<MavenPublication>("mavenJava") {
      from(components["java"])
      groupId = projectGroupString
      artifactId = rootProject.name
      version = projectVersionString
    }
  }
}

tasks.named("publishMavenJavaPublicationToMavenLocal") {
  dependsOn(tasks.named("build"))
}

val serverPropertiesFile = layout.projectDirectory.file("run/server.properties")
val backupPropertiesFile = layout.buildDirectory.file("server.properties.backup")

tasks.register("backupServerProperties") {
  doLast {
    val source = serverPropertiesFile.asFile
    val destination = backupPropertiesFile.get().asFile
    if (source.exists()) {
      Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING)
      println("Backed up server.properties to build directory.")
    }
  }
}

tasks.register("restoreServerProperties") {
  doLast {
    val source = backupPropertiesFile.get().asFile
    val destination = serverPropertiesFile.asFile
    if (source.exists()) {
      Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING)
    }
  }
}

tasks.named("runServer") {
  dependsOn("backupServerProperties")
  finalizedBy("restoreServerProperties")
}
