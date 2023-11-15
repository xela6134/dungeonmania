package dungeonmania.behaviors;

import dungeonmania.entities.Entity;
import dungeonmania.map.GameMap;

public interface Movable {
    public void onMovedAway(GameMap map, Entity entity);
}
