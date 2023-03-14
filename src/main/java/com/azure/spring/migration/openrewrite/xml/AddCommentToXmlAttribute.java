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
import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.marker.Markers;
import org.openrewrite.xml.XPathMatcher;
import org.openrewrite.xml.XmlVisitor;
import org.openrewrite.xml.tree.Xml;

import org.openrewrite.*;
import org.openrewrite.xml.tree.Content;

import java.util.*;

import static org.openrewrite.Tree.randomId;

@Value
@EqualsAndHashCode(callSuper = true)
public class AddCommentToXmlAttribute extends Recipe {

    @Option(displayName = "tag XPath",
        description = "An XPath expression used to find matching tags of the attribute.",
        example = "/project/dependencies/dependency")
    String tagXPath;

    @Option(displayName = "Attribute name",
        description = "The name of the attribute to find.",
        example = "name")
    String attributeName;

    @Option(displayName = "Attribute value regular expression",
        example = "foo.bar.attribute.value.string",
        required = false,
        description = "The regular expression of the value of the attribute to find.")
    @Nullable
    String attributeValueRegex;

    @Option(displayName = "Comment text",
        description = "The text to add as a comment.",
        example = "This is excluded due to CVE <X> and will be removed when we upgrade the next version is available.")
    String commentText;



    @Option(displayName = "File matcher",
        description = "If provided only matching files will be modified. This is a glob expression.",
        required = false,
        example = "'**/application-*.xml'")
    @Nullable
    String fileMatcher;

//    @Option(displayName = "Skip tag Xpath",
//        description = "The tag of whose presence leading to skip adding a comment.",
//        required = false,
//        example = "/project/dependencies/dependency")
//    String skipTagXPath;
//
//    @Option(displayName = "Skip attribute name",
//        description = "The attribute name of whose presence leading to skip adding a comment.",
//        required = false,
//        example = "name")
//    String skipAttributeName;
//
//    @Option(displayName = "Skip attribute value regular expression",
//        example = "(?i)FileAppender",
//        required = false,
//        description = "The regular expression of whose presence leading to skip adding a comment.")
//    @Nullable
//    String skipAttributeValueRegex;

    @Override
    public @NonNull String getDisplayName() {
        return "Add a comment to a `XML` tag of matched attribute";
    }

    @Override
    public @NonNull String getDescription() {
        return "Adds a comment in a `XML` tag of matched attribute."; // if there is no tag/attribute matching skip tag/skip attribute";
    }

    @Override
    protected TreeVisitor<?, ExecutionContext> getSingleSourceApplicableTest() {
        if (fileMatcher != null) {
            return new HasSourcePath<>(fileMatcher);
        }
        return null;
    }


    public boolean checkTag(Xml.Tag tag) {
        boolean found = false;
        java.util.List<Xml.Attribute> attributes = tag.getAttributes();
        for (Xml.Attribute attribute : attributes) {
            if (attribute.getKeyAsString().equals(attributeName))
                if (attributeValueRegex == null || !attribute.getValueAsString().matches(attributeValueRegex)) {
                    found = true;
                    break;
                }
        }
        return found;
    }

//    public boolean checkSkip(Xml.Tag tag) {
//        boolean skip = false;
//        java.util.List<Xml.Attribute> attributes = tag.getAttributes();
//        for (Xml.Attribute attribute : attributes) {
//            if (attribute.getKeyAsString().equals(skipAttributeName))
//                if (skipAttributeValueRegex == null || !attribute.getValueAsString().matches(skipAttributeValueRegex)) {
//                    skip = true;
//                    break;
//                }
//        }
//        return skip;
//    }

    @Override
    public @NonNull TreeVisitor<?, ExecutionContext> getVisitor() {
        return new XmlVisitor<ExecutionContext>() {
            final XPathMatcher matcher = new XPathMatcher(tagXPath);
//            final XPathMatcher skipMatcher = new XPathMatcher(skipTagXPath);

            @Override
            public @NonNull Xml.Tag visitTag(@NonNull Xml.Tag tag, @NonNull ExecutionContext ctx) {
                Xml.Tag t = (Xml.Tag) super.visitTag(tag, ctx);
//                boolean skip = false;
//                if (skipMatcher.matches(getCursor())) {
//                    skip = checkSkip(t);
//                }

                if (matcher.matches(getCursor())) {
                    if (tag.getContent() != null) {
                        if (checkTag(t)) {
                            List<Content> contents = new ArrayList<>(tag.getContent());
                            boolean containsComment = contents.stream()
                                .anyMatch(c -> c instanceof Xml.Comment &&
                                    commentText.equals(((Xml.Comment) c).getText()));
                            if (!containsComment) {
                                int insertPos = 0;
                                Xml.Comment customComment = new Xml.Comment(randomId(),
                                    contents.get(insertPos).getPrefix(),
                                    Markers.EMPTY,
                                    commentText);
                                contents.add(insertPos, customComment);
                                t = t.withContent(contents);
                            }
                        }
                    }
                }
                return t;
            }
        };
    }
}
