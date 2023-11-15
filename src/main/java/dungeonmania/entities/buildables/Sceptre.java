package dungeonmania.entities.buildables;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.collectables.Arrow;
import dungeonmania.entities.collectables.Key;
import dungeonmania.entities.collectables.SunStone;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.collectables.Wood;
import dungeonmania.entities.inventory.Inventory;

public class Sceptre extends Buildable {
    private int durability;
    private int mindControlDuration;

    public Sceptre(int durability, int mindControlDuration) {
        super(null);
        this.durability = durability;
        this.mindControlDuration = mindControlDuration;
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return origin;
    }

    @Override
    public void use(Game game) {
        durability--;
        if (durability <= 0) {
            game.removeItem(this);
        }
    }

    @Override
    public int getDurability() {
        return durability;
    }

    @Override
    public boolean hasRequiredItems(Inventory inventory) {
        return (sceptreBuild1(inventory) || sceptreBuild2(inventory)) ? true : false;
    }

    @Override
    public void consumeRequiredItems(Inventory inventory) {
        if (sceptreBuild1(inventory)) {
            defaultRemoval(inventory);
            if (inventory.count(Key.class) >= 1) {
                inventory.removeEntity(Key.class, 1);
            } else if (inventory.count(Treasure.class) >= 1) {
                inventory.removeEntity(Treasure.class, 1);
            }
        } else if (sceptreBuild2(inventory)) {
            defaultRemoval(inventory);
        }
    }

    public int getMindControlDuration() {
        return mindControlDuration;
    }

    /**
     * Using one sunstone
     */
    private boolean sceptreBuild1(Inventory inventory) {
        return ((inventory.count(Arrow.class) >= 2 || inventory.count(Wood.class) >= 1)
        && (inventory.count(Key.class) >= 1 || inventory.count(Treasure.class) >= 1)
        && inventory.count(SunStone.class) >= 1) ? true : false;
    }

    /**
     * Using two sunstones, one for interchanging
     */
    private boolean sceptreBuild2(Inventory inventory) {
        return ((inventory.count(Arrow.class) >= 2 || inventory.count(Wood.class) >= 1)
        && inventory.count(SunStone.class) >= 2) ? true : false;
    }

    private void defaultRemoval(Inventory inventory) {
        if (inventory.count(Arrow.class) >= 2) {
            inventory.removeEntity(Arrow.class, 2);
        } else if (inventory.count(Wood.class) >= 1) {
            inventory.removeEntity(Wood.class, 1);
        }
        inventory.removeEntity(SunStone.class, 1);
    }
}
