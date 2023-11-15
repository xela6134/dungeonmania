package dungeonmania.entities.logical;

import dungeonmania.entities.logical.strategy.Strategy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class LightBulbOn extends LightBulb {
    public LightBulbOn(Position position, Strategy strategy) {
        super(position, strategy);
    }

    @Override
    public void updateOnOverlap(GameMap map) {
        if (!getStrategy().checkConditionSatisfies(map, this)) {
            updateOnMovedAway(map);
        }
    }

    @Override
    public void updateOnMovedAway(GameMap map) {
        if (getStrategy().checkConditionSatisfies(map, this)) return;
        // Create a new LightBulbOff
        LightBulbOff bulb = new LightBulbOff(getPosition(), getStrategy());
        transferConnections(bulb, map);
    }
}
