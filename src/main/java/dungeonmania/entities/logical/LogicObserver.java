package dungeonmania.entities.logical;

import dungeonmania.map.GameMap;

public interface LogicObserver {
    public void updateOnOverlap(GameMap map);
    public void updateOnMovedAway(GameMap map);
    public void subscribe(LogicObserver observer, GameMap map);
    public void unsubscribe(LogicObserver observer, GameMap map);
}
