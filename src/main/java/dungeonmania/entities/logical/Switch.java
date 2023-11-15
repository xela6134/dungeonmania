package dungeonmania.entities.logical;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.behaviors.Movable;
import dungeonmania.behaviors.Overlappable;
import dungeonmania.entities.Boulder;
import dungeonmania.entities.Entity;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Switch extends Entity implements Overlappable, Movable, LogicObserver {
    private boolean activated;
    private List<LogicObserver> observers = new ArrayList<>();

    public Switch(Position position) {
        super(position.asLayer(Entity.ITEM_LAYER));
    }

    @Override
    public void subscribe(LogicObserver observer, GameMap map) {
        if (observers.contains(observer)) return;
        observers.add(observer);
        // Detonate bombs if placed next to activated switch
        if (activated && observer instanceof Bomb) {
            observer.updateOnOverlap(map);
        }
    }

    @Override
    public void unsubscribe(LogicObserver observer, GameMap map) {
        observers.remove(observer);
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    @Override
    public void updateOnMovedAway(GameMap map) {
    }

    @Override
    public void updateOnOverlap(GameMap map) {
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Boulder) {
            activated = true;
            new ArrayList<>(observers).forEach(o -> o.updateOnOverlap(map));
        }
    }

    @Override
    public void onMovedAway(GameMap map, Entity entity) {
        if (entity instanceof Boulder) {
            activated = false;
            new ArrayList<>(observers).forEach(o -> o.updateOnMovedAway(map));
        }
    }

    public boolean isActivated() {
        return activated;
    }
}
