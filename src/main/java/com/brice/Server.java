package com.brice;

import java.util.Date;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

public class Server extends Verticle {

    public void start() {
        JsonObject globalConfig = container.config();
        container.logger().info("[Server] Got config: " + container.config());

        container.deployVerticle("com.brice.SimpleHttpServer", globalConfig);
        container.deployVerticle("com.brice.TelnetEchoer");
        container.logger().info("[Server] Deployed modules!    " + new Date());
    }
}