plugins {
    id("org.openrewrite.build.recipe-library") version "1.12.0"
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

val rewriteVersion = rewriteRecipe.rewriteVersion.get()
dependencies {
    implementation("org.openrewrite.recipe:rewrite-spring:${rewriteVersion}")
    implementation("org.openrewrite:rewrite-java:${rewriteVersion}")
    implementation("org.openrewrite:rewrite-xml:${rewriteVersion}")
    implementation("org.openrewrite:rewrite-yaml:${rewriteVersion}")
    implementation("org.openrewrite:rewrite-properties:${rewriteVersion}")
    implementation(platform("org.openrewrite.recipe:rewrite-recipe-bom:${rewriteVersion}"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:${rewriteVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${rewriteVersion}")
    testImplementation("org.assertj:assertj-core:${rewriteVersion}")
}

