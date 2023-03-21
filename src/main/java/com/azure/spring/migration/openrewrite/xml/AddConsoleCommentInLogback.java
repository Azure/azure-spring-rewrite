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

import java.util.ArrayList;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Option;
import org.openrewrite.Recipe;
import org.openrewrite.internal.lang.NonNull;
import org.openrewrite.marker.Markers;
import org.openrewrite.xml.XmlVisitor;
import org.openrewrite.xml.tree.Xml;

import org.openrewrite.*;
import org.openrewrite.xml.tree.Content;

import java.util.*;

import static org.openrewrite.Tree.randomId;

@Value
@EqualsAndHashCode(callSuper = true)
public class AddConsoleCommentInLogback extends Recipe {

    String configurationXPath = "/configuration";
    String appenderTagName = "appender";

    String classAttributeName = "class";

    // lower case
    String fileAppenderKeyword = "fileappender";
    // lower case
    String consoleAppenderKeyword = "consoleappender";

    @Option(displayName = "Comment text",
        description = "The text to add as a comment.",
        example = "This is excluded due to CVE <X> and will be removed when we upgrade the next version is available.")
    String commentText;

    @Option(displayName = "File matcher",
        description = "If provided only matching files will be modified. This is a glob expression.",
        required = true,
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

    @Override
    protected TreeVisitor<?, ExecutionContext> getSingleSourceApplicableTest() {
        return new HasSourcePath<>(fileMatcher);
    }


    public boolean checkAppendersLogback(Xml.Tag tag) {
        boolean fileFlag = XmlUtil.searchChildren(tag,appenderTagName,classAttributeName,fileAppenderKeyword);
        boolean consoleFlag = XmlUtil.searchChildren(tag,appenderTagName,classAttributeName,consoleAppenderKeyword);
        return fileFlag && !consoleFlag;
    }


    @Override
    public @NonNull TreeVisitor<?, ExecutionContext> getVisitor() {
        return new XmlVisitor<ExecutionContext>() {
            final CaseInsensitiveXPathMatcher configurationTagMatcher = new CaseInsensitiveXPathMatcher(configurationXPath);

            @Override
            public @NonNull Xml.Tag visitTag(@NonNull Xml.Tag tag, @NonNull ExecutionContext ctx) {
                Xml.Tag t = (Xml.Tag) super.visitTag(tag, ctx);
                if (configurationTagMatcher.matches(getCursor())) {
                    if (checkAppendersLogback(t)) {
                        return XmlUtil.addComment(tag, t, commentText);
                    }
                }
                return t;
            }
        };
    }
}
