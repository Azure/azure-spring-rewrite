/*
 * Copyright 2020 the original author or authors.
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
package com.azure.spring.migration.openrewrite.xml;

import org.openrewrite.Cursor;
import org.openrewrite.xml.tree.Xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Supports a limited set of XPath expressions, specifically those
 * documented on <a href="https://www.w3schools.com/xml/xpath_syntax.asp">this page</a>.
 * <p>
 * Used for checking whether a visitor's cursor meets a certain XPath expression.
 * <p>
 * The "current node" for XPath evaluation is always the root node of the document.
 * As a result, '.' and '..' are not recognized.
 */
public class CaseInsensitiveXPathMatcher {
    private final String expression;

    public CaseInsensitiveXPathMatcher(String expression) {
        this.expression = expression;
    }

    public boolean matches(Cursor cursor) {
        List<Xml.Tag> path = cursor.getPathAsStream()
            .filter(p -> p instanceof Xml.Tag)
            .map(Xml.Tag.class::cast)
            .collect(Collectors.toList());

        if (expression.startsWith("//") || !expression.startsWith("/")) {
            List<String> parts = new ArrayList<>(Arrays.asList((expression.startsWith("//") ?
                expression.substring(2) : expression).split("/")));
            Collections.reverse(parts);

            int pathIndex = 0;
            for (int i = 0; i < parts.size(); i++, pathIndex++) {
                String part = parts.get(i);
                if (part.startsWith("@")) {
                    if (!(cursor.getValue() instanceof Xml.Attribute &&
                        (((Xml.Attribute) cursor.getValue()).getKeyAsString().equalsIgnoreCase(part.substring(1))) ||
                        "*".equals(part.substring(1)))) {
                        return false;
                    }

                    pathIndex--;
                    continue;
                }

                if (path.size() < i + 1 || (!path.get(pathIndex).getName().equalsIgnoreCase(part) && !"*".equalsIgnoreCase(part))) {
                    return false;
                }
            }

            return expression.startsWith("/") || path.size() - pathIndex <= 1;
        } else if (expression.startsWith("/")) {
            Collections.reverse(path);

            String[] parts = expression.substring(1).split("/");
            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                if (part.startsWith("@")) {
                    return cursor.getValue() instanceof Xml.Attribute &&
                        (((Xml.Attribute) cursor.getValue()).getKeyAsString().equalsIgnoreCase(part.substring(1)) ||
                            "*".equals(part.substring(1)));
                }

                if (path.size() < i + 1 || (!path.get(i).getName().equalsIgnoreCase(part) && !"*".equals(part))) {
                    return false;
                }
            }

            return cursor.getValue() instanceof Xml.Tag && path.size() == parts.length;
        }

        return false;
    }
}
