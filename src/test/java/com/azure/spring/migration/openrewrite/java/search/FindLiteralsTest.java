package com.azure.spring.migration.openrewrite.java.search;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

public final class FindLiteralsTest implements RewriteTest {
    @Test
    void testFindLiteral() {
        rewriteRun(
                spec -> spec.recipe(new FindLiterals("[A-z]:([\\\\][^\\n\\t]+)+|(\\\\\\\\([^\\\\\\,\\n\\t]+)\\\\\\S+)+","No window file url")),
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
                                           File file = new File("c:\\\\temp\\\\d.text");
                                       }
                                   }
                                """
                )
        );
    }
}
