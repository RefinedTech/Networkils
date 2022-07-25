package dev.refinedtech.networkils.sockets.builder.client;

import dev.refinedtech.networkils.sockets.client.ClientWrapper;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

public class ClientWrapperBuilder {


    private int port;
    private String host;

    private ClientWrapperBuilder() {

    }

    public static ClientWrapperBuilder create() {
        return new ClientWrapperBuilder();
    }

    public ClientWrapperBuilder port(int port) {
        this.port = Math.max(port, 0);
        return this;
    }

    public ClientWrapperBuilder host(String host) {
        this.host = host;
        return this;
    }

    public ClientSocketBuilder server() {
        return ClientSocketBuilder.create()
                .port(this.port)
                .host(this.host);
    }

    public ClientWrapper build() throws IOException {
        return new ClientWrapper(new Socket(this.host, this.port));
    }

    public Optional<ClientWrapper> buildOptional() {
        try {
            return Optional.of(build());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public ClientWrapper buildNullable() {
        try {
            return build();
        } catch (IOException e) {
            return null;
        }
    }


}
