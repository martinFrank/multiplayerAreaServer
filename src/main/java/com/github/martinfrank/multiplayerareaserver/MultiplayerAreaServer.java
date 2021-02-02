package com.github.martinfrank.multiplayerareaserver;

import com.github.martinfrank.multiplayerareaserver.client.MultiPlayerMetaClient;
import com.github.martinfrank.multiplayerareaserver.io.FileUtils;
import com.github.martinfrank.multiplayerareaserver.map.MapProvider;
import com.github.martinfrank.multiplayerareaserver.model.AreaModel;
import com.github.martinfrank.multiplayerareaserver.server.NonBlockingServer;
import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

public class MultiplayerAreaServer {

    public static final Logger LOGGER = LoggerFactory.getLogger(MultiplayerAreaServer.class);

    private final AreaTicker areaTicker;
    private final AreaModel areaModel;

    private final NonBlockingServer nonBlockingServer;

    public MultiplayerAreaServer() throws Exception {
        ServerConfig serverConfig = ConfigFactory.create(ServerConfig.class);
        LOGGER.debug("download map");
        downloadMap(serverConfig);


        MapProvider mapProvider = new MapProvider(serverConfig);
        areaModel = new AreaModel(mapProvider.getAreaMap()); //FIXME areaspecific parameters in constructor (from this constructors paramaters)

        nonBlockingServer = new NonBlockingServer(10523); //FIXME magic Number!
        areaTicker = new AreaTicker(250, areaModel); //FIXME magic Number!

        nonBlockingServer.setMessageQueue(areaModel);
        areaTicker.setBroadcastServer(nonBlockingServer);


        new Thread(areaTicker).start();
        new Thread(nonBlockingServer).start();
    }

    private void downloadMap(ServerConfig serverConfig) {
        try{
            String metaServerIp = serverConfig.metaServerAddress();
            int metaServerPort = serverConfig.metaServerPort();
            MultiPlayerMetaClient metaClient = new MultiPlayerMetaClient(metaServerIp, metaServerPort);

            String jarDirectory = FileUtils.getJarDirectory();
            String mapDir = FileUtils.createDirectory(serverConfig.areaDownloadDir());
            String downloadFilename = FileUtils.reCreateFile(mapDir, serverConfig.areaDownloadFile());

            String mapId = serverConfig.areaMapId();
            File zipFile = metaClient.downloadMapPack(mapId, downloadFilename);
            LOGGER.debug("ZipFile: {}", zipFile);
            UnzipUtility.unzip(zipFile, jarDirectory+"/maps/"+mapId);
        }catch(IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {

        new MultiplayerAreaServer();
    }


}
