package dungeonmania.entities.buildables;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.collectables.Arrow;
import dungeonmania.entities.collectables.Wood;
import dungeonmania.entities.inventory.Inventory;

public class Bow extends Buildable {
    private int durability;

    public Bow(int durability) {
        super(null);
        this.durability = durability;
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
        return BattleStatistics.applyBuff(origin, new BattleStatistics(0, 0, 0, 2, 1));
    }

    @Override
    public int getDurability() {
        return durability;
    }

    @Override
    public boolean hasRequiredItems(Inventory inventory) {
        return inventory.getEntities(Wood.class).size() >= 1
        && inventory.getEntities(Arrow.class).size() >= 3;
    }

    @Override
    public void consumeRequiredItems(Inventory inventory) {
        inventory.removeEntity(Wood.class, 1);
        inventory.removeEntity(Arrow.class, 3);
    }
}
