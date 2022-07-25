package dev.refinedtech.networkils.examples.ping;

import dev.refinedtech.networkils.sockets.builder.client.ClientSocketBuilder;
import dev.refinedtech.networkils.sockets.client.ClientMessageListener;
import dev.refinedtech.networkils.sockets.client.ClientWrapper;
import dev.refinedtech.networkils.sockets.messaging.ReadableMessage;
import dev.refinedtech.networkils.sockets.messaging.WritableMessage;

import java.io.IOException;

public class PingClient {

    public static void main(String[] args) {
        ClientSocketBuilder.create()
                .port(args.length < 2 ? 80 : Integer.parseInt(args[1]))
                .host("localhost")
                .wrapper()
                .buildOptional()
                .ifPresent(wrapper -> {
                    WritableMessage message = new WritableMessage(0xE91C);
                    wrapper.onConnect(client -> {
                        client.sendMessageSilently(message.writeLong(System.currentTimeMillis()));
                        message.clear();
                    }).onMessage(new ClientMessageListener(0xE91C) {
                        @Override
                        public void onMessage(ClientWrapper client, ReadableMessage message) {
                            System.out.println("Server: " + message.nextString());
                        }
                    }).start();
                });
    }

}
