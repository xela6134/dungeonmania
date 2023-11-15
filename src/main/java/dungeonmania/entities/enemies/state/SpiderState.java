package dungeonmania.entities.enemies.state;

import dungeonmania.entities.Player;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class SpiderState implements State {
    /**
     * This function is not supposed to be called
     * Returns null by default
     */
    @Override
    public Position getPosition(GameMap map, Enemy enemy, Position position, Player player) {
        return null;
    }
}
