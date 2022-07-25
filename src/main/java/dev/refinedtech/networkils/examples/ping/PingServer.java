package dev.refinedtech.networkils.examples.ping;

import dev.refinedtech.networkils.sockets.SocketCreator;
import dev.refinedtech.networkils.sockets.messaging.WritableMessage;

public class PingServer {

    public static void main(String[] args) {
        SocketCreator.createServerWrapperOptional(args.length == 0 ? 80 : Integer.parseInt(args[0])).ifPresent(wrapper -> {
            WritableMessage message = new WritableMessage(0xE91C);
            wrapper.onConnection((server, client) -> {
                long time = client.nextMessageOrNull().nextLong();
                long ping = System.currentTimeMillis() - time;
                System.out.println("Ping! From client " + client.getClientID() + ": " + ping);
                client.sendMessage(message.writeString("Pong! Time taken: " + ping + "ms"));
                message.clear();
                client.disconnect();
            }).start();
        });
    }

}
