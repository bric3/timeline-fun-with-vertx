package com.brice;

import org.vertx.java.platform.Verticle;

public class Server extends Verticle {

    public void start() {
        container.deployVerticle("com.brice.SimpleHttpServer");
        container.deployVerticle("com.brice.TelnetEchoer");
    }
}