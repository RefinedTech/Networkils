package me.ikevoodoo.networkils.examples.ping;

import me.ikevoodoo.networkils.sockets.SocketCreator;
import me.ikevoodoo.networkils.sockets.client.ClientMessageListener;
import me.ikevoodoo.networkils.sockets.client.ClientWrapper;
import me.ikevoodoo.networkils.sockets.messaging.ReadableMessage;
import me.ikevoodoo.networkils.sockets.messaging.WritableMessage;

import java.io.IOException;

public class PingClient {

    public static void main(String[] args) {
        SocketCreator.createClientWrapperOptional(args.length < 1 ? "localhost" : args[0], args.length < 2 ? 80 : Integer.parseInt(args[1])).ifPresent(wrapper -> {
            WritableMessage message = new WritableMessage(0xE91C);
            wrapper.onConnect(client -> {
                client.sendMessageSilently(message.writeLong(System.currentTimeMillis()));
                message.clear();
            }).onMessage(new ClientMessageListener(0xE91C) {
                @Override
                public void onMessage(ClientWrapper client, ReadableMessage message) throws IOException {
                    System.out.println("Server: " + message.nextString());
                }
            }).start();
        });
    }

}
