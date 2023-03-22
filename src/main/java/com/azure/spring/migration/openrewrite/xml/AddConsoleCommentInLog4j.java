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

import com.azure.spring.migration.openrewrite.xml.XmlUtil.AttributeToFind;
import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.ExecutionContext;
import org.openrewrite.HasSourcePath;
import org.openrewrite.Option;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.internal.lang.NonNull;
import org.openrewrite.xml.XmlVisitor;
import org.openrewrite.xml.tree.Xml;

@Value
@EqualsAndHashCode(callSuper = true)
public class AddConsoleCommentInLog4j extends Recipe {
    private static final String LOG4J2_APPENDERS_XPATH = "/configuration/appenders";
    private static final String LOG4J_CONFIGURATION_XPATH = "log4j:configuration";
    private static final String APPENDER_TAG_NAME = "appender";
    private static final String CONSOLE_TAG_NAME = "console";

    private static final Map<String, XmlUtil.AttributeToFind> ATTRIBUTE_MAP = new HashMap<String, XmlUtil.AttributeToFind>() {{
        put("log4j1", new XmlUtil.AttributeToFind("class", "consoleappender"));
        put("log4j2", new XmlUtil.AttributeToFind("type", "console"));
    }};


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

    @Override
    protected TreeVisitor<?, ExecutionContext> getSingleSourceApplicableTest() {
        return new HasSourcePath<>(fileMatcher);
    }

public boolean checkLog4j1HasConsole(Xml.Tag tag) {
    AttributeToFind log4j1KeyAttribute = ATTRIBUTE_MAP.get("log4j1");
    return XmlUtil.searchChildren(tag, APPENDER_TAG_NAME, log4j1KeyAttribute.attributeName, log4j1KeyAttribute.attributeValueKeyword);
}

public boolean checkLog4j2HasConsole(Xml.Tag tag) {
    AttributeToFind log4j2StrictAttribute = ATTRIBUTE_MAP.get("log4j2");
    return XmlUtil.dfs(tag, CONSOLE_TAG_NAME) ||
        XmlUtil.searchChildren(tag, APPENDER_TAG_NAME, log4j2StrictAttribute.attributeName, log4j2StrictAttribute.attributeValueKeyword);
}

    @Override
    public @NonNull TreeVisitor<?, ExecutionContext> getVisitor() {
        return new XmlVisitor<ExecutionContext>() {
            final CaseInsensitiveXPathMatcher log4j2AppendersTagMatcher = new CaseInsensitiveXPathMatcher(LOG4J2_APPENDERS_XPATH);
            final CaseInsensitiveXPathMatcher log4jConfigurationTagMatcher = new CaseInsensitiveXPathMatcher(LOG4J_CONFIGURATION_XPATH);

            @Override
            public @NonNull Xml.Tag visitTag(@NonNull Xml.Tag tag, @NonNull ExecutionContext ctx) {
                Xml.Tag t = (Xml.Tag) super.visitTag(tag, ctx);
                if (log4jConfigurationTagMatcher.matches(getCursor())) {
                    if (!checkLog4j1HasConsole(t)) {
                        return XmlUtil.addComment(tag, t, commentText);
                    }
                } else if (log4j2AppendersTagMatcher.matches(getCursor())) {
                    if (!checkLog4j2HasConsole(t)) {
                        return XmlUtil.addComment(tag, t, commentText);
                    }
                }
                return t;
            }
        };
    }
}
