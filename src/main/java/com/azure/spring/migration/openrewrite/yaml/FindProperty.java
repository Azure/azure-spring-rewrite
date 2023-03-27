/*
 * Copyright 2021 the original author or authors.
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

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.*;
import org.openrewrite.internal.NameCaseConvention;
import org.openrewrite.internal.StringUtils;
import org.openrewrite.internal.lang.NonNull;
import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.yaml.YamlIsoVisitor;
import org.openrewrite.yaml.YamlVisitor;
import org.openrewrite.yaml.tree.Yaml;

import java.util.*;

@Value
@EqualsAndHashCode(callSuper = true)
public class FindProperty extends Recipe {

    @Option(displayName = "Property key",
        description = "The key to look for. Glob is supported.",
        example = "management.metrics.binders.*.enabled")
    String propertyKey;

    @Option(displayName = "Comment text",
        description = "The text to add as a comment.")
    String commentText;

    @Incubating(since = "7.17.0")
    @Option(displayName = "Use relaxed binding",
        description = "Whether to match the `propertyKey` using [relaxed binding](https://docs.spring.io/spring-boot/docs/2.5.6/reference/html/features.html#features.external-config.typesafe-configuration-properties.relaxed-binding) " +
            "rules. Default is `true`. Set to `false`  to use exact matching.",
        required = false)
    @Nullable
    Boolean relaxedBinding;

    @Override
    public @NonNull String getDisplayName() {
        return "Find YAML properties";
    }

    @Override
    public @NonNull String getDescription() {
        return "Find a YAML property. Nested YAML mappings are interpreted as dot separated property names, i.e. " +
            " as Spring Boot interprets `application.yml` files.";
    }

    String HASH_TAG = "#";
    String wrapComment(String commentText, String indent) {
        return HASH_TAG + commentText + indent;
    }

    @Override
    public @NonNull YamlVisitor<ExecutionContext> getVisitor() {
        return new YamlIsoVisitor<>() {
            @Override
            public @NonNull Yaml.Mapping.Entry visitMappingEntry(@NonNull Yaml.Mapping.Entry entry,
                                                                 @NonNull ExecutionContext ctx) {
                Yaml.Mapping.Entry e = super.visitMappingEntry(entry, ctx);
                String prop = getProperty(getCursor());
                if (!Boolean.FALSE.equals(relaxedBinding) ?
                    NameCaseConvention.matchesGlobRelaxedBinding(prop, propertyKey) :
                    StringUtils.matchesGlob(prop, propertyKey)) {
                    String wrappedComment = wrapComment(commentText, e.getPrefix());
                    if (!e.getKey().getPrefix().startsWith(wrappedComment)) {
                        e = e.withKey(e.getKey().withPrefix(wrappedComment));
                    }
                }
                return e;
            }

        };
    }

    private static String getProperty(Cursor cursor) {
        StringBuilder asProperty = new StringBuilder();
        Iterator<Object> path = cursor.getPath();
        int i = 0;
        while (path.hasNext()) {
            Object next = path.next();
            if (next instanceof Yaml.Mapping.Entry entry) {
                if (i++ > 0) {
                    asProperty.insert(0, '.');
                }
                asProperty.insert(0, entry.getKey().getValue());
            }
        }
        return asProperty.toString();
    }
}