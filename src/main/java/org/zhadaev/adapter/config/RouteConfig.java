package org.zhadaev.adapter.config;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.zhadaev.adapter.model.MessageA;
import org.zhadaev.adapter.model.MessageB;
import org.zhadaev.adapter.service.IMeteoService;

import java.time.Instant;

@Configuration
public class RouteConfig extends RouteBuilder {

    @Autowired
    private IMeteoService meteoService;

    private final String serviceB = "http://localhost:8080/serviceb";

    @Override
    public void configure() {

        restConfiguration()
                .component("servlet")
                .host("localhost").port(8080)
                .bindingMode(RestBindingMode.json);

        onException(Exception.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .setBody(simple("${exception.message}\n"));

        rest("/adapter")
                .post().type(MessageA.class)
                .route()
                    .choice()
                        .when().simple("${body.lng} == \"ru\"")
                            .process(exchange -> {
                                MessageA msgA = exchange.getIn().getBody(MessageA.class);
                                if (msgA.getMsg().isEmpty()) throw new Exception();

                                int currentTemp = meteoService.getCurrentTemp(exchange.getContext().createProducerTemplate(), msgA.getCoordinates(), msgA.getLng());

                                ProducerTemplate pt = exchange.getContext().createProducerTemplate();
                                Exchange ex = pt.send("direct:adapter", exch -> {
                                    MessageB msgB = new MessageB();
                                    msgB.setTxt(msgA.getMsg());
                                    msgB.setCreatedDt(Instant.now().toString());
                                    msgB.setCurrentTemp(currentTemp);
                                    exch.getIn().setBody(msgB);
                                });
                                Integer responseCode = ex.getMessage().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
                                if (responseCode >= 500) throw new Exception();
                            });

        from("direct:adapter")
                .marshal().json()
                .to(serviceB + "?bridgeEndpoint=true");

    }

}
