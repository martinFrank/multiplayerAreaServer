package com.github.martinfrank.multiplayerareaserver.server;

import com.github.martinfrank.multiplayerareaserver.MultiplayerAreaServerTicker;
import com.github.martinfrank.multiplayerareaserver.ServerConfig;
import com.github.martinfrank.multiplayerareaserver.client.MultiPlayerMetaClient;
import com.github.martinfrank.multiplayerareaserver.model.AreaModel;
import com.github.martinfrank.multiplayerprotocol.area.*;
import com.github.martinfrank.multiplayerprotocol.meta.AreaServerCredentials;
import com.github.martinfrank.multiplayerprotocol.meta.PlayerData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AreaMessageParser extends BaseMessageParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(AreaMessageParser.class);

    private final MultiPlayerMetaClient metaClient;

    private final AreaModel areaModel;
    private final MultiplayerAreaServerTicker ticker;
    private final ServerConfig serverConfig;
    private final AreaServerCredentials areaServerCredentials;

    public AreaMessageParser(MultiplayerAreaServerTicker ticker, AreaModel areaModel, ServerConfig serverConfig, MultiPlayerMetaClient metaClient) {
        this.areaModel = areaModel;
        this.ticker = ticker;
        this.serverConfig = serverConfig;
        this.metaClient = metaClient;

        //FIXME from config
        areaServerCredentials = new AreaServerCredentials();
        areaServerCredentials.areaId = "templeTest";
        areaServerCredentials.user = "templeTest1";
        areaServerCredentials.pass = "swordFish";
    }


    @Override
    public void handlePlayerMovementRequest(PlayerMovementRequest playerMovementRequest) {
        Player player = areaModel.getPlayers().getPlayer(playerMovementRequest.playerId);
        LOGGER.debug("player is present? player: {}", player);
        Direction direction = playerMovementRequest.direction;
        boolean canEnter = areaModel.getMap().canEnter(player, direction);
        LOGGER.debug("canEnter={}",canEnter);
        if (player != null && canEnter) {
            Position from = new Position(player.position);
            player.move(direction);
            Position to = new Position(player.position);
            PlayerMovement playerMovement = new PlayerMovement(player.id, from, to);
            ticker.addPlayerMovement(playerMovement);
        }
    }


    @Override
    public void handlePlayerRegistration(PlayerRegistration playerRegistration) {
        String id = playerRegistration.playerId;
        LOGGER.debug("handlePlayerRegistration {}", playerRegistration);
        LOGGER.debug("then i should start a backup thread that backups the players position all 20 seconds or so");
        AreaServerCredentials credentials = new AreaServerCredentials(areaServerCredentials, id);
        PlayerData playerData = metaClient.getPlayerData(credentials);
        LOGGER.debug("i did get Player data from a Player: {}", playerData);
        LOGGER.warn("PLAYER IS NOT VALIDATED YET - SIMPLY ADDED");
        ticker.addNewPlayer(playerData);
    }

}
