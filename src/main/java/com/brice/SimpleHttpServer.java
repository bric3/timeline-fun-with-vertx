package com.brice;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

public class SimpleHttpServer extends Verticle {

    @Override
    public void start() {
        vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
            @Override
            public void handle(HttpServerRequest event) {
                if (event.path().equals("/") && event.method().equals("GET")) {
                    Yay message = new Yay("Yay! '" + event.remoteAddress() + "'");
                    JsonObject yayJson = new JsonObject().putValue("yay", message.toString());
                    container.logger().info(yayJson);
                    vertx.eventBus().publish("telnet.echoer", yayJson);
                    event.response()
                            .setChunked(true)
                            .write("dude!", "UTF-16")
                            .end();
                }
            }
        }).listen(1234);
    }
}
