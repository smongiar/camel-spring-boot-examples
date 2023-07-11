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
package org.apache.camel.springboot.example;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoadBalancerEIPRouter extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        // @formatter:off
		restConfiguration().component("servlet");
		rest()
			.post("/round-robin").to("direct:loadbalancer-round-robin")
			.post("/random").to("direct:loadbalancer-random")
			.post("/sticky").to("direct:loadbalancer-sticky")
			.post("/topic").to("direct:loadbalancer-topic")
			.post("/failover").to("direct:loadbalancer-failover")
			.post("/weighted").to("direct:loadbalancer-weighted-round-robin")
			.post("/custom").to("direct:loadbalancer-custom");

		// round-robin load balancer
		from("direct:loadbalancer-round-robin")
				.loadBalance().roundRobin()
				.to("direct:roundrobin1")
				.to("direct:roundrobin2")
				.end();

		// random load balancer
		from("direct:loadbalancer-random")
				.loadBalance().random()
				.to("direct:random1")
				.to("direct:random2");

		// sticky load balancer
		from("direct:loadbalancer-sticky")
				// expression parameter to calculate the correlation key
				.loadBalance().sticky(header("correlation-key"))
				.to("direct:sticky1")
				.to("direct:sticky2");

		// topic ("fan out") load-balancer
		from("direct:loadbalancer-topic")
				.id("start")
				.loadBalance().topic()
				.to("direct:topic1")
				.to("direct:topic2")
				.end();

		// failover load-balancer
		from("direct:loadbalancer-failover")
				.loadBalance()
				// failover on this Exception to subsequent producer
				.failover(MyException.class)
				.to("direct:failover1")
				.to("direct:failover2");
		from("direct:failover1")
				.log("Failover: Route 1 received message ${body}")
				.log("Failover: Route 1 throws an exception to simulate processing error")
				.process(exchange -> {
					throw new MyException("Failover");
				});
		from("direct:failover2").log("Failover: Route 2 received message ${body}");

		// weighted load-balancer round robin
		final String distributionRatio = "3,1";
		from("direct:loadbalancer-weighted-round-robin")
				.loadBalance().weighted(true, distributionRatio)
				.to("direct:weighted1")
				.to("direct:weighted2");

		// custom load balancer
		from("direct:loadbalancer-custom")
				// custom load balancer
				.loadBalance(new CustomLoadBalancer())
				.to("direct:custom1")
				.to("direct:custom2");

		// Create the direct routes that only log the received message
		for (String type : List.of("RoundRobin", "Random", "Sticky", "Topic", "Weighted", "Custom")) {
			for (int i = 1; i <= 2; i++) {
				fromF("direct:%s%d", type.toLowerCase(), i).log(String.format("%s: Route %d received message ${body}", type, i));
			}
		}
	}

}
