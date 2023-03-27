# azure-spring-rewrite

## how to use
azure-spring-rewrite is released as a Github Maven package. To use it, you need to perform two steps:
1) Configure authentication with a personal access token. 

   Go to your maven setting file, by default is $HOME/.m2/settings.xml, add azure-spring-rewrite repository and your github personal access token.

'''

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
            <username>git username</username>
            <password>token</password>
        </server>
    </servers>

'''

You can refer to [authenticating-with-a-personal-access-token](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-with-a-personal-access-token) for more information.


2) Run maven command

```cmd
./mvnw -U -Pgithub org.openrewrite.maven:rewrite-maven-plugin:run \
 "-Drewrite.activeRecipes=com.azure.spring.migration.UpgradeToAzureSpringApps" \
 "-Drewrite.recipeArtifactCoordinates=com.azure.spring.migration:azure-spring-rewrite:0.1.0"
```
