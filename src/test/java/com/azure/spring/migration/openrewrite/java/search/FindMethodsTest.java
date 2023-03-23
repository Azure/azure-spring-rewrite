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

package com.azure.spring.migration.openrewrite.java.search;

import static org.openrewrite.java.Assertions.java;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RewriteTest;

public final class FindMethodsTest implements RewriteTest {
    @Test
    void testFindTypes() {
        rewriteRun(
                spec -> spec.recipe(new FindMethods("java.io.File *(..)",true,null, "TODO ASA-FileStorageApi: need configuration to use storage")),
                java(
                        """
                                  package org.springframework.samples.petclinic;
                                  import java.io.File;
                                  
                                  public class LocalFile {
                                      public void test(){
                                          File file = new File("c:\\\\temp\\\\d.text");
                                      }
                                  }
                                """,
                        """
                              package org.springframework.samples.petclinic;
                              import java.io.File;
                              
                              public class LocalFile {
                                  public void test(){
                                      File file = /*~~(TODO ASA-FileStorageApi: need configuration to use storage)~~>*/new File("c:\\\\temp\\\\d.text");
                                  }
                              }
                            """
                )
        );

        rewriteRun(
                spec -> spec.recipe(new FindMethods("java.lang.System getenv(..)",true,null, "TODO ASA-JavaSystemConfig: need environment configuration in azure spring apps")),
                java(
                        """
                                  package org.springframework.samples.petclinic;

                                  public class LocalEnv {
                                      public void test(){
                                          System.getenv("1234");
                                      }
                                  }
                                """,
                        """
                                  package org.springframework.samples.petclinic;

                                  public class LocalEnv {
                                      public void test(){
                                          /*~~(TODO ASA-JavaSystemConfig: need environment configuration in azure spring apps)~~>*/System.getenv("1234");
                                      }
                                  }
                            """
                )
        );

        rewriteRun(
                spec -> spec.recipe(new FindMethods("java.lang.System getProperty(..)",true,null, "TODO ASA-JavaSystemConfig: need environment configuration in azure spring apps")),
                java(
                        """
                                  package org.springframework.samples.petclinic;

                                  public class LocalProperty {
                                      public void test(){
                                          System.getProperty("1234");
                                      }
                                  }
                                """,
                        """
                                  package org.springframework.samples.petclinic;

                                  public class LocalProperty {
                                      public void test(){
                                          /*~~(TODO ASA-JavaSystemConfig: need environment configuration in azure spring apps)~~>*/System.getProperty("1234");
                                      }
                                  }
                            """
                )
        );

        rewriteRun(
                spec -> spec.recipe(new FindMethods("java.lang.System setProperty(..)",true,null, "TODO ASA-JavaSystemConfig: need environment configuration in azure spring apps")),
                java(
                        """
                                  package org.springframework.samples.petclinic;

                                  public class LocalProperty {
                                      public void test(Property p){
                                          System.setProperties(p);
                                      }
                                  }
                                """,
                        """
                                  package org.springframework.samples.petclinic;

                                  public class LocalProperty {
                                      public void test(Property p){
                                          /*~~(TODO ASA-JavaSystemConfig: need environment configuration in azure spring apps)~~>*/System.setProperties("1234");
                                      }
                                  }
                            """
                )
        );

        rewriteRun(
                spec -> spec.recipe(new FindMethods("java.lang.System loadLibrary(..)",true,null, "TODO ASA-JavaSystemLoad: need to mount your own storage and upload your binary code")),
                java(
                        """
                                  package org.springframework.samples.petclinic;

                                  public class LocalNative {

                                      public void test(){
                                          System.loadLibrary("1234");
                                      }
                                  }
                            """,
                        """
                                  package org.springframework.samples.petclinic;

                                  public class LocalNative {

                                      public void test(){
                                          /*~~(TODO ASA-JavaSystemLoad: need to mount your own storage and upload your binary code)~~>*/System.loadLibrary("1234");
                                      }
                                  }
                            """
                )
        );

        rewriteRun(
                spec -> spec.recipe(new FindMethods("java.lang.System loadLibrary(..)",true,null, "TODO ASA-JavaSystemLoad: need to mount your own storage and upload your binary code")),
                java(
                        """
                                  package org.springframework.samples.petclinic;
                                  import java.io.File;

                                  public class LocalNative {

                                      public void load(){
                                          System.loadLibrary("1234");
                                      }
                                  }
                            """,
                        """
                                  package org.springframework.samples.petclinic;
                                  import java.io.File;

                                  public class LocalNative {

                                      public void load(){
                                          /*~~(TODO ASA-JavaSystemLoad: need to mount your own storage and upload your binary code)~~>*/System.loadLibrary("1234");
                                      }
                                  }
                            """
                )
        );
    }
}
