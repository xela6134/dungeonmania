package dungeonmania.entities.logical;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.entities.Entity;
import dungeonmania.entities.logical.strategy.Strategy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public abstract class LightBulb extends Entity implements LogicObserver {
    private Strategy strategy;
    private List<LogicObserver> observers = new ArrayList<>();

    public LightBulb(Position position, Strategy strategy) {
        super(position);
        this.strategy = strategy;
    }

    public void subscribe(LogicObserver observer, GameMap map) {
        if (observers.contains(observer)) return;
        observers.add(observer);
    }

    public void unsubscribe(LogicObserver observer, GameMap map) {
        observers.remove(observer);
    }

    public void transferConnections(LightBulb bulb, GameMap map) {
        // Connect new bulb to surrounding logicals
        for (LogicObserver o : observers) {
            o.subscribe(bulb, map);
            bulb.subscribe(o, map);
        }

        // Disconnect current from logical entities
        observers.forEach(o -> o.unsubscribe(this, map));

        // Clear the list
        observers.clear();

        map.destroyEntity(this);
        map.addEntity(bulb);
    }

    public Strategy getStrategy() {
        return strategy;
    }
}
