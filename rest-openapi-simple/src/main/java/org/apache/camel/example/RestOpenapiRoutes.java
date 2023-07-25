package org.apache.camel.example;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class RestOpenapiRoutes extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        // @formatter:off
        from("timer:t?period=5000")
            .setHeader("petId", header(Exchange.TIMER_COUNTER))
            .log("Retrieving pet with id ${header.petId}")
        .to("rest-openapi:{{specification.url}}#getPetById?host={{host.url}}")
            .log("${body}");
        // @formatter:on
    }
}
