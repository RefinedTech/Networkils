package me.ikevoodoo.networkils.sockets;

import me.ikevoodoo.networkils.sockets.client.ClientWrapper;
import me.ikevoodoo.networkils.sockets.server.ServerWrapper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;

public final class SocketCreator {

    private SocketCreator() {

    }

    public static ServerSocket createServer() throws IOException {
        return createServer(0);
    }

    public static ServerSocket createServer(int port) throws IOException {
        return createServer(port, 50);
    }

    public static ServerSocket createServer(int port, int backlog) throws IOException {
        return new ServerSocket(port, backlog);
    }

    public static ServerSocket createServerOrNull() {
        return createServerOrNull(0);
    }

    public static ServerSocket createServerOrNull(int port) {
        return createServerOrNull(port, 50);
    }

    public static ServerSocket createServerOrNull(int port, int backlog) {
        try {
            return createServer(port, backlog);
        } catch (IOException e) {
            return null;
        }
    }

    public static Optional<ServerSocket> createServerOptional() {
        return createServerOptional(0);
    }

    public static Optional<ServerSocket> createServerOptional(int port) {
        return createServerOptional(port, 50);
    }

    public static Optional<ServerSocket> createServerOptional(int port, int backlog) {
        try {
            return Optional.of(createServer(port, backlog));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public static ServerWrapper createServerWrapper() throws IOException {
        return createServerWrapper(0);
    }

    public static ServerWrapper createServerWrapper(int port) throws IOException {
        return createServerWrapper(port, 50);
    }

    public static ServerWrapper createServerWrapper(int port, int backlog) throws IOException {
        return new ServerWrapper(createServer(port, backlog));
    }

    public static ServerWrapper createServerWrapperOrNull() {
        return createServerWrapperOrNull(0);
    }

    public static ServerWrapper createServerWrapperOrNull(int port) {
        return createServerWrapperOrNull(port, 50);
    }

    public static ServerWrapper createServerWrapperOrNull(int port, int backlog) {
        try {
            return createServerWrapper(port, backlog);
        } catch (IOException e) {
            return null;
        }
    }

    public static Optional<ServerWrapper> createServerWrapperOptional() {
        return createServerWrapperOptional(0);
    }

    public static Optional<ServerWrapper> createServerWrapperOptional(int port) {
        return createServerWrapperOptional(port, 50);
    }

    public static Optional<ServerWrapper> createServerWrapperOptional(int port, int backlog) {
        try {
            return Optional.of(createServerWrapper(port, backlog));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public static Socket createClient() throws IOException {
        return createClient("localhost");
    }

    public static Socket createClient(String host) throws IOException {
        return createClient(host, 0);
    }

    public static Socket createClient(int port) throws IOException {
        return createClient("localhost", port);
    }

    public static Socket createClient(String host, int port) throws IOException {
        return new Socket(host, port);
    }

    public static Socket createClientOrNull() {
        return createClientOrNull("localhost");
    }

    public static Socket createClientOrNull(String host) {
        return createClientOrNull(host, 0);
    }

    public static Socket createClientOrNull(int port) {
        return createClientOrNull("localhost", port);
    }

    public static Socket createClientOrNull(String host, int port) {
        try {
            return createClient(host, port);
        } catch (IOException e) {
            return null;
        }
    }

    public static Optional<Socket> createClientOptional() {
        return createClientOptional("localhost");
    }

    public static Optional<Socket> createClientOptional(String host) {
        return createClientOptional(host, 0);
    }

    public static Optional<Socket> createClientOptional(int port) {
        return createClientOptional("localhost", port);
    }

    public static Optional<Socket> createClientOptional(String host, int port) {
        try {
            return Optional.of(createClient(host, port));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public static ClientWrapper createClientWrapper() throws IOException {
        return createClientWrapper("localhost", 0);
    }

    public static ClientWrapper createClientWrapper(String host) throws IOException {
        return createClientWrapper(host, 0);
    }

    public static ClientWrapper createClientWrapper(int port) throws IOException {
        return createClientWrapper("localhost", port);
    }

    public static ClientWrapper createClientWrapper(String host, int port) throws IOException {
        return new ClientWrapper(createClient(host, port));
    }

    public static ClientWrapper createClientWrapperOrNull() {
        return createClientWrapperOrNull("localhost");
    }

    public static ClientWrapper createClientWrapperOrNull(String host) {
        return createClientWrapperOrNull(host, 0);
    }

    public static ClientWrapper createClientWrapperOrNull(int port) {
        return createClientWrapperOrNull("localhost", port);
    }

    public static ClientWrapper createClientWrapperOrNull(String host, int port) {
        try {
            return createClientWrapper(host, port);
        } catch (IOException e) {
            return null;
        }
    }

    public static Optional<ClientWrapper> createClientWrapperOptional() {
        return createClientWrapperOptional("localhost");
    }

    public static Optional<ClientWrapper> createClientWrapperOptional(String host) {
        return createClientWrapperOptional(host, 0);
    }

    public static Optional<ClientWrapper> createClientWrapperOptional(int port) {
        return createClientWrapperOptional("localhost", port);
    }

    public static Optional<ClientWrapper> createClientWrapperOptional(String host, int port) {
        try {
            return Optional.of(createClientWrapper(host, port));
        } catch (IOException e) {
            return Optional.empty();
        }
    }



}
