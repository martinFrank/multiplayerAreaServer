package com.github.martinfrank.multiplayerareaserver.model;

import com.github.martinfrank.multiplayerprotocol.area.*;
import com.github.martinfrank.multiplayerprotocol.area.MonsterMovement;
import com.github.martinfrank.multiplayerprotocol.area.MapChanges;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AreaModel extends BaseMessageParser {

    public static final Logger LOGGER = LoggerFactory.getLogger(AreaModel.class);

    private int increment = 0;

    private final Monsters monsters;
    private final Players players;

    private final AreaMap areaMap;

    public AreaModel(AreaMap areaMap) {
        this.areaMap = areaMap;
        LOGGER.debug("areaMap: {}", areaMap);

        monsters = new Monsters();
        players = new Players();

        String name = "wolf";
        Monster monster = new Monster(UUID.randomUUID().toString(), UUID.randomUUID().toString(), new Position(5,5));
        monsters.monsters.add(monster);
    }

    public MapChanges tick() {
        MapChanges mapChanges = new MapChanges();
        increment = increment + 1;
        if (increment == 8) {
            increment = 0;
            for (Monster monster : monsters.getAll()) {
                tickMonster(monster, mapChanges);
            }
        }
        return mapChanges;
    }

    private void tickMonster(Monster monster, MapChanges mapChanges) {
        List<Direction> directions = Arrays.asList(Direction.values());
        Collections.shuffle(directions);

        for (Direction dir : directions) {
            if (areaMap.canEnter(monster, dir)) {
                Position from = new Position(monster.position);
                monster.move(dir);
                Position to = new Position(monster.position);
                MonsterMovement monsterMovement = new MonsterMovement(monster.entityId.toString(), from, to);
                mapChanges.monsterMovements.add(monsterMovement);
                break;
            }
        }
    }

    public Monsters getMonsters() {
        return monsters;
    }

    @Override
    protected void handlePlayerMovementRequest(PlayerMovementRequest playerMovementRequest) {
        Player player = players.getPlayer(playerMovementRequest.playerId);
        if(player != null){

        }
    }
}
