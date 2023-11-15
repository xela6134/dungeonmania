package dungeonmania.entities.logical;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.entities.enemies.Spider;
import dungeonmania.entities.logical.strategy.Strategy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class SwitchDoor extends Entity implements LogicObserver {
    private boolean open;
    private Strategy strategy;
    private List<LogicObserver> observers = new ArrayList<>();

    public SwitchDoor(Position position, Strategy strategy, boolean open) {
        super(position);
        this.strategy = strategy;
        this.open = open;
    }

    public boolean isOpen() {
        return open;
    }

    @Override
    public void subscribe(LogicObserver observer, GameMap map) {
        if (observers.contains(observer)) return;
        observers.add(observer);
    }

    @Override
    public void unsubscribe(LogicObserver observer, GameMap map) {
        observers.remove(observer);
    }

    @Override
    public void updateOnOverlap(GameMap map) {
        if (!strategy.checkConditionSatisfies(map, this) && isOpen()) {
            updateOnMovedAway(map);
        } else if (strategy.checkConditionSatisfies(map, this) && !isOpen()) {
            SwitchDoor switchDoor = new SwitchDoor(getPosition(), strategy, true);
            transferConnections(switchDoor, map);
        }
    }

    @Override
    public void updateOnMovedAway(GameMap map) {
        if (strategy.checkConditionSatisfies(map, this) && !isOpen()) {
            updateOnOverlap(map);
        } else if (!strategy.checkConditionSatisfies(map, this) && isOpen()) {
            SwitchDoor switchDoor = new SwitchDoor(getPosition(), strategy, false);
            transferConnections(switchDoor, map);
        }
    }

    private void transferConnections(SwitchDoor door, GameMap map) {
        for (LogicObserver o : observers) {
            o.subscribe(door, map);
            door.subscribe(o, map);
        }

        // Get all switches and wires connected to this door
        observers.forEach(o -> o.unsubscribe(this, map));
        observers.clear();

        map.destroyEntity(this);
        map.addEntity(door);
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        if (open || entity instanceof Spider) {
            return true;
        }
        return (entity instanceof Player && open);
    }
}
