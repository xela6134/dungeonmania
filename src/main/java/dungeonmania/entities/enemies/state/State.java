package dungeonmania.entities.enemies.state;

import dungeonmania.entities.Player;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public interface State {
    public Position getPosition(GameMap map, Enemy enemy, Position position, Player player);
}
