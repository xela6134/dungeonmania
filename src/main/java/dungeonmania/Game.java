package dungeonmania;

import java.util.List;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.stream.Collectors;

import dungeonmania.battles.BattleFacade;
import dungeonmania.entities.Entity;
import dungeonmania.entities.EntityFactory;
import dungeonmania.entities.Interactable;
import dungeonmania.entities.Player;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.collectables.potions.Potion;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.enemies.Mercenary;
import dungeonmania.entities.enemies.ZombieToast;
import dungeonmania.entities.enemies.ZombieToastSpawner;
import dungeonmania.entities.enemies.state.AlliedMercenaryState;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.entities.logical.LightBulb;
import dungeonmania.entities.logical.SwitchDoor;
import dungeonmania.entities.playerState.InvincibleState;
import dungeonmania.entities.playerState.InvisibleState;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.goals.Goal;
import dungeonmania.map.GameMap;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Game {
    private String id;
    private String name;
    private Goal goals;
    private GameMap map;
    private Player player;
    private BattleFacade battleFacade;
    private EntityFactory entityFactory;
    private boolean isInTick = false;
    public static final int PLAYER_MOVEMENT = 0;
    public static final int PLAYER_MOVEMENT_CALLBACK = 1;
    public static final int AI_MOVEMENT = 2;
    public static final int AI_MOVEMENT_CALLBACK = 3;
    public static final int ITEM_LONGEVITY_UPDATE = 4;

    private ComparableCallback currentAction = null;

    private int tickCount = 0;
    private PriorityQueue<ComparableCallback> sub = new PriorityQueue<>();
    private PriorityQueue<ComparableCallback> addingSub = new PriorityQueue<>();

    public Game(String dungeonName) {
        this.name = dungeonName;
        this.map = new GameMap();
        this.battleFacade = new BattleFacade();
    }

    public void init() {
        this.id = UUID.randomUUID().toString();
        map.init();
        this.tickCount = 0;
        player = map.getPlayer();
        register(() -> player.onTick(tickCount), PLAYER_MOVEMENT, "potionQueue");
    }

    public Game tick(Direction movementDirection) {
        registerOnce(() -> player.move(this.getMap(), movementDirection), PLAYER_MOVEMENT, "playerMoves");
        tick();
        return this;
    }

    public Game tick(String itemUsedId) throws InvalidActionException {
        Entity item = player.getEntity(itemUsedId);
        if (item == null)
            throw new InvalidActionException(String.format("Item with id %s doesn't exist", itemUsedId));
        if (!(item instanceof Bomb) && !(item instanceof Potion))
            throw new IllegalArgumentException(String.format("%s cannot be used", item.getClass()));

        registerOnce(() -> {
            if (item instanceof Bomb)
                player.use((Bomb) item, map);
            if (item instanceof Potion)
                player.use((Potion) item, tickCount);
        }, PLAYER_MOVEMENT, "playerUsesItem");

        tick();
        return this;
    }

    public void battle(Player player, Enemy enemy) {
        battleFacade.battle(this, player, enemy);
        if (player.getBattleHealth() <= 0) {
            map.destroyEntity(player);
        }
        if (enemy.getBattleHealth() <= 0) {
            map.destroyEntity(enemy);
        }
    }

    public Game build(String buildable) throws InvalidActionException {
        List<String> buildables = player.getBuildables();

        // delete midnight_armour if zombies are present
        int numOfZombies = map.getEntities(ZombieToast.class).size();
        if (numOfZombies != 0) {
            buildables.remove("midnight_armour");
        }

        if (!buildables.contains(buildable)) {
            throw new InvalidActionException(String.format("%s cannot be built", buildable));
        }
        registerOnce(() -> player.build(buildable, entityFactory), PLAYER_MOVEMENT, "playerBuildsItem");
        tick();
        return this;
    }

    public Game interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        Entity e = map.getEntity(entityId);
        if (e == null || !(e instanceof Interactable))
            throw new IllegalArgumentException("Entity cannot be interacted");
        if (!((Interactable) e).isInteractable(player)) {
            throw new InvalidActionException("Entity cannot be interacted");
        }
        registerOnce(() -> ((Interactable) e).interact(player, this), PLAYER_MOVEMENT, "playerInteracts");
        tick();
        return this;
    }

    public void register(Runnable r, int priority, String id) {
        if (isInTick)
            addingSub.add(new ComparableCallback(r, priority, id));
        else
            sub.add(new ComparableCallback(r, priority, id));
    }

    public void registerOnce(Runnable r, int priority, String id) {
        if (isInTick)
            addingSub.add(new ComparableCallback(r, priority, id, true));
        else
            sub.add(new ComparableCallback(r, priority, id, true));
    }

    public void unsubscribe(String id) {
        if (this.currentAction != null && id.equals(this.currentAction.getId())) {
            this.currentAction.invalidate();
        }

        for (ComparableCallback c : sub) {
            if (id.equals(c.getId())) {
                c.invalidate();
            }
        }
        for (ComparableCallback c : addingSub) {
            if (id.equals(c.getId())) {
                c.invalidate();
            }
        }
    }

    public int tick() {
        PriorityQueue<ComparableCallback> nextTickSub = new PriorityQueue<>();
        isInTick = true;
        while (!sub.isEmpty()) {
            currentAction = sub.poll();
            currentAction.run();
            if (currentAction.isValid()) {
                nextTickSub.add(currentAction);
            }
        }
        isInTick = false;
        nextTickSub.addAll(addingSub);
        addingSub = new PriorityQueue<>();
        sub = nextTickSub;
        tickCount++;
        changeEnemyStates();
        changeLogicConnectionHistory();
        return tickCount;
    }

    public int getTick() {
        return this.tickCount;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Goal getGoals() {
        return goals;
    }

    public void setGoals(Goal goals) {
        this.goals = goals;
    }

    public GameMap getMap() {
        return map;
    }

    public List<Entity> getMapEntities() {
        return map.getEntities();
    }

    public List<Entity> getMapEntities(Position p) {
        return map.getEntities(p);
    }

    public <T extends Entity> List<T> getMapEntities(Class<T> type) {
        return map.getEntities(type);
    }

    public void setMap(GameMap map) {
        this.map = map;
    }

    public EntityFactory getEntityFactory() {
        return entityFactory;
    }

    public void setEntityFactory(EntityFactory factory) {
        entityFactory = factory;
    }

    public int getCollectedTreasureCount() {
        return player.getCollectedTreasureCount();
    }

    public Player getPlayer() {
        return player;
    }

    public void removeItem(InventoryItem item) {
        player.remove(item);
    }

    public BattleFacade getBattleFacade() {
        return battleFacade;
    }

    public void spawnZombie(Game game, ZombieToastSpawner spawner) {
        entityFactory.spawnZombie(game, spawner);
    }

    public void destroyEntity(Entity entity) {
        map.destroyEntity(entity);
    }

    private void changeEnemyStates() {
        List<Mercenary> mercenaries = map.getEntities(Mercenary.class);
        List<ZombieToast> zombieToasts = map.getEntities(ZombieToast.class);

        if (player.getState().getClass().equals(InvincibleState.class)) {
            mercenaries.stream().forEach(x -> x.changeToInvincibleState());
            zombieToasts.stream().forEach(x -> x.changeToInvincibleState());
        } else if (player.getState().getClass().equals(InvisibleState.class)) {
            mercenaries.stream().forEach(x -> x.changeToInvisibleState());
        } else {
            mercenaries.stream().forEach(x -> x.changeToNormalState());
            zombieToasts.stream().forEach(x -> x.changeToNormalState());
        }

        List<Mercenary> mindControlledMercenaries = mercenaries.stream()
            .filter(m -> m.getIsMindControlled())
            .collect(Collectors.toList());
        for (Mercenary mercenary : mindControlledMercenaries) {
            mercenary.setState(new AlliedMercenaryState());
            mercenary.decreaseMindControlDuration();
            if (mercenary.getMindControlDuration() < 0) {
                mercenary.changeToNormalState();
                mercenary.setIsMindControlled(false);
            }
        }
    }

    private void changeLogicConnectionHistory() {
        List<LightBulb> lightBulbs = map.getEntities(LightBulb.class);
        lightBulbs.stream().forEach(l -> l.setLastActivated(l.currActivated(map)));
        List<SwitchDoor> switchDoors = map.getEntities(SwitchDoor.class);
        switchDoors.stream().forEach(d -> d.setLastActivated(d.currActivated(map)));
        List<Bomb> bombs = map.getEntities(Bomb.class);
        bombs.stream().forEach(b -> b.setLastActivated(b.currActivated(map)));
    }

    public void moveTo(Entity entity, Position position) {
        map.moveTo(entity, position);
    }
}
