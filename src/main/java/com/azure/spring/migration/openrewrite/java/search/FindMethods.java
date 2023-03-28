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

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.*;
import org.openrewrite.internal.StringUtils;
import org.openrewrite.internal.lang.NonNull;
import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.dataflow.FindLocalFlowPaths;
import org.openrewrite.java.dataflow.LocalFlowSpec;
import org.openrewrite.java.dataflow.LocalTaintFlowSpec;
import org.openrewrite.java.table.MethodCalls;
import org.openrewrite.java.tree.Expression;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.JavaSourceFile;

/**
 * Finds matching method invocations.
 */
@EqualsAndHashCode(callSuper = true)
@Value
public class FindMethods extends Recipe  {
    transient MethodCalls methodCalls = new MethodCalls(this);

    /**
     * A method pattern that is used to find matching method invocations.
     * See {@link MethodMatcher} for details on the expression's syntax.
     */
    @Option(displayName = "Method pattern",
        description = "A method pattern that is used to find matching method invocations.",
        example = "java.util.List add(..)")
    String methodPattern;

    @Option(displayName = "Match on overrides",
        description = "When enabled, find methods that are overrides of the method pattern.",
        required = false)
    @Nullable
    Boolean matchOverrides;

    @Option(displayName = "Show flow",
        description = "When enabled, show the data or taint flow of the method invocation.",
        valid = {"none", "data", "taint"},
        required = false
    )
    @Nullable
    String flow;

    @Option(displayName = "mark",
        description = "Mark in matched types",
        required = false)
    String mark;

    org.openrewrite.java.search.FindMethods findMethods;

    public FindMethods(final String methodPattern, @Nullable final Boolean matchOverrides, @Nullable final String flow, @Nullable final String mark) {
        this.methodPattern = methodPattern;
        this.matchOverrides = matchOverrides;
        this.flow = flow;
        this.mark = mark;
        this.findMethods = new org.openrewrite.java.search.FindMethods(methodPattern, matchOverrides, flow);
    }

    @Override
    public @NonNull String getDisplayName() {
        return this.findMethods.getDisplayName();
    }

    @Override
    public @NonNull String getDescription() {
        return this.findMethods.getDescription();
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public @NonNull TreeVisitor<?, ExecutionContext> getVisitor() {
        MethodMatcher methodMatcher = new MethodMatcher(methodPattern, matchOverrides);
        boolean flowEnabled = !StringUtils.isBlank(flow) && !"none".equals(flow);
        return new JavaIsoVisitor<ExecutionContext>() {
            @Override
            public @NonNull J.MethodInvocation visitMethodInvocation(@NonNull J.MethodInvocation method, @NonNull ExecutionContext ctx) {
                J.MethodInvocation m = super.visitMethodInvocation(method, ctx);
                if (methodMatcher.matches(method)) {
                    if (!flowEnabled) {
                        JavaSourceFile javaSourceFile = getCursor().firstEnclosing(JavaSourceFile.class);
                        if(javaSourceFile != null) {
                            methodCalls.insertRow(ctx, new MethodCalls.Row(
                                javaSourceFile.getSourcePath().toString(),
                                method.printTrimmed(getCursor())
                            ));
                        }
                        m = AddComment.addIfAbsent(m, mark);
                    } else {
                        doAfterVisit(new FindLocalFlowPaths<>(getFlowSpec(method)));
                    }
                }
                return m;
            }

            @Override
            public @NonNull J.MemberReference visitMemberReference(@NonNull J.MemberReference memberRef, @NonNull ExecutionContext ctx) {
                J.MemberReference m = super.visitMemberReference(memberRef, ctx);
                if (methodMatcher.matches(m.getMethodType())) {
                    if (!flowEnabled) {
                        JavaSourceFile javaSourceFile = getCursor().firstEnclosing(JavaSourceFile.class);
                        if(javaSourceFile != null) {
                            methodCalls.insertRow(ctx, new MethodCalls.Row(
                                javaSourceFile.getSourcePath().toString(),
                                memberRef.printTrimmed(getCursor())
                            ));
                        }
                        m = m.withReference(AddComment.addIfAbsent(m.getReference(), mark));
                    } else {
                        doAfterVisit(new FindLocalFlowPaths<>(getFlowSpec(memberRef)));
                    }
                }
                return m;
            }

            @Override
            public @NonNull J.NewClass visitNewClass(@NonNull J.NewClass newClass, @NonNull ExecutionContext ctx) {
                J.NewClass n = super.visitNewClass(newClass, ctx);
                if (methodMatcher.matches(newClass)) {
                    if (!flowEnabled) {
                        JavaSourceFile javaSourceFile = getCursor().firstEnclosing(JavaSourceFile.class);
                        if(javaSourceFile != null) {
                            methodCalls.insertRow(ctx, new MethodCalls.Row(
                                javaSourceFile.getSourcePath().toString(),
                                newClass.printTrimmed(getCursor())
                            ));
                        }
                        n = AddComment.addIfAbsent(n, mark);
                    } else {
                        doAfterVisit(new FindLocalFlowPaths<>(getFlowSpec(newClass)));
                    }
                }
                return n;
            }

            private LocalFlowSpec<Expression, Expression> getFlowSpec(Expression source) {
                switch (flow) {
                    case "data":
                        return new LocalFlowSpec<Expression, Expression>() {
                            @Override
                            public boolean isSource(@NonNull Expression expression, @NonNull Cursor cursor) {
                                return expression == source;
                            }

                            @Override
                            public boolean isSink(@NonNull Expression expression, @NonNull Cursor cursor) {
                                return true;
                            }
                        };
                    case "taint":
                        return new LocalTaintFlowSpec<Expression, Expression>() {
                            @Override
                            public boolean isSource(@NonNull Expression expression, @NonNull Cursor cursor) {
                                return expression == source;
                            }

                            @Override
                            public boolean isSink(@NonNull Expression expression, @NonNull Cursor cursor) {
                                return true;
                            }
                        };
                    default:
                        throw new IllegalStateException("Unknown flow: " + flow);
                }
            }
        };
    }

}