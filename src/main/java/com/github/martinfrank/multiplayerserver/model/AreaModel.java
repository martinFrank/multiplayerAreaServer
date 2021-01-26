package com.github.martinfrank.multiplayerserver.model;

import org.mapeditor.core.Map;

import java.util.ArrayList;
import java.util.List;

public class AreaModel {

    private List<Monster> monster = new ArrayList<>();

    public AreaModel(Map map) {
    }

    public MapChanges tick() {
        return new MapChanges();
    }
}
