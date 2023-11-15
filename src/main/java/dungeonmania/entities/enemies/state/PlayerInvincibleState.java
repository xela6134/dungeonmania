package dungeonmania.entities.enemies.state;

import dungeonmania.entities.Player;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class PlayerInvincibleState implements State {

    @Override
    public Position getPosition(GameMap map, Enemy enemy, Position position, Player player) {
        Position plrDiff = Position.calculatePositionBetween(map.getPlayer().getPosition(), position);

        Position moveX = (plrDiff.getX() >= 0) ? Position.translateBy(position, Direction.RIGHT)
                : Position.translateBy(position, Direction.LEFT);
        Position moveY = (plrDiff.getY() >= 0) ? Position.translateBy(position, Direction.UP)
                : Position.translateBy(position, Direction.DOWN);
        Position offset = position;

        if (plrDiff.getY() == 0 && map.canMoveTo(enemy, moveX))
            offset = moveX;
        else if (plrDiff.getX() == 0 && map.canMoveTo(enemy, moveY))
            offset = moveY;
        else if (Math.abs(plrDiff.getX()) >= Math.abs(plrDiff.getY())) {
            if (map.canMoveTo(enemy, moveX))
                offset = moveX;
            else if (map.canMoveTo(enemy, moveY))
                offset = moveY;
            else
                offset = position;
        } else {
            if (map.canMoveTo(enemy, moveY))
                offset = moveY;
            else if (map.canMoveTo(enemy, moveX))
                offset = moveX;
            else
                offset = position;
        }
        return offset;
    }
}
