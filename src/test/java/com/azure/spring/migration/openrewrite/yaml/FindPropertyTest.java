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

package com.azure.spring.migration.openrewrite.yaml;

import static org.openrewrite.yaml.Assertions.yaml;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RewriteTest;

public final class FindPropertyTest implements RewriteTest {
    @Test
    void testFindPassword() {
        rewriteRun(
            spec -> spec.recipe(new FindProperty("*password","TODO ASA-FindPassword: Don't save passwords or login information in files",true)),
            yaml(
                """
                        application:
                          Password: 1111
                        application:
                          password:
                            test: 111
                      """,
                """
                    application:
                      #TODO ASA-FindPassword: Don't save passwords or login information in files
                      Password: 1111
                    application:
                      #TODO ASA-FindPassword: Don't save passwords or login information in files
                      password:
                        test: 111
                      """
            )
        );
    }

    @Test
    void testFindEurekaConfig() {
        rewriteRun(
            spec -> spec.recipe(new FindProperty("*eureka.client.serviceUrl","TODO ASA-EurekaConfigServer: ASA will inject the eureka/config server connection info upon app start",true)),
            yaml(
                """
                        eureka:
                          instance:
                            instance-id:
                          client:
                            service-url: TEST
                      """,
                """
                    eureka:
                      instance:
                        instance-id:
                      client:
                        #TODO ASA-EurekaConfigServer: ASA will inject the eureka/config server connection info upon app start
                        service-url: TEST
                     """
            )
        );
        rewriteRun(
            spec -> spec.recipe(new FindProperty("*spring.config.import","TODO ASA-EurekaConfigServer: ASA will inject the eureka/config server connection info upon app start",true)),
            yaml(
                """
                    spring:
                      cloud:
                      config:
                        import:
                      """,
                """
                    spring:
                      cloud:
                      config:
                        #TODO ASA-EurekaConfigServer: ASA will inject the eureka/config server connection info upon app start
                        import:
                     """
            )
        );
        rewriteRun(
            spec -> spec.recipe(new FindProperty("*spring.cloud.config.uri","TODO ASA-EurekaConfigServer: ASA will inject the eureka/config server connection info upon app start",true)),
            yaml(
                """
                    spring:
                      cloud:
                        config:
                          uri:
                      """,
                """
                    spring:
                      cloud:
                        config:
                          #TODO ASA-EurekaConfigServer: ASA will inject the eureka/config server connection info upon app start
                          uri:
                     """
            )
        );

    }
}

