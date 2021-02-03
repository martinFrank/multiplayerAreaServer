package com.github.martinfrank.multiplayerareaserver.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.martinfrank.multiplayerprotocol.meta.AreaServerCredentials;
import com.github.martinfrank.multiplayerprotocol.meta.PlayerData;
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

    public PlayerData getPlayerData(AreaServerCredentials areaServerCredentials) {
        try {
            HttpResponse<JsonNode> response = Unirest.post("http://" + server + ":" + port + "/playerdata/player")
                    .header("accept", "application/json")
                    .header("content-type", "application/json")
                    .body(areaServerCredentials)
                    .asJson();

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.getBody().toString(), PlayerData.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            //FIXME
        }
        return null;
    }
}
