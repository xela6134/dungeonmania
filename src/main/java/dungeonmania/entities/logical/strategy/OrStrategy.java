package dungeonmania.entities.logical.strategy;

import dungeonmania.entities.Entity;
import dungeonmania.map.GameMap;

public class OrStrategy extends Strategy {

    @Override
    public boolean checkConditionSatisfies(GameMap map, Entity entity) {
        setNum(0);
        setNumbers(map, entity);
        return (getNum() >= 1);
    }
}
