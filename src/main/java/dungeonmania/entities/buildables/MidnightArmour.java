package dungeonmania.entities.buildables;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.collectables.SunStone;
import dungeonmania.entities.collectables.Sword;
import dungeonmania.entities.inventory.Inventory;

public class MidnightArmour extends Buildable {
    private double attack;
    private double defence;
    private int durability;

    public MidnightArmour(int durability, double attack, double defence) {
        super(null);
        this.durability = durability;
        this.attack = attack;
        this.defence = defence;
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(0, attack, defence, 1, 1));
    }

    @Override
    public void use(Game game) {
        return;
    }

    @Override
    public int getDurability() {
        return durability;
    }

    @Override
    public boolean hasRequiredItems(Inventory inventory) {
        return (inventory.count(Sword.class) >= 1 && inventory.count(SunStone.class) >= 1) ? true : false;
    }

    @Override
    public void consumeRequiredItems(Inventory inventory) {
        inventory.removeEntity(Sword.class, 1);
        inventory.removeEntity(SunStone.class, 1);
    }

}
