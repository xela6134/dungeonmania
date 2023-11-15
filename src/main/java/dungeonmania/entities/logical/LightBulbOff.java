package dungeonmania.entities.logical;

import dungeonmania.entities.logical.strategy.Strategy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class LightBulbOff extends LightBulb {
    public LightBulbOff(Position position, Strategy strategy) {
        super(position, strategy);
    }

    @Override
    public void updateOnOverlap(GameMap map) {
        if (!getStrategy().checkConditionSatisfies(map, this)) return;
        // Create a new LightBulbOn
        LightBulbOn bulb = new LightBulbOn(getPosition(), getStrategy());
        transferConnections(bulb, map);
    }

    @Override
    public void updateOnMovedAway(GameMap map) {
        if (!getStrategy().checkConditionSatisfies(map, this)) {
            updateOnOverlap(map);
        }
    }
}
