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
    void testFindMethods() {
        rewriteRun(
                spec -> spec.recipe(new FindMethods("java.io.File *(..)",true,null, "TODO ASA-FileStorageApi: need configuration to use storage")),
                java(
                        """
                              import java.io.File;

                              public class LocalFile {
                                  public void test(){
                                      File file = new File("c:\\\\temp\\\\d.text");
                                  }
                              }
                                """,
                        """
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
                              public class LocalEnv {
                                  public void test(){
                                      System.getenv("1234");
                                  }
                              }
                                """,
                        """
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
                              public class LocalProperty {
                                  public void test(){
                                      System.getProperty("1234");
                                  }
                              }
                                """,
                        """
                              public class LocalProperty {
                                  public void test(){
                                      /*~~(TODO ASA-JavaSystemConfig: need environment configuration in azure spring apps)~~>*/System.getProperty("1234");
                                  }
                              }
                            """
                )
        );

        rewriteRun(
                spec -> spec.recipe(new FindMethods("java.lang.System setProperties(..)",true,null, "TODO ASA-JavaSystemConfig: need environment configuration in azure spring apps")),
                java(
                        """
                              public class LocalProperty {
                                  public void test(java.util.Properties p){
                                      System.setProperties(p);
                                  }
                              }
                            """,
                        """
                              public class LocalProperty {
                                  public void test(java.util.Properties p){
                                      /*~~(TODO ASA-JavaSystemConfig: need environment configuration in azure spring apps)~~>*/System.setProperties(p);
                                  }
                              }
                            """
                )
        );

        rewriteRun(
                spec -> spec.recipe(new FindMethods("java.lang.System setProperty(..)",true,null, "TODO ASA-JavaSystemConfig: need environment configuration in azure spring apps")),
                java(
                        """
                                  public class LocalProperty {
                                      public void test(String key, String value){
                                          System.setProperty(key, value);
                                      }
                                  }
                                """,
                        """
                                  public class LocalProperty {
                                      public void test(String key, String value){
                                          /*~~(TODO ASA-JavaSystemConfig: need environment configuration in azure spring apps)~~>*/System.setProperty(key, value);
                                      }
                                  }
                            """
                )
        );

        rewriteRun(
                spec -> spec.recipe(new FindMethods("java.lang.System loadLibrary(..)",true,null, "TODO ASA-JavaSystemLoad: need to mount your own storage and upload your binary code")),
                java(
                        """
                              public class LocalNative {

                                  public void test(){
                                      System.loadLibrary("1234");
                                  }
                              }
                            """,
                        """
                              public class LocalNative {

                                  public void test(){
                                      /*~~(TODO ASA-JavaSystemLoad: need to mount your own storage and upload your binary code)~~>*/System.loadLibrary("1234");
                                  }
                              }
                            """
                )
        );

        rewriteRun(
                spec -> spec.recipe(new FindMethods("java.lang.System load(..)",true,null, "TODO ASA-JavaSystemLoad: need to mount your own storage and upload your binary code")),
                java(
                        """
                              import java.io.File;

                              public class LocalNative {

                                  public void load(){
                                      System.load("1234");
                                  }
                              }
                            """,
                        """
                              import java.io.File;

                              public class LocalNative {

                                  public void load(){
                                      /*~~(TODO ASA-JavaSystemLoad: need to mount your own storage and upload your binary code)~~>*/System.load("1234");
                                  }
                              }
                            """
                )
        );
    }
}
