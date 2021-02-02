package com.github.martinfrank.multiplayerareaserver.model;

import com.github.martinfrank.multiplayerprotocol.area.Direction;
import com.github.martinfrank.multiplayerprotocol.area.Monster;
import com.github.martinfrank.multiplayerprotocol.area.Position;
import org.mapeditor.core.Map;
import org.mapeditor.core.MapLayer;
import org.mapeditor.core.TileLayer;

import java.util.Optional;

public class AreaMap {

    private final Map map;

    public AreaMap(Map map) {
        this.map = map;
    }

    public boolean canEnter(Monster monster, Direction dir) {
        Position position = new Position(monster.position);
        position.move(dir);
        Optional<MapLayer> layer = map.getLayers().stream().filter(l -> "Floor Block".equals(l.getName())).findAny();
        if(!layer.isPresent()){
            return false;
        }
        if(layer.get() instanceof TileLayer){
            TileLayer tileLayer = (TileLayer) layer.get();
            return tileLayer.getTileAt(position.x, position.y) == null;
        }else{
            return false;
        }
    }

}
