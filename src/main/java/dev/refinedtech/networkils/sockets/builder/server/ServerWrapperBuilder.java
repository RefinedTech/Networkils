package dev.refinedtech.networkils.sockets.builder.server;

import dev.refinedtech.networkils.sockets.server.ServerWrapper;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Optional;

public class ServerWrapperBuilder {


    private int port;
    private int backlog;

    private ServerWrapperBuilder() {

    }

    public static ServerWrapperBuilder create() {
        return new ServerWrapperBuilder();
    }

    public ServerWrapperBuilder port(int port) {
        this.port = Math.max(port, 0);
        return this;
    }

    public ServerWrapperBuilder backlog(int backlog) {
        this.backlog = backlog;
        return this;
    }

    public ServerSocketBuilder server() {
        return ServerSocketBuilder.create()
                .port(this.port)
                .backlog(this.backlog);
    }

    public ServerWrapper build() throws IOException {
        return new ServerWrapper(new ServerSocket(this.port, this.backlog));
    }

    public Optional<ServerWrapper> buildOptional() {
        try {
            return Optional.of(build());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public ServerWrapper buildNullable() {
        try {
            return build();
        } catch (IOException e) {
            return null;
        }
    }


}
