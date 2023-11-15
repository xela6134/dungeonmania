package dungeonmania.entities;

import dungeonmania.entities.logical.Switch;
import dungeonmania.entities.logical.Wire;
import dungeonmania.map.GameMap;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class Entity {
    public static final int FLOOR_LAYER = 0;
    public static final int ITEM_LAYER = 1;
    public static final int DOOR_LAYER = 2;
    public static final int CHARACTER_LAYER = 3;

    private Position position;
    private Position previousPosition;
    private Position previousDistinctPosition;
    private Direction facing;
    private String entityId;

    public Entity(Position position) {
        this.position = position;
        this.previousPosition = position;
        this.previousDistinctPosition = null;
        this.entityId = UUID.randomUUID().toString();
        this.facing = null;
    }

    public boolean canMoveOnto(GameMap map, Entity entity) {
        return false;
    }

    public void setPosition(Position position) {
        previousPosition = this.position;
        this.position = position;
        if (!previousPosition.equals(this.position)) {
            previousDistinctPosition = previousPosition;
        }
    }

    public Position getPosition() {
        return position;
    }

    public Position getPreviousPosition() {
        return previousPosition;
    }

    public Position getPreviousDistinctPosition() {
        return previousDistinctPosition;
    }

    public String getId() {
        return entityId;
    }

    public void setFacing(Direction facing) {
        this.facing = facing;
    }

    public Direction getFacing() {
        return this.facing;
    }

    public List<Position> getCardinallyAdjacentPositions() {
        return position.getCardinallyAdjacentPositions();
    }


    // Below are for checking CO_AND
    private int activatedLastTick;

    public List<Entity> getAdjacentElectrics(GameMap map) {
        List<Entity> adjacentElectrics = new ArrayList<>();
        List<Position> adjPosList = getCardinallyAdjacentPositions();
        for (Position pos : adjPosList) {
            List<Entity> tempList = map.getEntities(pos).stream()
                .filter(e -> (e instanceof Switch) || (e instanceof Wire))
                .collect(Collectors.toList());
            adjacentElectrics.addAll(tempList);
        }
        return adjacentElectrics;
    }

    public int getMaxActivatable(GameMap map) {
        return getAdjacentElectrics(map).size();
    }

    public int currActivated(GameMap map) {
        int activated = 0;
        for (Entity elec : getAdjacentElectrics(map)) {
            if (elec instanceof Switch) {
                if (((Switch) elec).isActivated()) activated++;
            } else {
                if (((Wire) elec).isActivated()) activated++;
            }
        }
        return activated;
    }

    public void setLastActivated(int activatedLastTick) {
        this.activatedLastTick = activatedLastTick;
    }

    public int getLastActivated() {
        return activatedLastTick;
    }
}
