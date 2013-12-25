package com.brice;

import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.platform.Verticle;
import io.vertx.rxcore.java.RxVertx;
import io.vertx.rxcore.java.eventbus.RxMessage;
import io.vertx.rxcore.java.net.RxNetServer;
import io.vertx.rxcore.java.net.RxNetSocket;
import rx.util.functions.Action1;

public class TelnetEchoer extends Verticle {

    public void start() {
        RxNetServer netServer = new RxVertx(vertx).createNetServer();

        netServer.connectStream().subscribe(new Action1<RxNetSocket>() {
            @Override
            public void call(final RxNetSocket rxNetSocket) {
                new RxVertx(vertx).eventBus().registerHandler("telnet.endpoint").subscribe(new Action1<RxMessage<Object>>() {
                    @Override
                    public void call(RxMessage<Object> objectRxMessage) {
                        Message<Object> objectMessage = objectRxMessage.coreMessage();
                        container.logger().info("[TelnetEndpoint] received: " + objectMessage.body().toString());
                        rxNetSocket.coreSocket().write(objectMessage.body().toString().trim()).write("\n");
                    }
                });

                rxNetSocket.dataStream().subscribe(new Action1<Buffer>() {
                    @Override
                    public void call(Buffer buffer) {
                        new RxVertx(vertx).eventBus().send("ws.endpoint", buffer);
//                        rxNetSocket.coreSocket().write(buffer + "!").write("\n");
                    }
                });
            }
        });

        netServer.listen(4321);

//        vertx.createNetServer().connectHandler(new Handler<NetSocket>() {
//            public void handle(final NetSocket socket) {
////                Pump.createPump(socket, socket).start();
//                vertx.eventBus().registerHandler("telnet.echoer", new Handler<Message>() {
//                    @Override
//                    public void handle(Message event) {
//                        container.logger().info("TelnetEchoer message : " + event);
//                        socket
//                                .write(event.body().toString())
//                                .write("\n")
//                        ;
//
//                    }
//                });
//
//            }
//        }).listen(4321);
    }
}