package dungeonmania.entities.buildables;

import dungeonmania.behaviors.Buffable;
import dungeonmania.entities.BattleItem;
import dungeonmania.entities.Entity;
import dungeonmania.entities.inventory.Inventory;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.util.Position;

public abstract class Buildable extends Entity implements InventoryItem, BattleItem, Buffable {
    public Buildable(Position position) {
        super(position);
    }

    public abstract boolean hasRequiredItems(Inventory inventory);
    public abstract void consumeRequiredItems(Inventory inventory);
}
