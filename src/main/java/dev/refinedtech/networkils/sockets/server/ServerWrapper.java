package dev.refinedtech.networkils.sockets.server;

import dev.refinedtech.networkils.sockets.messaging.WritableMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ServerWrapper extends Thread {

    private final ServerSocket server;

    private final HashMap<Integer, List<ServerMessageListener>> messageListeners;

    private final List<ServerConnectionListener> connectionListeners;
    private final List<ServerDisconnectListener> disconnectListeners;

    private final List<ServerConnection> connections;

    private final boolean errorMode;

    private int highestId = 0;

    public ServerWrapper(ServerSocket server) {
        this(server, false);
    }

    public ServerWrapper(ServerSocket server, boolean errorMode) {
        this.server = server;
        this.messageListeners = new HashMap<>();
        this.connectionListeners = new ArrayList<>();
        this.disconnectListeners = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.errorMode = errorMode;
    }

    public ServerWrapper onMessage(ServerMessageListener listener) {
        int magicNumber = listener.getMagicNumber();
        if (!messageListeners.containsKey(magicNumber)) {
            List<ServerMessageListener> list = new ArrayList<>();
            list.add(listener);
            messageListeners.put(magicNumber, list);
        } else {
            messageListeners.get(magicNumber).add(listener);
        }
        return this;
    }

    public ServerWrapper onConnection(ServerConnectionListener listener) {
        connectionListeners.add(listener);
        return this;
    }

    public ServerWrapper onDisconnect(ServerDisconnectListener listener) {
        disconnectListeners.add(listener);
        return this;
    }

    public ServerWrapper broadcast(WritableMessage message) {
        for (ServerConnection connection : connections) {
            connection.sendMessage(message.copy());
        }
        return this;
    }

    public ServerWrapper broadcastExcept(WritableMessage message, ServerConnection... excludes) {
        List<ServerConnection> excludeList = Arrays.asList(excludes);
        for (ServerConnection connection : connections) {
            if(excludeList.contains(connection)) {
                continue;
            }
            connection.sendMessage(message.copy());
        }
        return this;
    }

    public ServerWrapper broadcast(WritableMessage message, ServerConnection... connections) {
        for (ServerConnection connection : connections) {
            connection.sendMessage(message.copy());
        }
        return this;
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        interrupt();
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                Socket socket = this.server.accept();
                ServerConnection connection = new ServerConnection(socket, highestId++);
                connection.onMessage(message -> {
                    if (messageListeners.containsKey(message.getMagicNumber())) {
                        messageListeners.get(message.getMagicNumber()).forEach(listener -> listener.onMessage(this, connection, message.copy()));
                    }
                });
                connection.onClose(() -> {
                    connections.remove(connection);
                    disconnectListeners.forEach(listener -> listener.onDisconnect(this, connection));
                });
                connectionListeners.forEach(listener -> listener.onConnect(this, connection));
                connections.add(connection);
            } catch (IOException e) {
                if(this.errorMode) {
                    System.err.println("Failed to accept connection");
                    e.printStackTrace();
                }
            }
        }
    }
}
