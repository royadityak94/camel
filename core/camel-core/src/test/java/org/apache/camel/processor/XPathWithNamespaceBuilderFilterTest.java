/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.processor;

import org.apache.camel.ContextTestSupport;
import org.apache.camel.Endpoint;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.builder.Namespaces;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class XPathWithNamespaceBuilderFilterTest extends ContextTestSupport {
    protected Endpoint startEndpoint;
    protected MockEndpoint resultEndpoint;

    @Test
    public void testSendMatchingMessage() throws Exception {
        resultEndpoint.expectedMessageCount(1);

        template.sendBody("direct:start", "<person xmlns='http://acme.com/cheese' name='James' city='London'/>");

        resultEndpoint.assertIsSatisfied();
    }

    @Test
    public void testSendNotMatchingMessage() throws Exception {
        resultEndpoint.expectedMessageCount(0);

        template.sendBody("direct:start", "<person xmlns='http://acme.com/cheese'  name='Hiram' city='Tampa'/>");

        resultEndpoint.assertIsSatisfied();
    }

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        startEndpoint = resolveMandatoryEndpoint("direct:start");
        resultEndpoint = getMockEndpoint("mock:result");
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                // START SNIPPET: example
                // lets define the namespaces we'll need in our filters
                Namespaces ns = new Namespaces("c", "http://acme.com/cheese").add("xsd", "http://www.w3.org/2001/XMLSchema");

                // now lets create an xpath based Message Filter
                from("direct:start").filter(xpath("/c:person[@name='James']", ns)).to("mock:result");
                // END SNIPPET: example
            }
        };
    }
}
