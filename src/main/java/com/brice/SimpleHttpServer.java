package com.brice;

import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.platform.Verticle;
import io.vertx.rxcore.java.RxVertx;
import io.vertx.rxcore.java.eventbus.RxMessage;
import io.vertx.rxcore.java.http.RxHttpServer;
import io.vertx.rxcore.java.http.RxHttpServerRequest;
import io.vertx.rxcore.java.http.RxServerWebSocket;
import rx.util.functions.Action1;

public class SimpleHttpServer extends Verticle {

    /* Notes
     *
     * mod-RxVertx : - Action1 ...duh
     *               - .http() ...observable methods?
     *               - not fluent enough, would be nice to add a .listen
     *               - listen sur netServer()
     */

    @Override
    public void start() {
        container.logger().info("[SimpleHttpServer] starting");
        container.logger().info("[SimpleHttpServer] Got web.root config: " + webRoot());
        RxHttpServer httpServer = new RxVertx(vertx).createHttpServer();
        httpServer.http()
                .subscribe(
                        new Action1<RxHttpServerRequest>() {
                            @Override
                            public void call(RxHttpServerRequest req) {
                                container.logger().info("got http request: " + req.method() + " " + req.path());

                                req.response().headers().set("Content-Type", "text/html; charset=UTF-8");

                                if (req.path().equals("/")) req.response().sendFile(fromWebRoot("ws.html")); // Serve the html
                                else req.response().setStatusCode(404);
                            }
                        }
                );

        httpServer.websocket()
                .subscribe(new Action1<RxServerWebSocket>() {
                    @Override
                    public void call(final RxServerWebSocket ws) {
                        container.logger().info("[WebSocketServer] opened: " + ws.path());
                        if(ws.path().endsWith("ws")) {
                            ws.asObservable().subscribe(
                                    new Action1<Buffer>() {
                                        @Override
                                        public void call(Buffer buffer) {
                                            container.logger().info("[WebSocketServer] received: '" + buffer + "' on WS " + ws.binaryHandlerID());
                                            new RxVertx(vertx).eventBus().send("telnet.endpoint", buffer);
                                        }
                                    }
//                                    new Action1<Buffer>() {
//                                        @Override
//                                        public void call(Buffer buffer) {
//                                            container.logger().info("WebSocketServer:received[" + buffer + "]");
//                                            ws.writeTextFrame(new Date().toString());
//                                        }
//                                    }
                            );
                            new RxVertx(vertx).eventBus().registerHandler("ws.endpoint").subscribe(new Action1<RxMessage<Object>>() {
                                @Override
                                public void call(RxMessage<Object> objectRxMessage) {
                                    Message<Object> objectMessage = objectRxMessage.coreMessage();
                                    container.logger().info("[WSEndpoint] writing: '" + objectMessage.body().toString().trim() + "' on WS " + ws.binaryHandlerID());
                                    ws.writeTextFrame(objectMessage.body().toString());
                                }
                            });
                        } else {
                            ws.reject();
                        }
                    }
                });

        httpServer.coreHttpServer()
                .setCompressionSupported(true)
                .listen(1234);


//        vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
//            public void handle(HttpServerRequest req) {
//                if (req.path().equals("/")) {
//                    vertx.fileSystem().readDir(".", new Handler<AsyncResult<String[]>>() {
//                        @Override
//                        public void handle(AsyncResult<String[]> event) {
//                            container.logger().info("-> " + Arrays.toString(event.result()) );
//                        }
//                    });
//                    req.response().sendFile(webRoot() + "/ws.html");
//                } else {
//                    //Clearly in a real server you would check the path for better security!!
//                    req.response().sendFile(req.path());
//                }
//            }
//        }).listen(1234);

//        vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
//            @Override
//            public void handle(HttpServerRequest event) {
//                if (event.path().equals("/") && event.method().equals("GET")) {
//                    Yay message = new Yay("Yay! '" + event.remoteAddress() + "'");
//                    JsonObject yayJson = new JsonObject().putValue("yay", message.toString());
//                    container.logger().info(yayJson);
//                    vertx.eventBus().publish("telnet.echoer", yayJson);
//                    event.response()
//                            .setChunked(true)
//                            .write("dude!", "UTF-16")
//                            .end();
//                }
//            }
//        }).listen(1234);
    }

    private String fromWebRoot(String pathString) {
        return webRoot() + "/" + pathString;
    }

    private String webRoot() {
        return container.config().getString("web.root.directory");
    }
}
