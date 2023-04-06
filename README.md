# Azure Spring Rewrite

## Recipes

There are two sets of recipes available: one for upgrading Spring Boot/Spring Cloud and another for migrating to Azure Spring Apps.

### Upgrade Spring Boot/Spring Cloud

The recipe `com.azure.spring.migration.UpgradeSpringboot_2_7_SpringCloud_2021` upgrades your Spring Boot app to Spring Boot 2.7 and Spring Cloud 2021.

For a list of changes this recipe makes for the Spring Boot 2.7 upgrade, see [UpgradeSpringBoot_2_7](https://docs.openrewrite.org/reference/recipes/java/spring/boot2/upgradespringboot_2_7#definition).
For the Spring Cloud 2021 upgrade, this recipe modifies the Spring Cloud version in the application's Maven POM file.

### Migrate to Azure Spring Apps

The recipe `com.azure.spring.migration.UpgradeToAzureSpringApps` checks your code and adds TODO comments in your application for migration to Azure Spring Apps.

The currently available list of check items includes:
- Windows file path
- File storage usage
- Logging to console
- Eureka and Config Server connection info
- Password and user ID in configuration files
- Java system load
- Java system config

## How to Use

Azure-spring-rewrite is released as a GitHub Maven package. To use it, you need to perform two steps:
1) Configure authentication with a personal access token.

   Go to your Maven settings file (by default, `$HOME/.m2/settings.xml`) and add the azure-spring-rewrite repository and your GitHub personal access token.
   You can refer to [authenticating-with-a-personal-access-token](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-with-a-personal-access-token) for more information.
   
   To create a personal access token, refer to [Creating a personal access token (classic)](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token#creating-a-personal-access-token-classic). Make sure you select access to `read:packages` when creating the token.
   ```
       <profiles>
           <profile>
               <id>github</id>
               <repositories>
                   <repository>
                       <id>central</id>
                       <url>https://repo1.maven.org/maven2</url>
                   </repository>
                  <!-- add azure-spring-rewrite repository here -->
                   <repository>
                       <id>github</id>
                       <url>https://maven.pkg.github.com/azure/azure-spring-rewrite</url>
                       <snapshots>
                           <enabled>true</enabled>
                       </snapshots>
                   </repository>
               </repositories>
           </profile>
       </profiles>
       <servers>
            <!-- add github personal access token -->
           <server>
               <id>github</id>
               <username>username</username>
               <password>github personal access token</password>
           </server>
       </servers>
   ```


2) Run rewrite plugin

   For maven projects, you can run maven command line under your application path:
   ```cmd
   ./mvnw -U -Pgithub org.openrewrite.maven:rewrite-maven-plugin:run "-Drewrite.activeRecipes=com.azure.spring.migration.UpgradeToAzureSpringApps, com.azure.spring.migration.UpgradeSpringboot_2_7_SpringCloud_2021" "-Drewrite.recipeArtifactCoordinates=com.azure.spring.migration:azure-spring-rewrite:LATEST"
   ```
   
   For gradle projects, you need to configure rewrite-gradle-plugin:
   ```gradle
   plugins {
      id("org.openrewrite.rewrite") version("5.38.0")
   }
   
   rewrite {
      activeRecipe("com.azure.spring.migration.UpgradeToAzureSpringApps", "com.azure.spring.migration.UpgradeSpringboot_2_7_SpringCloud_2021")
   }
   
   repositories {
   mavenCentral()
   maven {
      name = "GitHubPackages"
      url = uri("https://maven.pkg.github.com/azure/azure-spring-rewrite")
      credentials {
      username = "<username>"
      password = "<github personal access token>"
         }
      }
   }
   
   dependencies {
      rewrite(platform("org.openrewrite.recipe:rewrite-recipe-bom:1.17.0"))
      rewrite("com.azure.spring.migration:azure-spring-rewrite:0.1.1")
   }
   ```

   `-Drewrite.activeRecipes` in the Maven command line and `activeRecipe` in the Gradle file are the recipe lists you want to apply to your application.
   You can configure it as you like by adding one or all of the defined recipes `com.azure.spring.migration.UpgradeToAzureSpringApps``com.azure.spring.migration.UpgradeSpringboot_2_7_SpringCloud_2021`, or adding official openrewrite recipes, e.g., `org.openrewrite.java.migrate.UpgradeToJava17`([reference page](https://docs.openrewrite.org/reference/recipes/java/migrate/upgradetojava17)).