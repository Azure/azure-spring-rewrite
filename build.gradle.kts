plugins {
    id("java")

    id("org.openrewrite.rewrite") version "latest.release"

    id("nebula.maven-resolved-dependencies") version "17.3.2"
    id("nebula.release") version "15.3.1"
    id("io.github.gradle-nexus.publish-plugin") version "1.0.0"
}

group = "com.azure.spring.migration"
description = "Recipe to migrate ASA"

repositories {
    mavenCentral()
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/azure/azure-spring-rewrite")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("azure-spring-rewrite") {
            from(components["java"])
        }
    }
}

configure<nebula.plugin.release.git.base.ReleasePluginExtension> {
    defaultVersionStrategy = nebula.plugin.release.NetflixOssStrategies.SNAPSHOT(project)
}

project.rootProject.tasks.getByName("final").dependsOn(project.tasks.getByName("publishAzure-spring-rewritePublicationToGitHubPackagesRepository"))
project.rootProject.tasks.getByName("snapshot").dependsOn(project.tasks.getByName("publishAzure-spring-rewritePublicationToGitHubPackagesRepository"))

nexusPublishing {
    repositories {
        sonatype()
    }
}

val rewriteSpringVersion = "4.32.0"
dependencies {
    implementation("org.openrewrite.recipe:rewrite-spring:${rewriteSpringVersion}")
    implementation("org.openrewrite:rewrite-java:latest.release")
    testImplementation("org.openrewrite:rewrite-test:latest.release")
}