package org.zhadaev.adapter;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.rest.RestBindingMode;
import org.junit.*;
import org.springframework.boot.SpringApplication;
import org.zhadaev.adapter.model.Coordinates;
import org.zhadaev.adapter.model.MessageA;
import org.zhadaev.adapter.model.MessageB;

import static org.junit.Assert.*;

public class AdapterTest {

    private static CamelContext camelContextServiceA;
    private static CamelContext camelContextServiceB;
    private static MessageB msgB;

    @BeforeClass
    public static void setUp() throws Exception {

        SpringApplication.run(Adapter.class);

        camelContextServiceA = new DefaultCamelContext();
        camelContextServiceB = new DefaultCamelContext();

        camelContextServiceA.addRoutes(new RouteBuilder() {
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

                from("direct:serviceA")
                        .marshal().json()
                        .to("http://localhost:8080/adapter");
            }
        });

        camelContextServiceB.addRoutes(new RouteBuilder() {
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

                rest("/serviceb")
                        .post().type(MessageB.class)
                        .route()
                        .process(exchange -> {
                            msgB = exchange.getIn().getBody(MessageB.class);
                        });

            }
        });

        camelContextServiceA.start();
        camelContextServiceB.start();

    }

    @AfterClass
    public static void stopContext() {
        camelContextServiceA.stop();
        camelContextServiceB.stop();
    }

    @Test
    public void sendMessage() {
        String message = "Погода в Самаре";

        ProducerTemplate templateServiceA = camelContextServiceA.createProducerTemplate();
        Exchange ex = templateServiceA.send("direct:serviceA", exchange -> {
            MessageA msgA = new MessageA();
            msgA.setMsg(message);
            msgA.setLng("ru");
            Coordinates coordinates = new Coordinates();
            coordinates.setLatitude("53.200");
            coordinates.setLongitude("50.150");
            msgA.setCoordinates(coordinates);
            exchange.getIn().setBody(msgA);
        });
        Integer responseCode = ex.getMessage().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);

        assertEquals(200, (int) responseCode);
        assertEquals(message, msgB.getTxt());
    }

    @Test
    public void sendEmptyMessage() {
        ProducerTemplate templateServiceA = camelContextServiceA.createProducerTemplate();
        Exchange ex = templateServiceA.send("direct:serviceA", exchange -> {
            MessageA msgA = new MessageA();
            msgA.setLng("ru");
            Coordinates coordinates = new Coordinates();
            coordinates.setLatitude("53.200");
            coordinates.setLongitude("50.150");
            msgA.setCoordinates(coordinates);
            exchange.getIn().setBody(msgA);
            exchange.getIn().setBody(msgA);
        });
        Integer responseCode = ex.getMessage().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);

        assertNotEquals(200, (int) responseCode);
    }

    @Test
    public void sendIncorrectMessage() {
        ProducerTemplate templateServiceA = camelContextServiceA.createProducerTemplate();
        Exchange ex = templateServiceA.send("direct:serviceA", exchange -> {
            exchange.getIn().setBody("Incorrect message");
        });
        Integer responseCode = ex.getMessage().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);

        assertNotEquals(200, (int) responseCode);
    }

    @Test
    public void sendEmptyCoordinates() {
        ProducerTemplate templateServiceA = camelContextServiceA.createProducerTemplate();
        Exchange ex = templateServiceA.send("direct:serviceA", exchange -> {
            MessageA msgA = new MessageA();
            msgA.setMsg("Погода на острове Java");
            msgA.setLng("ru");
            Coordinates coordinates = new Coordinates();
            msgA.setCoordinates(coordinates);
            exchange.getIn().setBody(msgA);
            exchange.getIn().setBody(msgA);
        });
        Integer responseCode = ex.getMessage().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);

        assertNotEquals(200, (int) responseCode);
    }
}