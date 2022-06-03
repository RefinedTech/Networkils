package me.ikevoodoo.networkils.sockets.server;

public interface ServerDisconnectListener {

    public void onDisconnect(ServerWrapper server, ServerConnection client);

}
