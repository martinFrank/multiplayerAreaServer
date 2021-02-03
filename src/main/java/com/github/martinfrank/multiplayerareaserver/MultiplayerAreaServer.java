package com.github.martinfrank.multiplayerareaserver;

import com.github.martinfrank.multiplayerareaserver.client.MultiPlayerMetaClient;
import com.github.martinfrank.multiplayerareaserver.model.MapProvider;
import com.github.martinfrank.multiplayerareaserver.model.AreaModel;
import com.github.martinfrank.multiplayerareaserver.server.NonBlockingServer;
import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiplayerAreaServer {

    public static final Logger LOGGER = LoggerFactory.getLogger(MultiplayerAreaServer.class);

    private final MultiplayerAreaServerTicker multiplayerAreaServerTicker;
    private final NonBlockingServer nonBlockingServer;

    public MultiplayerAreaServer() throws Exception {
        ServerConfig serverConfig = ConfigFactory.create(ServerConfig.class);

        String metaServerIp = serverConfig.metaServerAddress();
        int metaServerPort = serverConfig.metaServerPort();
        MultiPlayerMetaClient metaClient = new MultiPlayerMetaClient(metaServerIp, metaServerPort);

        MapProvider mapProvider = new MapProvider(serverConfig, metaClient);
        AreaModel areaModel = new AreaModel(mapProvider.getAreaMap());

        nonBlockingServer = new NonBlockingServer(10523); //FIXME magic Number!
        multiplayerAreaServerTicker = new MultiplayerAreaServerTicker(250, serverConfig, areaModel, metaClient); //FIXME magic Number!

        nonBlockingServer.setMessageQueue(multiplayerAreaServerTicker);
        multiplayerAreaServerTicker.setBroadcastServer(nonBlockingServer);
    }

    public static void main(String[] args) throws Exception {
        MultiplayerAreaServer multiplayerAreaServer = new MultiplayerAreaServer();
        multiplayerAreaServer.start();
    }

    private void start() {
        new Thread(multiplayerAreaServerTicker).start();
        new Thread(nonBlockingServer).start();
    }


}
