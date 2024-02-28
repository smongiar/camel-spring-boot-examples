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
package org.apache.camel.example.kafka.offsetRepository;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.StateRepository;
import org.apache.camel.support.processor.state.FileStateRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;

@Component
public class OffsetRepoRoutes extends RouteBuilder {
    @Bean(name = "offsetRepo")
    StateRepository<String, String> offsetRepository(@Value("${offset.repository.path}") String path) {
        return new FileStateRepository(new File(path), new HashMap<>());
    }

    @Override
    public void configure() throws Exception {
        from("timer://foo?period={{period}}")
                .setBody(header(Exchange.TIMER_COUNTER).prepend("Message #"))
                .to("kafka:{{topic}}")
                .log("Produced ${body}");

        from("kafka:{{topic}}?offsetRepository=#offsetRepo&autoOffsetReset=earliest")
                .log("Received ${body}");
    }
}
