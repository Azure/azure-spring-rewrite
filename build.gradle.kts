plugins {
    id("org.openrewrite.build.recipe-library") version "latest.release"
}

group = "com.azure.spring.migration"
description = "Recipe to migrate ASA"
version = "0.1.0"

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/azure/azure-spring-rewrite")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("azure-spring-rewrite") {
            from(components["java"])
        }
    }
}



val rewriteVersion = rewriteRecipe.rewriteVersion.get()
dependencies {
    implementation("org.openrewrite:rewrite-java:${rewriteVersion}")
    testImplementation("org.openrewrite.recipe:rewrite-testing-frameworks:1.34.0")
}


