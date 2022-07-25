package dev.refinedtech.networkils.sockets.client;

import dev.refinedtech.networkils.sockets.messaging.ReadableMessage;

import java.io.IOException;

public abstract class ClientMessageListener {

    private final int magicNumber;

    /**
     * Create a ServerMessageListener which listens for messages on a specific magic number.
     * <br>
     * If a packet begins with the magic number, it will be passed to the onMessage method.
     * */
    public ClientMessageListener(int magicNumber) {
        this.magicNumber = magicNumber;
    }

    /**
     * Process a message coming from the server.
     *
     * @see ReadableMessage
     * @param message The message to be read. This is a clone of the original message.
     * */
    abstract public void onMessage(ClientWrapper wrapper,  ReadableMessage message) throws IOException;

    public final int getMagicNumber() {
        return magicNumber;
    }

}