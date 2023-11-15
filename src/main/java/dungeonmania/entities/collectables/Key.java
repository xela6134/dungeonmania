package dungeonmania.entities.collectables;

import dungeonmania.behaviors.Overlappable;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Key extends Entity implements InventoryItem, Overlappable {
    private int number;

    public Key(Position position, int number) {
        super(position);
        this.number = number;
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Player) {
            ((Player) entity).collect(this, map);
        }
    }

    public int getnumber() {
        return number;
    }

}
