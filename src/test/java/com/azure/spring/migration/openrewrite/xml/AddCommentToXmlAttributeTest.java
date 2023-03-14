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
public class AddCommentToXmlAttributeTest implements RewriteTest {

    @Test
    void testLogback() {
        rewriteRun(
            spec -> spec.recipe(new AddCommentToXmlAttribute(
                "/configuration/Appender", "class","(?i)FileAppender",
                "TODO ASA-FindFileAppenderLogging: Replace file appender with console appender","**/log{back,4j,4j2}{-*,}.xml")),
            xml(
                """
                      <configuration>
                         <timestamp key="byDay" datePattern="yyyyMMdd'T'HHmmss"/>
                         <appender name="FILE" class="ch.qos.logback.core.FileAppender">
                            <file> log-${byDay}.txt </file>
                            <append>true</append>
                            <encoder>
                               <pattern>%-4relative [%thread] %-5level %logger{35} - %msg%n</pattern>
                            </encoder>
                         </appender>
                         <root level="debug">
                            <appender-ref ref="FILE" />
                            <appender-ref ref="STDOUT" />
                         </root>
                      </configuration>
                    """,
                """
                      <configuration>
                         <timestamp key="byDay" datePattern="yyyyMMdd'T'HHmmss"/>
                         <appender name="FILE" class="ch.qos.logback.core.FileAppender">
                            <!--TODO ASA-FindFileAppenderLogging: Replace file appender with console appender-->
                            <file> log-${byDay}.txt </file>
                            <append>true</append>
                            <encoder>
                               <pattern>%-4relative [%thread] %-5level %logger{35} - %msg%n</pattern>
                            </encoder>
                         </appender>
                         <root level="debug">
                            <appender-ref ref="FILE" />
                            <appender-ref ref="STDOUT" />
                         </root>
                      </configuration>
                    """,
                spec -> spec.path("logback-spring.xml")
            )
        );
    }

    @Test
    void testLog4j2() {
        rewriteRun(
            spec -> spec.recipe(new AddCommentToXmlAttribute(
                "/Configuration/Appenders/File", null, null,
                "TODO ASA-FindFileAppenderLogging: Replace file appender with console appender","**/log{back,4j,4j2}{-*,}.xml")),
            xml(
                """
                      <?xml version="1.0" encoding="UTF-8"?>
                      <Configuration status="DEBUG">
                          <Appenders>
                              <File name="LogToFile" fileName="logs/app.log">
                                  <PatternLayout>
                                      <Pattern>%d %p %c{1.} [%t] %m%n</Pattern>
                                  </PatternLayout>
                              </File>
                          </Appenders>
                          <Loggers>
                              <!-- avoid duplicated logs with additivity=false -->
                              <Logger name="com.mkyong" level="debug" additivity="false">
                                  <AppenderRef ref="LogToFile"/>
                              </Logger>
                              <Root level="error">
                                  <AppenderRef ref="LogToFile"/>
                              </Root>
                          </Loggers>
                      </Configuration>
                    """,
                """
                      <?xml version="1.0" encoding="UTF-8"?>
                      <Configuration status="DEBUG">
                          <Appenders>
                              <File name="LogToFile" fileName="logs/app.log">
                                  <!--TODO ASA-FindFileAppenderLogging: Replace file appender with console appender-->
                                  <PatternLayout>
                                      <Pattern>%d %p %c{1.} [%t] %m%n</Pattern>
                                  </PatternLayout>
                              </File>
                          </Appenders>
                          <Loggers>
                              <!-- avoid duplicated logs with additivity=false -->
                              <Logger name="com.mkyong" level="debug" additivity="false">
                                  <AppenderRef ref="LogToFile"/>
                              </Logger>
                              <Root level="error">
                                  <AppenderRef ref="LogToFile"/>
                              </Root>
                          </Loggers>
                      </Configuration>
                    """,
                spec -> spec.path("log4j2-spring.xml")
            )
        );
    }
}
