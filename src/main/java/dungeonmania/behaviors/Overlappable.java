package dungeonmania.behaviors;

import dungeonmania.entities.Entity;
import dungeonmania.map.GameMap;

public interface Overlappable {
    public void onOverlap(GameMap map, Entity entity);
}
