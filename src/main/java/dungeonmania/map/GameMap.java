package dungeonmania.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import dungeonmania.Game;
import dungeonmania.behaviors.Destroyable;
import dungeonmania.behaviors.Movable;
import dungeonmania.behaviors.Overlappable;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.entities.Portal;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.enemies.ZombieToastSpawner;
import dungeonmania.entities.logical.LightBulb;
import dungeonmania.entities.logical.Switch;
import dungeonmania.entities.logical.SwitchDoor;
import dungeonmania.entities.logical.Wire;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class GameMap {
    private Game game;
    private Map<Position, GraphNode> nodes = new HashMap<>();
    private Player player;

    /**
     * Initialise the game map
     * 1. pair up portals
     * 2. register all movables
     * 3. register all spawners
     * 4. register bombs and switches
     * 5. more...
     */
    public void init() {
        initPairPortals();
        initRegisterMovables();
        initRegisterSpawners();
        initRegisterBombsAndSwitches();
    }

    private void initRegisterBombsAndSwitches() {
        List<Bomb> bombs = getEntities(Bomb.class);
        List<Switch> switches = getEntities(Switch.class);
        List<Wire> wires = getEntities(Wire.class);
        List<LightBulb> lightbulbs = getEntities(LightBulb.class);
        List<SwitchDoor> switchdoors = getEntities(SwitchDoor.class);

        // Connect a bomb to a switch if they are adjacent
        for (Bomb b : bombs) {
            for (Switch s : switches) {
                if (Position.isAdjacent(b.getPosition(), s.getPosition())) {
                    s.subscribe(b, game.getMap());
                    b.subscribe(s, game.getMap());
                }
            }

            for (Wire w : wires) {
                if (Position.isAdjacent(b.getPosition(), w.getPosition())) {
                    w.subscribe(b, game.getMap());
                    b.subscribe(w, game.getMap());
                }
            }
        }

        // Connect a switch to a wire / lightbulb / switchdoor if they are adjacent
        for (Switch s : switches) {
            for (Wire w : wires) {
                if (Position.isAdjacent(s.getPosition(), w.getPosition())) {
                    s.subscribe(w, game.getMap());
                }
            }
            for (LightBulb l : lightbulbs) {
                if (Position.isAdjacent(s.getPosition(), l.getPosition())) {
                    s.subscribe(l, game.getMap());
                    l.subscribe(s, game.getMap());
                }
            }
            for (SwitchDoor d : switchdoors) {
                if (Position.isAdjacent(s.getPosition(), d.getPosition())) {
                    s.subscribe(d, game.getMap());
                    d.subscribe(s, game.getMap());
                }
            }
        }

        // Connect wire to a wire / lightbulb / switchdoor if they are adjacent
        for (Wire w1 : wires) {
            for (Wire w2 : wires) {
                if (Position.isAdjacent(w1.getPosition(), w2.getPosition())) {
                    w1.subscribe(w2, game.getMap());
                }
            }
            for (LightBulb l : lightbulbs) {
                if (Position.isAdjacent(w1.getPosition(), l.getPosition())) {
                    w1.subscribe(l, game.getMap());
                    l.subscribe(w1, game.getMap());
                }
            }
            for (SwitchDoor d : switchdoors) {
                if (Position.isAdjacent(w1.getPosition(), d.getPosition())) {
                    w1.subscribe(d, game.getMap());
                    d.subscribe(w1, game.getMap());
                }
            }
        }
    }

    // Pair up portals if there's any
    private void initPairPortals() {
        Map<String, Portal> portalsMap = new HashMap<>();
        nodes.forEach((k, v) -> {
            v.getEntities().stream().filter(Portal.class::isInstance).map(Portal.class::cast).forEach(portal -> {
                String color = portal.getColor();
                if (portalsMap.containsKey(color)) {
                    portal.bind(portalsMap.get(color));
                } else {
                    portalsMap.put(color, portal);
                }
            });
        });
    }

    private void initRegisterMovables() {
        List<Enemy> enemies = getEntities(Enemy.class);
        enemies.forEach(e -> {
            game.register(() -> e.move(game), Game.AI_MOVEMENT, e.getId());
        });
    }

    private void initRegisterSpawners() {
        List<ZombieToastSpawner> zts = getEntities(ZombieToastSpawner.class);
        zts.forEach(e -> {
            game.register(() -> e.spawn(game), Game.AI_MOVEMENT, e.getId());
        });
        game.register(() -> game.getEntityFactory().spawnSpider(game), Game.AI_MOVEMENT, "spawnSpiders");
    }

    public void moveTo(Entity entity, Position position) {
        if (!canMoveTo(entity, position))
            return;

        triggerMovingAwayEvent(entity);
        removeNode(entity);
        entity.setPosition(position);
        addEntity(entity);
        triggerOverlapEvent(entity);
    }

    public void moveTo(Entity entity, Direction direction) {
        if (!canMoveTo(entity, Position.translateBy(entity.getPosition(), direction)))
            return;
        triggerMovingAwayEvent(entity);
        removeNode(entity);
        entity.setPosition(Position.translateBy(entity.getPosition(), direction));
        addEntity(entity);
        triggerOverlapEvent(entity);
    }

    private void triggerMovingAwayEvent(Entity entity) {
        List<Runnable> callbacks = new ArrayList<>();
        getEntities(entity.getPosition()).forEach(e -> {
            if (e != entity && e instanceof Movable) {
                callbacks.add(() -> ((Movable) e).onMovedAway(this, entity));
            }
        });
        callbacks.forEach(callback -> {
            callback.run();
        });
    }

    private void triggerOverlapEvent(Entity entity) {
        List<Runnable> overlapCallbacks = new ArrayList<>();
        getEntities(entity.getPosition()).forEach(e -> {
            if (e != entity && e instanceof Overlappable) {
                overlapCallbacks.add(() -> ((Overlappable) e).onOverlap(this, entity));
            }
        });
        overlapCallbacks.forEach(callback -> {
            callback.run();
        });
    }

    public boolean canMoveTo(Entity entity, Position position) {
        return !nodes.containsKey(position) || nodes.get(position).canMoveOnto(this, entity);
    }

    public Position dijkstraPathFind(Position src, Position dest, Entity entity) {
        // if inputs are invalid, don't move
        if (!nodes.containsKey(src) || !nodes.containsKey(dest))
            return src;

        Map<Position, Integer> dist = new HashMap<>();
        Map<Position, Position> prev = new HashMap<>();
        Map<Position, Boolean> visited = new HashMap<>();

        prev.put(src, null);
        dist.put(src, 0);

        PriorityQueue<Position> q = new PriorityQueue<>((x, y) -> Integer
                .compare(dist.getOrDefault(x, Integer.MAX_VALUE), dist.getOrDefault(y, Integer.MAX_VALUE)));
        q.add(src);

        while (!q.isEmpty()) {
            Position curr = q.poll();
            if (curr.equals(dest) || dist.get(curr) > 200)
                break;
            // check portal
            if (nodes.containsKey(curr) && nodes.get(curr).getEntities().stream().anyMatch(Portal.class::isInstance)) {
                Portal portal = nodes.get(curr).getEntities().stream().filter(Portal.class::isInstance)
                        .map(Portal.class::cast).collect(Collectors.toList()).get(0);
                List<Position> teleportDest = portal.getDestPositions(this, entity);
                teleportDest.stream().filter(p -> !visited.containsKey(p)).forEach(p -> {
                    dist.put(p, dist.get(curr));
                    prev.put(p, prev.get(curr));
                    q.add(p);
                });
                continue;
            }
            visited.put(curr, true);
            List<Position> neighbours = curr.getCardinallyAdjacentPositions().stream()
                    .filter(p -> !visited.containsKey(p))
                    .filter(p -> !nodes.containsKey(p) || nodes.get(p).canMoveOnto(this, entity))
                    .collect(Collectors.toList());

            neighbours.forEach(n -> {
                int newDist = dist.get(curr) + (nodes.containsKey(n) ? nodes.get(n).getWeight() : 1);
                if (newDist < dist.getOrDefault(n, Integer.MAX_VALUE)) {
                    q.remove(n);
                    dist.put(n, newDist);
                    prev.put(n, curr);
                    q.add(n);
                }
            });
        }
        Position ret = dest;
        if (prev.get(ret) == null || ret.equals(src))
            return src;
        while (!prev.get(ret).equals(src)) {
            ret = prev.get(ret);
        }
        return ret;
    }

    public void removeNode(Entity entity) {
        Position p = entity.getPosition();
        if (nodes.containsKey(p)) {
            nodes.get(p).removeEntity(entity);
            if (nodes.get(p).size() == 0) {
                nodes.remove(p);
            }
        }
    }

    public void destroyEntity(Entity entity) {
        removeNode(entity);
        if (entity instanceof Destroyable) {
            ((Destroyable) entity).onDestroy(this);
        }
    }

    public void addEntity(Entity entity) {
        addNode(new GraphNode(entity));
    }

    public void addNode(GraphNode node) {
        Position p = node.getPosition();

        if (!nodes.containsKey(p))
            nodes.put(p, node);
        else {
            GraphNode curr = nodes.get(p);
            curr.mergeNode(node);
            nodes.put(p, curr);
        }
    }

    public Entity getEntity(String id) {
        Entity res = null;
        for (Map.Entry<Position, GraphNode> entry : nodes.entrySet()) {
            List<Entity> es = entry.getValue().getEntities().stream().filter(e -> e.getId().equals(id))
                    .collect(Collectors.toList());
            if (es != null && es.size() > 0) {
                res = es.get(0);
                break;
            }
        }
        return res;
    }

    public List<Entity> getEntities(Position p) {
        GraphNode node = nodes.get(p);
        return (node != null) ? node.getEntities() : new ArrayList<>();
    }

    public List<Entity> getEntities() {
        List<Entity> entities = new ArrayList<>();
        nodes.forEach((k, v) -> entities.addAll(v.getEntities()));
        return entities;
    }

    public <T extends Entity> List<T> getEntities(Class<T> type) {
        return getEntities().stream().filter(type::isInstance).map(type::cast).collect(Collectors.toList());
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void battleInGame(Player player, Enemy enemy) {
        game.battle(player, enemy);
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void unsubscribe(String id) {
        game.unsubscribe(id);
    }

    public void incrementEnemiesKilled() {
        player.incrementEnemiesKilled();
    }
}
