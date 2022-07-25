package dev.refinedtech.networkils.examples.ping;

import dev.refinedtech.networkils.sockets.builder.server.ServerSocketBuilder;
import dev.refinedtech.networkils.sockets.messaging.WritableMessage;

public class PingServer {

    public static void main(String[] args) {
        ServerSocketBuilder.create()
                .port(args.length < 2 ? 80 : Integer.parseInt(args[1]))
                .backlog(50)
                .wrapper()
                .buildOptional()
                .ifPresent(wrapper -> {
                    WritableMessage message = new WritableMessage(0xE91C);
                    wrapper.onConnection((server, client) -> {
                        long time = client.nextMessageOrNull().nextLong();
                        long ping = System.currentTimeMillis() - time;
                        System.out.println("Ping! From client " + client.getClientID() + ": " + ping + "ms");
                        client.sendMessage(message.writeString("Pong! Time taken: " + ping + "ms"));
                        message.clear();
                        client.disconnect();
                    }).start();
                });
    }

}
