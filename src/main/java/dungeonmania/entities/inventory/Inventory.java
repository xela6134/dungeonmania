package dungeonmania.entities.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.entities.BattleItem;
import dungeonmania.entities.Entity;
import dungeonmania.entities.EntityFactory;
import dungeonmania.entities.buildables.Bow;
import dungeonmania.entities.buildables.Buildable;
import dungeonmania.entities.buildables.Sceptre;
import dungeonmania.entities.collectables.Arrow;
import dungeonmania.entities.collectables.Key;
import dungeonmania.entities.collectables.SunStone;
import dungeonmania.entities.collectables.Sword;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.collectables.Wood;

public class Inventory {
    private List<InventoryItem> items = new ArrayList<>();

    public boolean add(InventoryItem item) {
        items.add(item);
        return true;
    }

    public <T extends InventoryItem> void removeEntity(Class<T> clz, int count) {
        List<T> entities = getEntities(clz);
        for (int i = 0; i < count && i < entities.size(); i++) {
            if (entities != null) items.remove(entities.get(i));
        }
    }

    public List<String> getBuildables() {
        int woods = count(Wood.class);
        int arrows = count(Arrow.class);
        int treasures = count(Treasure.class);
        int keys = count(Key.class);
        int sunstones = count(SunStone.class);
        int swords = count(Sword.class);

        List<String> result = new ArrayList<>();

        if (woods >= 1 && arrows >= 3) {
            result.add("bow");
        }

        if (woods >= 2 && (treasures >= 1 || keys >= 1 || sunstones >= 1)) {
            result.add("shield");
        }

        if ((woods >= 1 || arrows >= 2) && (keys >= 1 || treasures >= 1) && (sunstones >= 1)) {
            result.add("sceptre");
        } else if ((woods >= 1 || arrows >= 2) && (sunstones >= 2)) {
            result.add("sceptre");
        }

        if (swords >= 1 && sunstones >= 1) {
            result.add("midnight_armour");
        }
        return result;
    }

    public InventoryItem checkBuildCriteria(String itemToBuild, EntityFactory factory) {
        Buildable buildableItem = null;
        switch (itemToBuild) {
            case "bow":
                buildableItem = factory.buildBow();
                break;
            case "shield":
                buildableItem = factory.buildShield();
                break;
            case "sceptre":
                buildableItem = factory.buildSceptre();
                break;
            case "midnight_armour":
                buildableItem = factory.buildMidnightArmour();
                break;
            default:
                return null;
        }

        if (buildableItem.hasRequiredItems(this)) {
            buildableItem.consumeRequiredItems(this);
        }

        return buildableItem;
    }

    public <T extends InventoryItem> T getFirst(Class<T> itemType) {
        for (InventoryItem item : items)
            if (itemType.isInstance(item))
                return itemType.cast(item);
        return null;
    }

    public <T extends InventoryItem> int count(Class<T> itemType) {
        int count = 0;
        for (InventoryItem item : items)
            if (itemType.isInstance(item))
                count++;
        return count;
    }

    public Entity getEntity(String itemUsedId) {
        for (InventoryItem item : items)
            if (((Entity) item).getId().equals(itemUsedId))
                return (Entity) item;
        return null;
    }

    public List<Entity> getEntities() {
        return items.stream().map(Entity.class::cast).collect(Collectors.toList());
    }

    public <T> List<T> getEntities(Class<T> clz) {
        return items.stream().filter(clz::isInstance).map(clz::cast).collect(Collectors.toList());
    }

    public boolean hasWeapon() {
        return getFirst(Sword.class) != null || getFirst(Bow.class) != null;
    }

    public boolean hasSceptre() {
        return getFirst(Sceptre.class) != null;
    }

    public BattleItem getWeapon() {
        BattleItem weapon = getFirst(Sword.class);
        if (weapon == null)
            return getFirst(Bow.class);
        return weapon;
    }

    public Sceptre getSceptre() {
        return getFirst(Sceptre.class);
    }
}
