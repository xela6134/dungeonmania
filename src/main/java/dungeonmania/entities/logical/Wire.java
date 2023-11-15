package dungeonmania.entities.logical;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.behaviors.Destroyable;
import dungeonmania.entities.Entity;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Wire extends Entity implements Destroyable, LogicObserver {
    private boolean activated = false;
    private List<LogicObserver> observers = new ArrayList<>();

    public Wire(Position position) {
        super(position);
    }

    public boolean isActivated() {
        return activated;
    }

    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    @Override
    public void subscribe(LogicObserver observer, GameMap map) {
        if (observers.contains(observer)) return;
        observers.add(observer);
        // Detonate bombs if placed next to activated wire
        if (activated && observer instanceof Bomb) {
            observer.updateOnOverlap(map);
        }
    }

    @Override
    public void unsubscribe(LogicObserver observer, GameMap map) {
        observers.remove(observer);
    }

    @Override
    public void onDestroy(GameMap map) {
        new ArrayList<>(observers).forEach(o -> o.unsubscribe(this, map));
        observers.clear();
    }

    @Override
    public void updateOnOverlap(GameMap map) {
        if (activated) return;

        activated = true;
        new ArrayList<>(observers).forEach(o -> o.updateOnOverlap(map));
    }

    @Override
    public void updateOnMovedAway(GameMap map) {
        if (!activated) return;

        activated = false;
        new ArrayList<>(observers).forEach(o -> o.updateOnMovedAway(map));
    }
}
