plugins {
    id("org.openrewrite.build.recipe-library") version "latest.release"
}

group = "com.azure.spring.migration"
description = "Recipe to migrate ASA"

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/showpune/azure-spring-rewrite")
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



// val rewriteVersion = rewriteRecipe.rewriteVersion.get()
val rewriteVersion = "7.34.3"
var springBoot3Version = "3.0.1"
dependencies {
    implementation("org.openrewrite:rewrite-java:${rewriteVersion}")
    implementation("org.openrewrite:rewrite-xml:${rewriteVersion}")
    implementation("org.openrewrite:rewrite-properties:${rewriteVersion}")
    implementation("org.openrewrite:rewrite-yaml:${rewriteVersion}")
    implementation("org.openrewrite:rewrite-maven:${rewriteVersion}")

    runtimeOnly("org.openrewrite:rewrite-java-17:$rewriteVersion")
    runtimeOnly("org.openrewrite.recipe:rewrite-testing-frameworks:1.32.0")
    runtimeOnly("org.openrewrite.recipe:rewrite-migrate-java:1.15.0")
    runtimeOnly("org.openrewrite:rewrite-java-17:$rewriteVersion")

    testImplementation("com.github.marschall:memoryfilesystem:latest.release")

    // for generating properties migration configurations
    testImplementation("io.github.classgraph:classgraph:latest.release")
    testImplementation("org.openrewrite:rewrite-java-17:${rewriteVersion}")
    testImplementation("org.openrewrite.recipe:rewrite-migrate-java:${rewriteVersion}")
    testImplementation("org.openrewrite.recipe:rewrite-testing-frameworks:${rewriteVersion}")

}


