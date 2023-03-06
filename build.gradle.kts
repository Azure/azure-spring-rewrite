plugins {
    id("org.openrewrite.build.recipe-library") version "1.8.1"
}

group = "com.azure.spring.migration"
description = "Recipe to migrate ASA"

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

project.rootProject.tasks.getByName("final").dependsOn(project.tasks.getByName("publishAzure-spring-rewritePublicationToGitHubPackagesRepository"))
project.rootProject.tasks.getByName("snapshot").dependsOn(project.tasks.getByName("publishAzure-spring-rewritePublicationToGitHubPackagesRepository"))

val rewriteVersion = "7.36.0"
val rewriteSpringVersion = "4.32.0"
dependencies {
    implementation("org.openrewrite:rewrite-java:${rewriteVersion}")
    implementation("org.openrewrite.recipe:rewrite-spring:${rewriteSpringVersion}")
    testImplementation("org.openrewrite.recipe:rewrite-testing-frameworks:latest.release")
}