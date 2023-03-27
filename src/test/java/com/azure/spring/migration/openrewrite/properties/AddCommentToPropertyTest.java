/*
 * Copyright 2023 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.azure.spring.migration.openrewrite.properties;
import org.junit.jupiter.api.Test;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.properties.Assertions.properties;

public class AddCommentToPropertyTest implements RewriteTest {
    @Test
    void testFindPassword() {
        rewriteRun(
            spec -> spec.recipe(new AddCommentToProperty("*password","TODO ASA-FindPassword: Don't save passwords or login information in files",true)),
            properties(
                """
                    application.Password = 1111
                    application.name = name
                      """,
                """
                    #TODO ASA-FindPassword: Don't save passwords or login information in files
                    application.Password = 1111
                    application.name = name
                    """
            )
        );
    }

    @Test
    void testFindEurekaConfig() {
        rewriteRun(
            spec -> spec.recipe(new AddCommentToProperty("*eureka.client.serviceUrl","TODO ASA-EurekaConfigServer: ASA will inject the eureka/config server connection info upon app start",true)),
            properties(
                """
                    application.name = name
                    eureka.client.service-url = test
                      """,
                """
                    application.name = name
                    #TODO ASA-EurekaConfigServer: ASA will inject the eureka/config server connection info upon app start
                    eureka.client.service-url = test
                    """
            )
        );
        rewriteRun(
            spec -> spec.recipe(new AddCommentToProperty("*spring.config.import","TODO ASA-EurekaConfigServer: ASA will inject the eureka/config server connection info upon app start",true)),
            properties(
                """
                    spring.config.import = test
                    application.name = name
                      """,
                """
                    #TODO ASA-EurekaConfigServer: ASA will inject the eureka/config server connection info upon app start
                    spring.config.import = test
                    application.name = name
                    """
            )
        );
        rewriteRun(
            spec -> spec.recipe(new AddCommentToProperty("*spring.cloud.config.uri","TODO ASA-EurekaConfigServer: ASA will inject the eureka/config server connection info upon app start",true)),
            properties(
                """
                    application = test
                    spring.cloud.config.uri = test
                    application.name = name
                      """,
                """
                    application = test
                    #TODO ASA-EurekaConfigServer: ASA will inject the eureka/config server connection info upon app start
                    spring.cloud.config.uri = test
                    application.name = name
                    """
            )
        );
    }

}
