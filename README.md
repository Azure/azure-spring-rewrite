# azure-spring-rewrite

## how to use
1) go to $HOME/.m2/settings.xml, add

'''

    <profiles>
        <profile>
            <id>github</id>
            <repositories>
                <repository>
                    <id>central</id>
                    <url>https://repo1.maven.org/maven2</url>
                </repository>
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
        <server>
            <id>github</id>
            <username>git username</username>
            <password>token</password>
        </server>
    </servers>

'''

2) Download project
   https://github.com/showpune/spring-petclinic-migration


3) Run
   ./mvnw -Pgithub org.openrewrite.maven:rewrite-maven-plugin:4.39.0:run "-Drewrite.activeRecipes=com.azure.spring.migration.UpgradeToAzureSpringApps" "-Drewrite.recipeArtifactCoordinates=com.azure.spring.migration:azure-spring-rewrite:0.1.0,org.openrewrite.recipe:rewrite-spring:4.32.0"
  
 
    
