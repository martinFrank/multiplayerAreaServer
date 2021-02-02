package com.github.martinfrank.multiplayerareaserver.io;

import com.github.martinfrank.multiplayerareaserver.map.MapProvider;

import java.io.File;
import java.net.URISyntaxException;

public class FileUtils {

    public static String getJarDirectory() throws URISyntaxException {
        return new File(MapProvider.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                .getParentFile().getPath();
    }

    public static String createDirectory(String directory) throws URISyntaxException {
        String directoryName = getJarDirectory()+"/"+directory;
        File mapDir = new File(directoryName);
        if (!mapDir.exists()) {
            mapDir.mkdir();
        }
        return directoryName;
    }

    public static String reCreateFile(String dir, String fileName) {
        String fullFileName = dir+"/"+fileName;
        File f = new File(fullFileName);
        if (f.exists()){
            f.delete();
        }
        return fullFileName;
    }

}
