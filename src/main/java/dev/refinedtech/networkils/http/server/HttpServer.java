package dev.refinedtech.networkils.http.server;

import dev.refinedtech.networkils.http.threading.HttpReadThread;
import dev.refinedtech.networkils.sockets.builder.server.ServerSocketBuilder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer extends Thread {

    private final ServerSocket server;

    public HttpServer(ServerSocket server) {
        this.server = server;
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
                HttpReadThread thread = new HttpReadThread(socket.getInputStream());
                thread.start();
                thread.onMessage(message -> {
                    System.out.println(message.method());
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Test
    public static void main(String[] args) {
        new HttpServer(ServerSocketBuilder.create().port(80).backlog(50).buildNullable()).start();
    }

}
