package com.brice;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.net.NetSocket;
import org.vertx.java.platform.Verticle;

public class TelnetEchoer extends Verticle {

    public void start() {
        vertx.createNetServer().connectHandler(new Handler<NetSocket>() {
            public void handle(final NetSocket socket) {
//                Pump.createPump(socket, socket).start();
                vertx.eventBus().registerHandler("telnet.echoer", new Handler<Message>() {
                    @Override
                    public void handle(Message event) {
                        container.logger().info("TelnetEchoer message : " + event);
                        socket.write(event.body().toString())
                                .write("\n")
                                ;

                    }
                });

            }
        }).listen(4321);
    }
}