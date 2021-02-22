package com.github.martinfrank.multiplayerareaserver;

import com.github.martinfrank.multiplayerareaserver.client.MultiPlayerMetaClient;
import com.github.martinfrank.multiplayerareaserver.model.AreaModel;
import com.github.martinfrank.multiplayerareaserver.server.AreaMessageParser;
import com.github.martinfrank.multiplayerareaserver.server.BroadcastServer;
import com.github.martinfrank.multiplayerareaserver.server.SelectionKeyId;
import com.github.martinfrank.multiplayerprotocol.area.*;
import com.github.martinfrank.multiplayerprotocol.meta.PlayerData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiplayerAreaServerTicker implements Runnable, MessageParser<SelectionKeyId> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiplayerAreaServerTicker.class);

    private final int delayInMillis;
    private final AreaModel areaModel;
    private BroadcastServer broadcastServer;
    private final List<Message<SelectionKeyId>> inputMessages;
    private final List<Message<SelectionKeyId>> messageBuffer;
    private final Map<SelectionKeyId, Player> players;

    private final AreaMessageParser areaMessageParser;

    public MultiplayerAreaServerTicker(int delayInMillis, ServerConfig serverConfig, AreaModel areaModel, MultiPlayerMetaClient metaClient) {
        this.delayInMillis = delayInMillis;
        this.areaModel = areaModel;

        inputMessages = new ArrayList<>();
        messageBuffer = new ArrayList<>();
        players = new HashMap<>();

        areaMessageParser = new AreaMessageParser(this, areaModel, serverConfig, metaClient);
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
        messageBuffer.clear();
        messageBuffer.addAll(inputMessages);
        messageBuffer.forEach(this::processInput);
        inputMessages.removeAll(messageBuffer);
    }

    private void processInput(Message<SelectionKeyId> input) {
        areaMessageParser.parse(input);
    }

    private void tickMap() {
        MapChanges mapChanges = areaModel.tick();
        if (mapChanges.hasChanges()) {
            String messageJson = MessageJsonFactory.create(mapChanges);
            broadcastServer.broadcast(messageJson);
            areaModel.clearMapChanges();
        }

    }

    public void setBroadcastServer(BroadcastServer broadcastServer) {
        this.broadcastServer = broadcastServer;
    }

    @Override
    public void parse(Message<SelectionKeyId> message) {
        inputMessages.add(message);
    }

    public void addPlayerMovement(PlayerMovement playerMovement) {
        areaModel.getMapChanges().playerMovements.add(playerMovement);
        LOGGER.debug("playerchanges inside? {}",areaModel.getMapChanges().playerMovements);
    }

    public AreaTotal getAreaTotal() {
        return areaModel.getAreaTotal();
    }

    public void addNewPlayer(PlayerData playerData, SelectionKeyId keyId) {
        Player player = new Player(playerData, true);
        areaModel.getPlayers().players.add(player);
        String messageJson = MessageJsonFactory.create(player);
        broadcastServer.broadcast(messageJson);
    }

    public void deregister(SelectionKeyId selectionKeyId) {
        LOGGER.info("handle registration connection from {}", selectionKeyId);
    }

}
