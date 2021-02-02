package com.github.martinfrank.multiplayerareaserver.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.martinfrank.multiplayerprotocol.meta.PlayerMetaData;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class MultiPlayerMetaClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiPlayerMetaClient.class);
    private final String server;
    private final int port;


    public MultiPlayerMetaClient(String server, int port) {
        this.server = server;
        this.port = port;
    }


    public File downloadMapPack(String mapId, String file) {
        return Unirest.get("http://" + server + ":" + port + "/mapdata/download")
                .header("accept", "application/octet-stream")
                .queryString("mapid", mapId)
                .asFile(file).getBody();
    }

}
