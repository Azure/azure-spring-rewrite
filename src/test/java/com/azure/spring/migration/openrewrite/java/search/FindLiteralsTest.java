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

import org.junit.jupiter.api.Test;
import org.openrewrite.DocumentExample;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

public final class FindLiteralsTest implements RewriteTest {
    @DocumentExample
    @Test
    void testFindLiteral() {
        rewriteRun(
                spec -> spec.recipe(new FindLiterals("(?:[a-zA-Z]\\:|\\\\\\\\[\\w\\s\\.]+\\\\[\\w\\s\\.$]+)"
                    + "([\\\\\\/][^\\n\\t]+)+",
                    "TODO ASA-WindowFilePath: this file system path is Microsoft Windows platform dependent")),
                java(
                        """
                                  package org.springframework.samples.petclinic;
                                  import java.io.File;
                                    
                                  public class LocalFile {
                                  
                                      public void test(){
                                          File file = new File("c:/test.temp");
                                          File file = new File("c:\\\\temp\\\\d.text");
                                      }
                                  }
                                """,
                        """
                                  package org.springframework.samples.petclinic;
                                  import java.io.File;
                                    
                                  public class LocalFile {
                                  
                                      public void test(){
                                          File file = new File(/*~~(TODO ASA-WindowFilePath: this file system path is Microsoft Windows platform dependent)~~>*/"c:/test.temp");
                                          File file = new File(/*~~(TODO ASA-WindowFilePath: this file system path is Microsoft Windows platform dependent)~~>*/"c:\\\\temp\\\\d.text");
                                      }
                                  }
                                """
                )
        );
    }
}
