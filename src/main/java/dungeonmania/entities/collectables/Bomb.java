package dungeonmania.entities.collectables;

import dungeonmania.util.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.behaviors.Overlappable;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.entities.logical.LogicObserver;
import dungeonmania.entities.logical.Switch;
import dungeonmania.entities.logical.Wire;
import dungeonmania.entities.logical.strategy.Strategy;
import dungeonmania.map.GameMap;

public class Bomb extends Entity implements InventoryItem, Overlappable, LogicObserver {
    public enum State {
        SPAWNED, INVENTORY, PLACED
    }

    public static final int DEFAULT_RADIUS = 1;
    private State state;
    private int radius;
    private List<LogicObserver> observers = new ArrayList<>();
    private boolean isLogical;
    private Strategy strategy;

    public Bomb(Position position, int radius) {
        super(position);
        state = State.SPAWNED;
        this.radius = radius;
        isLogical = false;
        this.strategy = null;
    }

    public Bomb(Position position, int radius, Strategy strategy) {
        super(position);
        state = State.SPAWNED;
        this.radius = radius;
        isLogical = true;
        this.strategy = strategy;
    }

    public void notify(GameMap map) {
        updateOnOverlap(map);
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (state != State.SPAWNED)
            return;
        if (entity instanceof Player) {
            ((Player) entity).collect(this, map);
            this.state = State.INVENTORY;

            if (!isLogical) return;
            // Disconnect from wires and switches on pickup
            new ArrayList<>(observers).forEach(o -> o.unsubscribe(this, map));
            observers.clear();
        }
    }

    public void onPutDown(GameMap map, Position p) {
        setPosition(Position.translateBy(getPosition(), Position.calculatePositionBetween(getPosition(), p)));
        map.addEntity(this);
        this.state = State.PLACED;

        List<Position> adjPosList = getCardinallyAdjacentPositions();
        adjPosList.stream().forEach(node -> {
            List<Entity> entities = map.getEntities(node).stream()
                    .filter(e -> (e instanceof Switch) || (e instanceof Wire)).collect(Collectors.toList());
            entities.stream().forEach(e -> {
                if (e instanceof Switch) {
                    ((Switch) e).subscribe(this, map);
                    this.subscribe((Switch) e, map);
                } else if (e instanceof Wire) {
                    ((Wire) e).subscribe(this, map);
                    this.subscribe((Wire) e, map);
                }
            });
        });
    }

    @Override
    public void updateOnOverlap(GameMap map) {
        if (isLogical && !getStrategy().checkConditionSatisfies(map, this)) return;
        int x = getPosition().getX();
        int y = getPosition().getY();
        for (int i = x - radius; i <= x + radius; i++) {
            for (int j = y - radius; j <= y + radius; j++) {
                List<Entity> entities = map.getEntities(new Position(i, j));
                entities = entities.stream().filter(e -> !(e instanceof Player)).collect(Collectors.toList());
                for (Entity e : entities) {
                    map.destroyEntity(e);
                }
            }
        }
    }

    @Override
    public void updateOnMovedAway(GameMap map) {
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


    public State getState() {
        return state;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public boolean isLogical() {
        return isLogical;
    }
}
