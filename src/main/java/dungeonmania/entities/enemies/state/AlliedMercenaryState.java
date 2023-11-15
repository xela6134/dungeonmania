package dungeonmania.entities.enemies.state;

import dungeonmania.entities.Player;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class AlliedMercenaryState implements State {

    @Override
    public Position getPosition(GameMap map, Enemy enemy, Position position, Player player) {
        Position nextPos = enemy.getIsAdjacentToPlayer() ? player.getPreviousDistinctPosition()
                : map.dijkstraPathFind(position, player.getPosition(), enemy);
        if (!enemy.getIsAdjacentToPlayer() && Position.isAdjacent(player.getPosition(), nextPos))
            enemy.setIsAdjacentToPlayer(true);
        return nextPos;
    }
}
