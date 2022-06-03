package me.ikevoodoo.networkils.sockets.server;

import me.ikevoodoo.networkils.sockets.messaging.ReadableMessage;

public abstract class ServerMessageListener {

    private final int magicNumber;

    /**
     * Create a ServerMessageListener which listens for messages on a specific magic number.
     * <br>
     * If a packet begins with the magic number, it will be passed to the onMessage method.
     * */
    public ServerMessageListener(int magicNumber) {
        this.magicNumber = magicNumber;
    }

    /**
     * Process a message coming from the client.
     *
     * @see ReadableMessage
     * @param message The message to be read. This is a clone of the original message.
     * */
    abstract public void onMessage(ServerWrapper server, ServerConnection client, ReadableMessage message);

    public final int getMagicNumber() {
        return magicNumber;
    }

}
