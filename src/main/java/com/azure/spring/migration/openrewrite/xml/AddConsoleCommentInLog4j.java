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

import static org.openrewrite.Tree.randomId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.ExecutionContext;
import org.openrewrite.HasSourcePath;
import org.openrewrite.Option;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.internal.lang.NonNull;
import org.openrewrite.marker.Markers;
import org.openrewrite.xml.XmlVisitor;
import org.openrewrite.xml.tree.Content;
import org.openrewrite.xml.tree.Xml;

@Value
@EqualsAndHashCode(callSuper = true)
public class AddConsoleCommentInLog4j extends Recipe {

    String log4j2ConfigurationXPath = "/configuration";

    String log4j2AppendersXPath = "/configuration/appenders";
    String log4jConfigurationXPath = "log4j:configuration";
    String appenderTagName = "appender";

    String classAttributeName = "class";

    // lower case
    String fileAppenderKeyword = "fileappender";
    // lower case
    String consoleAppenderKeyword = "consoleappender";

    List<String> fileTagList = Arrays.asList("file", "rollingfile");
    String consoleTagName = "console";

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

    public boolean checkLog4j1(Xml.Tag tag) {
        boolean fileFlag = XmlUtil.searchChildren(tag,appenderTagName,classAttributeName,fileAppenderKeyword);
        boolean consoleFlag = XmlUtil.searchChildren(tag,appenderTagName,classAttributeName,consoleAppenderKeyword);
        return fileFlag && !consoleFlag;
    }

    public boolean checkConciseSyntaxLog4j2(Xml.Tag tag) {
        boolean fileFlag = false;
        for (String fileTagName : fileTagList) {
            if (XmlUtil.dfs(tag, fileTagName)) {
                fileFlag = true;
                break;
            }
        }
        boolean consoleFlag = XmlUtil.dfs(tag,consoleTagName);
        return fileFlag && !consoleFlag;
    }

    public boolean checkStrictSyntaxLog4j2(Xml.Tag tag) {
        boolean fileFlag = false;
        for (String fileTagName : fileTagList) {
            if (XmlUtil.searchChildren(tag, "appender", "type", fileTagName)) {
                fileFlag = true;
                break;
            }
        }
        boolean consoleFlag = XmlUtil.searchChildren(tag, "appender", "type", "console");
        return fileFlag && !consoleFlag;
    }


    @Override
    public @NonNull TreeVisitor<?, ExecutionContext> getVisitor() {
        return new XmlVisitor<ExecutionContext>() {
            final CaseInsensitiveXPathMatcher log4j2ConfigurationTagMatcher = new CaseInsensitiveXPathMatcher(log4j2ConfigurationXPath);
            final CaseInsensitiveXPathMatcher log4j2AppendersTagMatcher = new CaseInsensitiveXPathMatcher(log4j2AppendersXPath);
            final CaseInsensitiveXPathMatcher log4jConfigurationTagMatcher = new CaseInsensitiveXPathMatcher(log4jConfigurationXPath);


            @Override
            public @NonNull Xml.Tag visitTag(@NonNull Xml.Tag tag, @NonNull ExecutionContext ctx) {
                Xml.Tag t = (Xml.Tag) super.visitTag(tag, ctx);
                boolean needComment = false;
                if (log4jConfigurationTagMatcher.matches(getCursor())) {
                    needComment = checkLog4j1(t);
                } else if (log4j2ConfigurationTagMatcher.matches(getCursor())) {
                    needComment = checkConciseSyntaxLog4j2(t);
                } else if (log4j2AppendersTagMatcher.matches(getCursor())) {
                    needComment = checkStrictSyntaxLog4j2(t);
                }
                if (needComment) {
                    return XmlUtil.addComment(tag, t, commentText);
                }
                return t;
            }
        };
    }
}
