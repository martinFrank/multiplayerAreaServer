package com.github.martinfrank.multiplayerareaserver.map;

import com.github.martinfrank.multiplayerareaserver.ServerConfig;
import com.github.martinfrank.multiplayerareaserver.client.MultiPlayerMetaClient;
import com.github.martinfrank.multiplayerareaserver.io.FileUtils;
import com.github.martinfrank.multiplayerareaserver.model.AreaMap;
import org.mapeditor.core.Map;
import org.mapeditor.io.TMXMapReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class MapProvider {

    private final Map map;

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiPlayerMetaClient.class);
    private static final String MAP = "map.tmx";

    public MapProvider(ServerConfig serverConfig) throws Exception {
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
}
