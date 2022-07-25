package dev.refinedtech.networkils.sockets.builder.client;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

public class ClientSocketBuilder {

    private int port;
    private String host = "localhost";

    private ClientSocketBuilder() {

    }

    public static ClientSocketBuilder create() {
        return new ClientSocketBuilder();
    }

    public ClientSocketBuilder port(int port) {
        this.port = Math.max(port, 0);
        return this;
    }

    public ClientSocketBuilder host(String host) {
        this.host = host == null || host.trim().isEmpty() ? "localhost" : host;
        return this;
    }

    public ClientWrapperBuilder wrapper() {
        return ClientWrapperBuilder.create()
                .port(this.port)
                .host(this.host);
    }

    public Socket build() throws IOException {
        return new Socket(this.host, this.port);
    }

    public Optional<Socket> buildOptional() {
        try {
            return Optional.of(build());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public Socket buildNullable() {
        try {
            return build();
        } catch (IOException e) {
            return null;
        }
    }

}
