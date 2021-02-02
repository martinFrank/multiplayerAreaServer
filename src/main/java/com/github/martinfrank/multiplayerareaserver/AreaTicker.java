package com.github.martinfrank.multiplayerareaserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.martinfrank.multiplayerareaserver.client.MultiPlayerMetaClient;
import com.github.martinfrank.multiplayerareaserver.model.AreaModel;
import com.github.martinfrank.multiplayerareaserver.server.BroadcastServer;
import com.github.martinfrank.multiplayerprotocol.area.MapChanges;
import com.github.martinfrank.multiplayerprotocol.area.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AreaTicker implements Runnable {

    private final int delayInMillis;
    private final AreaModel areaModel;
    private BroadcastServer broadcastServer;
    private final List<String> internalQueue;
    private final List<MapChanges> mapChanges;
    private final List<String> inputBuffer;

    private static final Logger LOGGER = LoggerFactory.getLogger(AreaTicker.class);


    public AreaTicker(int delayInMillis, AreaModel areaModel) {
        this.delayInMillis = delayInMillis;
        this.areaModel = areaModel;

        internalQueue = new ArrayList<>();
        mapChanges = new ArrayList<>();
        inputBuffer = new ArrayList<>();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(delayInMillis);
                processInputs();
                tickMap();
            } catch (InterruptedException e) {
                //FIXME
                e.printStackTrace();
                break;
            }
        }
    }

    private void processInputs() {
        inputBuffer.clear();
        inputBuffer.addAll(internalQueue);
        inputBuffer.forEach(this::processInput);
        internalQueue.removeAll(inputBuffer);
    }

    private void processInput(String input) {
    }

    private void tickMap() {
        MapChanges mapChanges = areaModel.tick();


        if (mapChanges.hasChanges()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                Message message = new Message();
                message.className = MapChanges.class.getName();
                String mapChangesAsJson = mapper.writeValueAsString(mapChanges);
                LOGGER.debug("mapChangesAsJson {}",mapChangesAsJson);
                message.jsonContent = mapper.writeValueAsString(mapChanges);
                String messageJson = mapper.writeValueAsString(message);
                LOGGER.debug("!messageJson {}",messageJson);
                broadcastServer.broadcast(messageJson);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

    }

    public void setBroadcastServer(BroadcastServer broadcastServer) {
        this.broadcastServer = broadcastServer;
    }
}
