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

repositories {
    mavenCentral()
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}
project.rootProject.tasks.getByName("final").dependsOn(project.tasks.getByName("publishAzure-spring-rewritePublicationToGitHubPackagesRepository"))
project.rootProject.tasks.getByName("snapshot").dependsOn(project.tasks.getByName("publishAzure-spring-rewritePublicationToGitHubPackagesRepository"))

val rewriteVersion = rewriteRecipe.rewriteVersion.get()
val rewriteSpringVersion = "4.32.0"
dependencies {
    implementation("org.openrewrite.recipe:rewrite-spring:${rewriteSpringVersion}")
    implementation("org.openrewrite:rewrite-java:${rewriteVersion}")
    testImplementation("org.openrewrite.recipe:rewrite-testing-frameworks:latest.integration")
}

