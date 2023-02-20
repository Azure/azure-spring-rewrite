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

import java.util.regex.Pattern;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Option;
import org.openrewrite.Recipe;
import org.openrewrite.internal.lang.NonNull;
import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaVisitor;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.JavaType.Primitive;
import org.openrewrite.marker.SearchResult;

@EqualsAndHashCode(callSuper = true)
@Value
public class FindLiterals extends Recipe {
    @Option(
            displayName = "Pattern",
            description = "A regular expression pattern to match literals against."
    )
    String pattern;

    @Option(displayName = "mark",
            description = "Mark in matched literals",
        required = false)
    String mark;

    org.openrewrite.java.search.FindLiterals findLiterals;

    public FindLiterals(final String pattern, @Nullable final String mark) {
        this.pattern = pattern;
        this.mark = mark;
        this.findLiterals = new org.openrewrite.java.search.FindLiterals(pattern);
    }


    public @NonNull String getDisplayName() {
        return this.findLiterals.getDisplayName();
    }

    @Override
    public @NonNull String getDescription() {
        return this.findLiterals.getDescription();
    }

    public @NonNull JavaVisitor<ExecutionContext> getVisitor() {
        final Pattern compiledPattern = Pattern.compile(this.pattern);
        return new JavaIsoVisitor<ExecutionContext>() {
            public J. @NonNull Literal visitLiteral(J. @NonNull Literal literal, @NonNull ExecutionContext ctx) {
                if (literal.getValueSource() != null) {
                    if (literal.getType() == Primitive.String && compiledPattern.matcher(literal.getValueSource().substring(1, literal.getValueSource().length() - 1)).matches()) {
                        return SearchResult.found(literal,mark);
                    }
                    if (compiledPattern.matcher(literal.getValueSource()).matches()) {
                        return SearchResult.found(literal,mark);
                    }
                }
                return literal;
            }
        };
    }
}
