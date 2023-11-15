package dungeonmania.entities.logical.strategy;

import dungeonmania.entities.Entity;
import dungeonmania.map.GameMap;

public class CoAndStrategy extends Strategy {
    @Override
    public boolean checkConditionSatisfies(GameMap map, Entity entity) {
        return (entity.getLastActivated() == 0 && entity.currActivated(map) == entity.getMaxActivatable(map));
    }
}
