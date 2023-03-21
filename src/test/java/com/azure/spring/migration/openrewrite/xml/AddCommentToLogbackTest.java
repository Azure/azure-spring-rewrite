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

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.xml.Assertions.xml;
public class AddCommentToLogbackTest implements RewriteTest {

    @Test
    void testAddComment() {
        rewriteRun(
            spec -> spec.recipe(new AddConsoleCommentInLogback(
                "TODO ASA-CheckLogging: Replace file appender with console appender",
                "**/logback{-*,}.xml")),
            xml(
                """
                      <Configuration>
                         <timestamp key="byDay" datePattern="yyyyMMdd'T'HHmmss"/>
                         <Appender name="FILE" class="ch.qos.logback.core.FileAppender">
                            <file> log-${byDay}.txt </file>
                            <append>true</append>
                            <encoder>
                               <pattern>%-4relative [%thread] %-5level %logger{35} - %msg%n</pattern>
                            </encoder>
                         </Appender>
                         <Appender name="FILE" class="ch.qos.logback.core.FileAppender">
                            <file> log-${byDay}.txt </file>
                         </Appender>
                         <root level="debug">
                            <appender-ref ref="FILE" />
                            <appender-ref ref="STDOUT" />
                         </root>
                      </Configuration>
                    """,
                """
                      <Configuration>
                         <!--TODO ASA-CheckLogging: Replace file appender with console appender-->
                         <timestamp key="byDay" datePattern="yyyyMMdd'T'HHmmss"/>
                         <Appender name="FILE" class="ch.qos.logback.core.FileAppender">
                            <file> log-${byDay}.txt </file>
                            <append>true</append>
                            <encoder>
                               <pattern>%-4relative [%thread] %-5level %logger{35} - %msg%n</pattern>
                            </encoder>
                         </Appender>
                         <Appender name="FILE" class="ch.qos.logback.core.FileAppender">
                            <file> log-${byDay}.txt </file>
                         </Appender>
                         <root level="debug">
                            <appender-ref ref="FILE" />
                            <appender-ref ref="STDOUT" />
                         </root>
                      </Configuration>
                    """,
                spec -> spec.path("logback-spring.xml")
            )
        );
    }

    @Test
    void testNotAddComment() {
        rewriteRun(
            spec -> spec.recipe(new AddConsoleCommentInLogback(
                "TODO ASA-CheckLogging: Replace file appender with console appender",
                "**/logback{-*,}.xml")),
            xml(
                """
                      <configuration>
                        <appender name="FILE" class="ch.qos.logback.core.FileAppender">
                          <file>myApp.log</file>
                          <encoder>
                            <pattern>%date %level [%thread] %logger{10} [%file:%line] -%kvp- %msg%n</pattern>
                          </encoder>
                        </appender>
                        <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
                          <encoder>
                            <pattern>%kvp %msg%n</pattern>
                          </encoder>
                        </appender>
                        <root level="debug">
                          <appender-ref ref="FILE" />
                          <appender-ref ref="STDOUT" />
                        </root>
                      </configuration>
                    """,
                spec -> spec.path("logback.xml")
            )
        );
    }


}
