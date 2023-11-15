package dungeonmania.entities;

import dungeonmania.behaviors.Destroyable;
import dungeonmania.behaviors.Movable;
import dungeonmania.behaviors.Overlappable;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Exit extends Entity implements Overlappable, Movable, Destroyable {
    public Exit(Position position) {
        super(position.asLayer(Entity.ITEM_LAYER));
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        return;
    }

    @Override
    public void onMovedAway(GameMap map, Entity entity) {
        return;
    }

    @Override
    public void onDestroy(GameMap gameMap) {
        return;
    }
}
