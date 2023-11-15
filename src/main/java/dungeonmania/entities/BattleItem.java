package dungeonmania.entities;

import dungeonmania.Game;

/**
 * Item has buff in battles
 */
public interface BattleItem {
    public void use(Game game);
    public int getDurability();
}
