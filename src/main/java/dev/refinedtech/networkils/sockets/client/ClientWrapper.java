package dev.refinedtech.networkils.sockets.client;

import dev.refinedtech.networkils.sockets.messaging.ReadableMessage;
import dev.refinedtech.networkils.sockets.messaging.WritableMessage;
import dev.refinedtech.networkils.sockets.threading.ReadThread;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientWrapper extends Thread {
    private final HashMap<Integer, List<ClientMessageListener>> messageListeners;
    private final List<ClientConnectListener> connectListeners;
    private final List<ClientDisconnectListener> disconnectListeners;

    private Socket socket;

    private ReadThread readThread;


    public ClientWrapper(Socket socket) {
        this.socket = socket;
        this.messageListeners = new HashMap<>();
        this.connectListeners = new ArrayList<>();
        this.disconnectListeners = new ArrayList<>();
    }

    public ClientWrapper onMessage(ClientMessageListener listener) {
        int magicNumber = listener.getMagicNumber();
        if (!messageListeners.containsKey(magicNumber)) {
            List<ClientMessageListener> list = new ArrayList<>();
            list.add(listener);
            messageListeners.put(magicNumber, list);
        } else {
            messageListeners.get(magicNumber).add(listener);
        }
        return this;
    }

    public ClientWrapper onConnect(ClientConnectListener listener) {
        connectListeners.add(listener);
        return this;
    }

    public ClientWrapper onDisconnect(ClientDisconnectListener listener) {
        disconnectListeners.add(listener);
        return this;
    }

    public ClientWrapper sendMessage(WritableMessage message) throws IOException {
        message.writeTo(socket.getOutputStream());
        return this;
    }

    public ClientWrapper sendMessageSilently(WritableMessage message) {
        try {
            this.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public ClientWrapper call(ReadableMessage message) throws IOException {
        int magicNumber = message.getMagicNumber();
        if (messageListeners.containsKey(magicNumber)) {
            for (ClientMessageListener listener : messageListeners.get(magicNumber)) {
                listener.onMessage(this, message);
            }
        }
        return this;
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            this.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ClientWrapper connectTo(String host, int port) throws IOException {
        this.disconnect();
        socket = new Socket(host, port);
        if(Thread.currentThread() == this) {
            this.run();
        } else {
            this.start();
        }
        return this;
    }

    public ClientWrapper disconnect() throws IOException {
        this.closeReadThread();
        socket.close();

        this.disconnectListeners.forEach(listener -> listener.onDisconnect(this));
        return this;
    }

    @Override
    public void run() {
        try {
            this.emitConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void emitConnection() throws IOException {
        this.createReadThread();
        this.connectListeners.forEach(listener -> listener.onConnect(this));
    }

    private void createReadThread() throws IOException {
        this.closeReadThread();
        this.readThread = new ReadThread(socket.getInputStream()).onMessage(message -> {
            if (messageListeners.containsKey(message.getMagicNumber())) {
                messageListeners.get(message.getMagicNumber()).forEach(listener -> {
                    try {
                        listener.onMessage(this, message.copy());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });
        this.readThread.start();
    }

    private void closeReadThread() {
        if(readThread != null) {
            readThread.interrupt();
        }
    }
}
