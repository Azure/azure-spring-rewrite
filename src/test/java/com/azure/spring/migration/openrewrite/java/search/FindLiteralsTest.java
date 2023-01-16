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
                                File file = new File("c:\\\\temp\\\\d.text");
                              """,
                        """
                                File file = new File(/*~~(No window file url)~~>*/"c:\\\\temp\\\\d.text");
                              """
                )
        );
    }
}
