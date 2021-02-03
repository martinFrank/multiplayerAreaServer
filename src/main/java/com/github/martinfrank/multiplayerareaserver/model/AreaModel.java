package com.github.martinfrank.multiplayerareaserver.model;

import com.github.martinfrank.multiplayerprotocol.area.*;
import com.github.martinfrank.multiplayerprotocol.area.MonsterMovement;
import com.github.martinfrank.multiplayerprotocol.area.MapChanges;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AreaModel {

    public static final Logger LOGGER = LoggerFactory.getLogger(AreaModel.class);

    private int increment = 0;

    private final Monsters monsters;
    private final Players players;

    private final AreaMap areaMap;
    private final MapChanges mapChanges = new MapChanges();

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
        increment = increment + 1;
        if (increment == 8) {
            increment = 0;
            for (Monster monster : monsters.getAll()) {
                tickMonster(monster);
            }
        }
        return mapChanges;
    }

    private void tickMonster(Monster monster) {
        List<Direction> directions = Arrays.asList(Direction.values());
        Collections.shuffle(directions);

        for (Direction dir : directions) {
            if (areaMap.canEnter(monster, dir)) {
                Position from = new Position(monster.position);
                monster.move(dir);
                Position to = new Position(monster.position);
                MonsterMovement monsterMovement = new MonsterMovement(monster.id, from, to);
                mapChanges.monsterMovements.add(monsterMovement);
                break;
            }
        }
    }

    public Monsters getMonsters() {
        return monsters;
    }

    public Players getPlayers() {
        return players;
    }

    public AreaMap getMap() {
        return areaMap;
    }

    public void clearMapChanges() {
        mapChanges.clear();
    }

    public MapChanges getMapChanges() {
        return mapChanges;
    }

    public AreaTotal getAreaTotal() {
        return new AreaTotal(monsters, players);
    }
}
