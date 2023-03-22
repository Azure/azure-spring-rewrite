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
import java.util.List;
import org.openrewrite.marker.Markers;
import org.openrewrite.xml.tree.Content;
import org.openrewrite.xml.tree.Xml;

public class XmlUtil {

    static class AttributeToFind {
        String attributeName;
        String attributeValueKeyword;
        public AttributeToFind(String attributeName, String attributeValueKeyword) {
            this.attributeName = attributeName;
            this.attributeValueKeyword = attributeValueKeyword;
        }
    }

    public static boolean dfs(Xml.Tag tag, String target) {
        if (tag == null) {
            return false;
        }
        if (tag.getName().equalsIgnoreCase(target)) {
            return true;
        }
        for (Xml.Tag child : tag.getChildren()) {
            if (dfs(child, target)) {
                return true;
            }
        }
        return false;
    }

    public static boolean searchChildren(Xml.Tag tag, String targetTagName, String targetAttributeName, String targetAttributeValueKeyword) {
        boolean targetTagNameFound = false;
        for (Xml.Tag child : tag.getChildren()) {
            if (child.getName().equalsIgnoreCase(targetTagName)) {
                targetTagNameFound = true;
                for (Xml.Attribute attribute : child.getAttributes()) {
                    if (attribute.getKeyAsString().equalsIgnoreCase(targetAttributeName) &&
                        attribute.getValueAsString().toLowerCase().contains(targetAttributeValueKeyword)) {
                        return true;
                    }
                }
            }
        }
        return !targetTagNameFound;
    }

    public static Xml.Tag addComment(Xml.Tag tag, Xml.Tag t, String commentText) {
        if (tag.getContent() != null) {
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
            return t;
        }
        return t;
    }

}
