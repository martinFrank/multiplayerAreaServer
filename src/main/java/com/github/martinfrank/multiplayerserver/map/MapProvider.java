package com.github.martinfrank.multiplayerserver.map;

import org.mapeditor.core.Map;
import org.mapeditor.io.TMXMapReader;

import java.io.File;

public class MapProvider {

    private final Map map;

    public MapProvider() throws Exception {
        //FIXME restAccess for map
        //FIXME - proper directory access
        String jarDirectory = new File(MapProvider.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath();
        TMXMapReader reader = new TMXMapReader();
        String mapName = jarDirectory + "/TempleTest/TempleTest.tmx";
        map = reader.readMap(mapName);
    }

    public Map getMap() {
        return map;
    }
}
