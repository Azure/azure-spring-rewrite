plugins {
    id("org.openrewrite.build.recipe-library") version "latest.release"
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

// val rewriteVersion = rewriteRecipe.rewriteVersion.get()
val rewriteVersion = "7.36.0"
dependencies {
    rewrite("org.openrewrite.recipe:rewrite-spring:4.32.0")
    implementation("org.openrewrite:rewrite-gradle:${rewriteVersion}")

    implementation("org.openrewrite:rewrite-java:${rewriteVersion}")
    implementation("org.openrewrite:rewrite-xml:${rewriteVersion}")
    implementation("org.openrewrite:rewrite-properties:${rewriteVersion}")
    implementation("org.openrewrite:rewrite-yaml:${rewriteVersion}")
    implementation("org.openrewrite:rewrite-maven:${rewriteVersion}")

    runtimeOnly("org.openrewrite:rewrite-java-17:$rewriteVersion")
    runtimeOnly("org.openrewrite.recipe:rewrite-testing-frameworks:1.32.0")
    runtimeOnly("org.openrewrite.recipe:rewrite-migrate-java:1.15.0")
    runtimeOnly("org.openrewrite:rewrite-java-17:$rewriteVersion")

    testImplementation("org.openrewrite:rewrite-java-17:${rewriteVersion}")
    testImplementation("org.openrewrite.recipe:rewrite-migrate-java:1.15.0")
    testImplementation("org.openrewrite.recipe:rewrite-testing-frameworks:1.32.0")
}

nebulaPublishVerification {
    ignore("org.openrewrite:rewrite-gradle")
    ignore("org.openrewrite:rewrite-java")
    ignore("org.openrewrite:rewrite-xml")
    ignore("org.openrewrite:rewrite-properties")
    ignore("org.openrewrite:rewrite-maven")
}
