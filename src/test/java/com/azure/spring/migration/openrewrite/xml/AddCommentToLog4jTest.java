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

import static org.openrewrite.xml.Assertions.xml;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RewriteTest;

public class AddCommentToLog4jTest implements RewriteTest {

    @Test
    void testAddCommentLog4j2() {
        rewriteRun(
            spec -> spec.recipe(new AddConsoleCommentInLog4j(
                "TODO ASA-CheckLogging: Replace file appender with console appender",
                "**/log{4j,4j2}{-*,}.xml")),
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
                          <!--TODO ASA-CheckLogging: Replace file appender with console appender-->
                          <Appenders>
                              <File name="LogToFile" fileName="logs/app.log">
                                  <PatternLayout>
                                      <Pattern>%d %p %c{1.} [%t] %m%n</Pattern>
                                  </PatternLayout>
                              </File>
                          </Appenders>
                          <Loggers>
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

    @Test
    void testAddCommentLog4j2XmlStrict() {
        rewriteRun(
            spec -> spec.recipe(new AddConsoleCommentInLog4j(
                "TODO ASA-CheckLogging: Replace file appender with console appender",
                "**/log{4j,4j2}{-*,}.xml")),
            xml(
                """
                      <?xml version="1.0" encoding="UTF-8"?>
                      <Configuration status="debug" strict="true" name="XMLConfigTest"
                                     packages="org.apache.logging.log4j.test">
                        <Properties>
                          <Property name="filename">target/test.log</Property>
                        </Properties>
                        <Filter type="ThresholdFilter" level="trace"/>
                        <Appenders>
                          <Appender type="File" name="File" fileName="${filename}">
                            <Layout type="PatternLayout">
                              <Pattern>%d %p %C{1.} [%t] %m%n</Pattern>
                            </Layout>
                          </Appender>
                        </Appenders>
                        <Loggers>
                          <Logger name="org.apache.logging.log4j.test2" level="debug" additivity="false">
                            <AppenderRef ref="File"/>
                          </Logger>
                          <Root level="trace">
                            <AppenderRef ref="STDOUT"/>
                          </Root>
                        </Loggers>
                      </Configuration>
                    """,
                """
                      <?xml version="1.0" encoding="UTF-8"?>
                      <Configuration status="debug" strict="true" name="XMLConfigTest"
                                     packages="org.apache.logging.log4j.test">
                        <Properties>
                          <Property name="filename">target/test.log</Property>
                        </Properties>
                        <Filter type="ThresholdFilter" level="trace"/>
                        <Appenders>
                          <!--TODO ASA-CheckLogging: Replace file appender with console appender-->
                          <Appender type="File" name="File" fileName="${filename}">
                            <Layout type="PatternLayout">
                              <Pattern>%d %p %C{1.} [%t] %m%n</Pattern>
                            </Layout>
                          </Appender>
                        </Appenders>
                        <Loggers>
                          <Logger name="org.apache.logging.log4j.test2" level="debug" additivity="false">
                            <AppenderRef ref="File"/>
                          </Logger>
                          <Root level="trace">
                            <AppenderRef ref="STDOUT"/>
                          </Root>
                        </Loggers>
                      </Configuration>
                    """,
                spec -> spec.path("log4j2-spring.xml")
            )
        );
    }

    @Test
    void testAddCommentLog4j() {
        rewriteRun(
            spec -> spec.recipe(new AddConsoleCommentInLog4j(
                "TODO ASA-CheckLogging: Replace file appender with console appender",
                "**/log{4j,4j2}{-*,}.xml")),
            xml(
                """
                      <!DOCTYPE log4j:configuration PUBLIC "-//APACHE//DTD LOG4J 1.2//EN" "log4j.dtd">
                      <log4j:configuration xmlns:log4j="http://jakarta.apache.org/log4j/">
                        <appender name="A1" class="org.apache.log4j.FileAppender">
                          <param name="File"   value="A1.log" />
                          <param name="Append" value="false" />
                          <layout class="org.apache.log4j.xml.XMLLayout" />
                        </appender>
                        <category name="org.apache.log4j.xml">
                          <priority value="debug" />
                          <appender-ref ref="A1" />
                        </category>
                        <root>
                          <priority value ="debug" />
                          <appender-ref ref="STDOUT" />
                        </Root>
                      </log4j:configuration>
                    """,
                """
                      <!DOCTYPE log4j:configuration PUBLIC "-//APACHE//DTD LOG4J 1.2//EN" "log4j.dtd">
                      <log4j:configuration xmlns:log4j="http://jakarta.apache.org/log4j/">
                        <!--TODO ASA-CheckLogging: Replace file appender with console appender-->
                        <appender name="A1" class="org.apache.log4j.FileAppender">
                          <param name="File"   value="A1.log" />
                          <param name="Append" value="false" />
                          <layout class="org.apache.log4j.xml.XMLLayout" />
                        </appender>
                        <category name="org.apache.log4j.xml">
                          <priority value="debug" />
                          <appender-ref ref="A1" />
                        </category>
                        <root>
                          <priority value ="debug" />
                          <appender-ref ref="STDOUT" />
                        </Root>
                      </log4j:configuration>
                    """,
                spec -> spec.path("log4j2-spring.xml")
            )
        );
    }

    @Test
    void testNotAddCommentLog4j2() {
        rewriteRun(
            spec -> spec.recipe(new AddConsoleCommentInLog4j(
                "TODO ASA-CheckLogging: Replace file appender with console appender",
                "**/log{4j,4j2}{-*,}.xml")),
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
                              <Console name="STDOUT" target="SYSTEM_OUT">
                                    <PatternLayout pattern="%level - %m%n"/>
                              </Console>
                          </Appenders>
                          <Loggers>
                              <Logger name="com.mkyong" level="debug" additivity="false">
                                  <AppenderRef ref="LogToFile"/>
                              </Logger>
                              <Root level="error">
                                  <AppenderRef ref="LogToFile"/>
                              </Root>
                          </Loggers>
                      </Configuration>
                    """,
                spec -> spec.path("log4j2.xml")
            )
        );
    }

    @Test
    void testNotAddCommentLog4j() {
        rewriteRun(
            spec -> spec.recipe(new AddConsoleCommentInLog4j(
                "TODO ASA-CheckLogging: Replace file appender with console appender",
                "**/log{4j,4j2}{-*,}.xml")),
            xml(
                """
                      <!DOCTYPE log4j:configuration PUBLIC "-//APACHE//DTD LOG4J 1.2//EN" "log4j.dtd">
                      <log4j:configuration xmlns:log4j="http://jakarta.apache.org/log4j/">
                        <appender name="A1" class="org.apache.log4j.FileAppender">
                          <param name="File"   value="A1.log" />
                          <param name="Append" value="false" />
                          <layout class="org.apache.log4j.xml.XMLLayout" />
                        </appender>
                        <appender name="STDOUT" class="org.apache.log4j.ConsoleAppender">
                            <layout class="org.apache.log4j.PatternLayout">
                              <param name="ConversionPattern" value="%d %-5p [%t] %C{2} (%F:%L) - %m%n"/>
                            </layout>
                         </appender>
                        <category name="org.apache.log4j.xml">
                          <priority value="debug" />
                          <appender-ref ref="A1" />
                        </category>
                        <root>
                          <priority value ="debug" />
                          <appender-ref ref="STDOUT" />
                        </Root>
                      </log4j:configuration>
                    """,

                spec -> spec.path("log4j-dev.xml")
            )
        );
    }
}
