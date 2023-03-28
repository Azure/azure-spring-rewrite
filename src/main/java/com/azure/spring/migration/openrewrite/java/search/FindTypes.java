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
import lombok.Value;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Option;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.internal.StringUtils;
import org.openrewrite.internal.lang.NonNull;
import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.java.JavaVisitor;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.JavaType;
import org.openrewrite.java.tree.NameTree;
import org.openrewrite.java.tree.TypeUtils;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Value
public class FindTypes extends Recipe {
    @Option(displayName = "Fully-qualified type name",
        description = "A fully-qualified type name, that is used to find matching type references. " +
            "Supports glob expressions. `java..*` finds every type from every subpackage of the `java` package.",
        example = "java.util.List")
    String fullyQualifiedTypeName;

    @Option(displayName = "Check for assignability",
        description = "When enabled, find type references that are assignable to the provided type.",
        required = false)
    @Nullable
    Boolean checkAssignability;

    @Option(displayName = "mark",
            description = "Mark in matched types",
        required = false)
    String mark;

    org.openrewrite.java.search.FindTypes findTypes;

    public FindTypes(final String fullyQualifiedTypeName, @Nullable final Boolean checkAssignability, @Nullable final String mark) {
        this.fullyQualifiedTypeName = fullyQualifiedTypeName;
        this.checkAssignability = checkAssignability;
        this.mark = mark;
        this.findTypes = new org.openrewrite.java.search.FindTypes(fullyQualifiedTypeName, checkAssignability);
    }

    @Override
    public @NonNull String getDisplayName() {
        return this.findTypes.getDisplayName();
    }

    public @NonNull String getDescription() {
        return this.findTypes.getDescription();
    }

    public @NonNull TreeVisitor<?, ExecutionContext> getVisitor() {
        Pattern fullyQualifiedType = Pattern.compile(StringUtils.aspectjNameToPattern(fullyQualifiedTypeName));

        return new JavaVisitor<ExecutionContext>() {
            @Override
            public @NonNull J visitIdentifier(J.@NonNull Identifier ident, @NonNull ExecutionContext executionContext) {
                if (ident.getType() != null &&
                    getCursor().firstEnclosing(J.Import.class) == null &&
                    getCursor().firstEnclosing(J.FieldAccess.class) == null &&
                    !(getCursor().getParentOrThrow().getValue() instanceof J.ParameterizedType)) {
                    JavaType.FullyQualified type = TypeUtils.asFullyQualified(ident.getType());
                    if (typeMatches(Boolean.TRUE.equals(checkAssignability), fullyQualifiedType, type) &&
                        ident.getSimpleName().equals(type.getClassName())) {
                        return AddComment.addIfAbsent(ident, mark);
                    }
                }
                return super.visitIdentifier(ident, executionContext);
            }

            @Override
            public <N extends NameTree> @NonNull N visitTypeName(@NonNull N name, @NonNull ExecutionContext ctx) {
                N n = super.visitTypeName(name, ctx);
                JavaType.FullyQualified type = TypeUtils.asFullyQualified(n.getType());
                if (typeMatches(Boolean.TRUE.equals(checkAssignability), fullyQualifiedType, type) &&
                    getCursor().firstEnclosing(J.Import.class) == null) {
                    return AddComment.addIfAbsent(n, mark);
                }
                return n;
            }

            @Override
            public @NonNull J visitFieldAccess(J.@NonNull FieldAccess fieldAccess, @NonNull ExecutionContext ctx) {
                J.FieldAccess fa = (J.FieldAccess) super.visitFieldAccess(fieldAccess, ctx);
                JavaType.FullyQualified type = TypeUtils.asFullyQualified(fa.getTarget().getType());
                if (typeMatches(Boolean.TRUE.equals(checkAssignability), fullyQualifiedType, type) &&
                    fa.getName().getSimpleName().equals("class")) {
                    return AddComment.addIfAbsent(fa, mark);
                }
                return fa;
            }
        };
    }

    private static boolean typeMatches(boolean checkAssignability, Pattern pattern,
                                       @Nullable JavaType.FullyQualified test) {
        return test != null && (checkAssignability ?
            test.isAssignableFrom(pattern) :
            pattern.matcher(test.getFullyQualifiedName()).matches()
        );
    }
}