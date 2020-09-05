package org.zhadaev.adapter.config;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;
import org.zhadaev.adapter.model.MessageA;

@Component
public class RouteConfig extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        rest("/adapter")
                .post().type(MessageA.class)
                .route()
                    .choice()
                        .when().simple("${body.lng} == \"ru\"")
                            .to("direct:adapter");

        restConfiguration()
                .component("servlet")
                .host("localhost").port(8080)
                .bindingMode(RestBindingMode.json);

        from("direct:adapter")
                .process(exchange -> {
                    MessageA msgA = exchange.getIn().getBody(MessageA.class);
                });

    }
}
