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

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Incubating;
import org.openrewrite.Option;
import org.openrewrite.Recipe;
import org.openrewrite.Tree;
import org.openrewrite.TreeVisitor;
import org.openrewrite.internal.NameCaseConvention;
import org.openrewrite.internal.StringUtils;
import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.internal.lang.NonNull;
import org.openrewrite.marker.Markers;
import org.openrewrite.properties.PropertiesVisitor;
import org.openrewrite.properties.tree.Properties;
import org.openrewrite.properties.tree.Properties.Comment;
import org.openrewrite.properties.tree.Properties.Content;
import org.openrewrite.properties.tree.Properties.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@Value
@EqualsAndHashCode(callSuper = true)
public class AddCommentToProperty extends Recipe {

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
    public @NonNull String getDisplayName() {
        return "Add comment to property";
    }

    @Override
    public @NonNull String getDescription() {
        return "Add comment to a matched property.";
    }

    @Override
    public @NonNull TreeVisitor<?, ExecutionContext> getVisitor() {
        return new PropertiesVisitor<>() {
            @Override
            public @NonNull Properties visitFile(@NonNull Properties.File file, @NonNull ExecutionContext ctx) {
                List<Content> contents = new ArrayList<>(file.getContent());
                boolean needComment = false;
                ListIterator<Content> iterator = contents.listIterator(contents.size());
                while (iterator.hasPrevious()) {
                    Content content = iterator.previous();
                    if (content instanceof Entry entry) {
                        if (!Boolean.FALSE.equals(relaxedBinding) ? NameCaseConvention.matchesGlobRelaxedBinding(
                            entry.getKey(), propertyKey) :
                            StringUtils.matchesGlob(entry.getKey(), propertyKey)) {
                            Comment comment = new Comment(Tree.randomId(),
                                entry.getPrefix(),
                                Markers.EMPTY,
                                Comment.Delimiter.HASH_TAG,
                                commentText);
                            if (!iterator.hasPrevious() ||
                                !(contents.get(iterator.previousIndex()) instanceof Comment) ||
                                !((Comment) contents.get(iterator.previousIndex())).getMessage().equals(commentText)) {
                                needComment = true;
                                iterator.set(entry.withPrefix("\n"));
                                iterator.add(comment);
                            }
                        }
                    }
                }
                if (needComment) {
                    return file.withContent(contents);
                } else {
                    return file;
                }

            }
        };
    }
}
