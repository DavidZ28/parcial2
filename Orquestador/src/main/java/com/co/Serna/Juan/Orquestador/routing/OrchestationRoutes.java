package com.co.Serna.Juan.Orquestador.routing;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class OrchestationRoutes extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        onException(Exception.class)
                .log("Error occurred: ${exception.message}")
                .setBody(simple("{\"error\": \"Service unavailable\"}"))
                .handled(true);

        from("direct:orchestrateSteps")
                .log("Starting orchestration...")

                .to("direct:getStepOne")
                .log("Received Step 1: ${body}")
                .setProperty("step1", simple("${body}"))

                .to("direct:getStepTwo")
                .log("Received Step 2: ${body}")
                .setProperty("step2", simple("${body}"))

                .to("direct:getStepThree")
                .log("Received Step 3: ${body}")
                .setProperty("step3", simple("${body}"))

                .process(exchange -> {
                    String step1 = exchange.getProperty("step1", String.class);
                    String step2 = exchange.getProperty("step2", String.class);
                    String step3 = exchange.getProperty("step3", String.class);

                    String finalAnswer = "Step1: " + step1 + " - Step2: " + step2 + " - Step3: " + step3;
                    exchange.getIn().setBody(finalAnswer);
                })
                .log("Final answer: ${body}");
    }
}
