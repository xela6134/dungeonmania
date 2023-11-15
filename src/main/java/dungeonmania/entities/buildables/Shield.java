package dungeonmania.entities.buildables;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.collectables.Key;
import dungeonmania.entities.collectables.SunStone;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.collectables.Wood;
import dungeonmania.entities.inventory.Inventory;

public class Shield extends Buildable {
    private int durability;
    private double defence;

    public Shield(int durability, double defence) {
        super(null);
        this.durability = durability;
        this.defence = defence;
    }

    @Override
    public void use(Game game) {
        durability--;
        if (durability <= 0) {
            game.removeItem(this);
        }
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(0, 0, defence, 1, 1));
    }

    @Override
    public int getDurability() {
        return durability;
    }

    @Override
    public boolean hasRequiredItems(Inventory inventory) {
        boolean hasItems = false;

        if (inventory.count(Wood.class) >= 2
        && (inventory.count(Treasure.class) >= 1
            || inventory.count(Key.class) >= 1
            || inventory.count(SunStone.class) >= 1)) {
            hasItems = true;
        }
        return hasItems;
    }

    @Override
    public void consumeRequiredItems(Inventory inventory) {
        inventory.removeEntity(Wood.class, 2);
        if (inventory.count(Treasure.class) >= 1) {
            inventory.removeEntity(Treasure.class, 1);
        } else {
            inventory.removeEntity(Key.class, 1);
        }
    }
}
