package dev.refinedtech.networkils.sockets.builder.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Optional;

public class ServerSocketBuilder {

    private int port;
    private int backlog;

    private ServerSocketBuilder() {

    }

    public static ServerSocketBuilder create() {
        return new ServerSocketBuilder();
    }

    public ServerSocketBuilder port(int port) {
        this.port = Math.max(port, 0);
        return this;
    }

    public ServerSocketBuilder backlog(int backlog) {
        this.backlog = backlog;
        return this;
    }

    public ServerWrapperBuilder wrapper() {
        return ServerWrapperBuilder.create()
                .port(this.port)
                .backlog(this.backlog);
    }

    public ServerSocket build() throws IOException {
        return new ServerSocket(this.port, this.backlog);
    }

    public Optional<ServerSocket> buildOptional() {
        try {
            return Optional.of(build());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public ServerSocket buildNullable() {
        try {
            return build();
        } catch (IOException e) {
            return null;
        }
    }

}
