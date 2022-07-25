package dev.refinedtech.networkils.sockets.server;

public interface ServerDisconnectListener {

    public void onDisconnect(ServerWrapper server, ServerConnection client);

}
