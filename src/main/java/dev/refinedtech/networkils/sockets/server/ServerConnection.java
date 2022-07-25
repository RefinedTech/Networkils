package dev.refinedtech.networkils.sockets.server;

import dev.refinedtech.networkils.sockets.messaging.ReadableMessage;
import dev.refinedtech.networkils.sockets.messaging.WritableMessage;
import dev.refinedtech.networkils.sockets.threading.ReadThread;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class ServerConnection {

    private final Socket socket;
    private final int clientID;

    private final Deque<CompletableFuture<ReadableMessage>> receiveQueue;

    private boolean closed;

    private Runnable onClose;


    protected ServerConnection(Socket socket, int id) {
        this.socket = socket;
        this.clientID = id;
        this.receiveQueue = new ArrayDeque<>();
    }

    public void disconnect() {
        try {
            socket.close();
            closed = true;
            if(onClose != null) {
                onClose.run();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CompletableFuture<ReadableMessage> nextMessageFuture() {
        CompletableFuture<ReadableMessage> future = new CompletableFuture<>();
        receiveQueue.add(future);
        return future;
    }

    public ReadableMessage nextMessage() throws ExecutionException, InterruptedException {
        return nextMessageFuture().get();
    }

    public ReadableMessage nextMessageOrNull() {
        try {
            return nextMessage();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void sendMessage(WritableMessage message) {
        try {
            message.writeTo(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isClosed() {
        return socket.isClosed() || closed;
    }

    public int getClientID() {
        return clientID;
    }

    protected void onMessage(Consumer<ReadableMessage> onMessage) throws IOException {
        // The thread will be interrupted if the connection is closed
        new ReadThread(this.socket.getInputStream()).onMessage(msg -> {
            if(receiveQueue.isEmpty()) {
                onMessage.accept(msg);
            } else {
                receiveQueue.poll().complete(msg);
            }
        }).onClose(this::disconnect).start();
    }

    protected void onClose(Runnable onClose) {
        this.onClose = onClose;
    }
}
