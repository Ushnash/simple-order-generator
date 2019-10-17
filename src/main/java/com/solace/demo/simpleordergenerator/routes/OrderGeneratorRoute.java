package com.solace.demo.simpleordergenerator.routes;

import static org.apache.camel.processor.idempotent.MemoryIdempotentRepository.memoryIdempotentRepository;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * A Simple Camel route that randomly generates "order numbers" between 1 & n for sending to the Solace PubSub+ event broker.
 * The first time an order is generated, it is sent to an initialization queue. Subsequent occurrences of the order 
 * take advantage of PubSub+'s Topic-to-Queue mapping feature & send to a dedicated subtopic.
 * 
 * @author Ush Shukla
 *
 */

@Component
public class OrderGeneratorRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
	from("timer:orderTimer?period={{ordergen.period}}").routeId("OrderGeneratorRoute")

	        //generate a random order #
		.setHeader("OrderNumber").simple("${random(1,{{ordergen.ordernumber.max}})}")
		
		//Use an idempotent repository to store the first occurrence of an order.
		//The first occurrence of an order is sent to a work-intake queue.
		//Subsequent iterations are marked as "duplicate" & sent to an order-specific
		//topic.
		.idempotentConsumer(header("OrderNumber"), memoryIdempotentRepository(200)).skipDuplicate(false)
		.filter(exchangeProperty(Exchange.DUPLICATE_MESSAGE).isEqualTo(true))
                  .toD("solace-jms:topic:order/${header.OrderNumber}")
		  .log("Order #${header.OrderNumber} routed to sub-topic")
		  .stop()
		.end()
		.to("{{ordergen.start.channel}}")
		.log("Order #${header.OrderNumber} routed to {{ordergen.start.channel}}");
    }
}
