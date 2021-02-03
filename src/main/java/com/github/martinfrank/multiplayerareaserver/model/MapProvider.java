package com.github.martinfrank.multiplayerareaserver.model;

import com.github.martinfrank.multiplayerareaserver.ServerConfig;
import com.github.martinfrank.multiplayerareaserver.io.UnzipUtility;
import com.github.martinfrank.multiplayerareaserver.client.MultiPlayerMetaClient;
import com.github.martinfrank.multiplayerareaserver.io.FileUtils;
import org.mapeditor.core.Map;
import org.mapeditor.io.TMXMapReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class MapProvider {

    private final Map map;

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiPlayerMetaClient.class);
    private static final String MAP = "map.tmx";

    public MapProvider(ServerConfig serverConfig, MultiPlayerMetaClient metaClient) throws Exception {
        downloadMap(serverConfig, metaClient);

        String jarDirectory = FileUtils.getJarDirectory();
        TMXMapReader reader = new TMXMapReader();

        String mapName = jarDirectory+"/"
                +serverConfig.areaDownloadDir()+"/"
                +serverConfig.areaMapId()+"/"
                +MAP;
        LOGGER.debug("loading map: {}", mapName);
        map = reader.readMap(mapName);
    }

    public AreaMap getAreaMap() {
        return new AreaMap(map);
    }

    private void downloadMap(ServerConfig serverConfig, MultiPlayerMetaClient metaClient) {
        try{
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
}
