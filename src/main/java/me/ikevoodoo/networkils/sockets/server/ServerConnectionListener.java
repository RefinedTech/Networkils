package me.ikevoodoo.networkils.sockets.server;

public interface ServerConnectionListener {

    public void onConnect(ServerWrapper server, ServerConnection client);

}
