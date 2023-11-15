package dungeonmania.entities.enemies.state;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import dungeonmania.entities.Player;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class DefaultZombieToastState implements State {

    @Override
    public Position getPosition(GameMap map, Enemy enemy, Position position, Player player) {
        List<Position> pos = position.getCardinallyAdjacentPositions();
        pos = pos.stream().filter(p -> map.canMoveTo(enemy, p)).collect(Collectors.toList());
        if (pos.size() == 0) {
            return position;
        } else {
            Random randGen = new Random();
            return pos.get(randGen.nextInt(pos.size()));
        }
    }
}
