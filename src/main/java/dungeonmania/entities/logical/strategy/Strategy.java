package dungeonmania.entities.logical.strategy;

import java.util.List;

import dungeonmania.entities.Entity;
import dungeonmania.entities.logical.Switch;
import dungeonmania.entities.logical.Wire;
import dungeonmania.map.GameMap;

public abstract class Strategy {
    private int numActivated = 0;

    public abstract boolean checkConditionSatisfies(GameMap map, Entity entity);

    public void setNumbers(GameMap map, Entity entity) {
        List<Entity> adjEntities = entity.getAdjacentElectrics(map);
        int activated = 0;
        for (Entity elec : adjEntities) {
            if (elec instanceof Switch) {
                if (((Switch) elec).isActivated()) activated++;
            } else {
                if (((Wire) elec).isActivated()) activated++;
            }
        }
        setNum(activated);
    }

    public void setNum(int numActivated) {
        this.numActivated = numActivated;
    }

    public int getNum() {
        return numActivated;
    }
}
