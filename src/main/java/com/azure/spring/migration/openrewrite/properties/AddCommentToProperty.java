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

package com.azure.spring.migration.openrewrite.properties;

import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.*;
import org.openrewrite.internal.NameCaseConvention;
import org.openrewrite.internal.StringUtils;
import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.marker.Markers;
import org.openrewrite.properties.PropertiesVisitor;
import org.openrewrite.properties.tree.Properties;
import org.openrewrite.properties.tree.Properties.Comment;
import org.openrewrite.properties.tree.Properties.Content;

@Value
@EqualsAndHashCode(callSuper = true)
public class AddCommentToProperty extends Recipe {
    @Override
    public String getDisplayName() {
        return "Add comment to property";
    }

    @Override
    public String getDescription() {
        return "Add comment to a matched property.";
    }

    @Option(displayName = "Property key",
        description = "The property key to look for.",
        example = "management.metrics.binders.files.enabled")
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
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new PropertiesVisitor<ExecutionContext>() {
            @Override
            public Properties visitFile(Properties.File file, ExecutionContext ctx) {
                List<Content> contents = new ArrayList<>(file.getContent());
                for (int i = 0; i < contents.size(); i++) {
                    Properties.Content content = contents.get(i);
                    if (content instanceof Properties.Entry) {
                        Properties.Entry entry = (Properties.Entry) content;
                        if (!Boolean.FALSE.equals(relaxedBinding) ? NameCaseConvention.matchesGlobRelaxedBinding(entry.getKey(), propertyKey) :
                            StringUtils.matchesGlob(entry.getKey(), propertyKey)) {
                            Properties.Comment comment = new Properties.Comment(Tree.randomId(),
                                "\n",
                                Markers.EMPTY,
                                Properties.Comment.Delimiter.HASH_TAG,
                                commentText);
                            if (i+1==contents.size() ||
                                !(contents.get(i + 1) instanceof Properties.Comment) ||
                                !((Comment) contents.get(i + 1)).getMessage().equals(commentText)) {
                                contents.add(i + 1, comment);
                            }

                        }
                    }
                }
                return file.withContent(contents);

            }
        };
    }
}