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

package com.azure.spring.migration.openrewrite.xml;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.ExecutionContext;
import org.openrewrite.HasSourcePath;
import org.openrewrite.Option;
import org.openrewrite.Preconditions;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.internal.lang.NonNull;
import org.openrewrite.xml.XmlVisitor;
import org.openrewrite.xml.tree.Xml;

@Value
@EqualsAndHashCode(callSuper = true)
public class AddConsoleCommentInLogback extends Recipe {

    private static final String CONFIGURATION_XPATH = "/configuration";
    private static final String APPENDER_TAG_NAME = "appender";

    private static final XmlUtil.AttributeToFind KEY_ATTRIBUTE = new XmlUtil.AttributeToFind("class", "consoleappender");

    @Option(displayName = "Comment text",
        description = "The text to add as a comment.")
    String commentText;

    @Option(displayName = "File matcher",
        description = "If provided only matching files will be modified. This is a glob expression.",
        example = "'**/application-*.xml'")
    String fileMatcher;

    @Override
    public @NonNull String getDisplayName() {
        return "Add a comment to a `XML` tag of matched attribute";
    }

    @Override
    public @NonNull String getDescription() {
        return "Adds a comment in a `XML` tag of matched attribute.";
    }

    private boolean checkLogbackHasConsole(Xml.Tag tag) {
        return !XmlUtil.searchChildTag(tag, APPENDER_TAG_NAME) || XmlUtil.searchChildAttribute(tag, APPENDER_TAG_NAME, KEY_ATTRIBUTE.attributeName,
            KEY_ATTRIBUTE.attributeValueKeyword);
    }

    @Override
    public @NonNull TreeVisitor<?, ExecutionContext> getVisitor() {
        return Preconditions.check(fileMatcher != null ? new HasSourcePath<>(fileMatcher) : TreeVisitor.noop(), new XmlVisitor<ExecutionContext>() {
            final CaseInsensitiveXPathMatcher configurationTagMatcher = new CaseInsensitiveXPathMatcher(CONFIGURATION_XPATH);

            @Override
            public @NonNull Xml.Tag visitTag(@NonNull Xml.Tag tag, @NonNull ExecutionContext ctx) {
                Xml.Tag t = (Xml.Tag) super.visitTag(tag, ctx);
                if (configurationTagMatcher.matches(getCursor())) {
                    if (!checkLogbackHasConsole(t)) {
                        return XmlUtil.addComment(tag, t, commentText);
                    }
                }
                return t;
            }
        });
    }
}
