package com.github.martinfrank.multiplayerareaserver.server;

import java.nio.channels.SelectionKey;

public interface BroadcastServer {

    void broadcast(String message);

    void singlecast(String message, SelectionKey key);

}
