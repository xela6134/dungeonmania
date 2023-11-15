package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.behaviors.Destroyable;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Interactable;
import dungeonmania.entities.Player;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class ZombieToastSpawner extends Entity implements Interactable, Destroyable {
    public static final int DEFAULT_SPAWN_INTERVAL = 0;

    public ZombieToastSpawner(Position position, int spawnInterval) {
        super(position);
    }

    public void spawn(Game game) {
        game.spawnZombie(game, this);
    }

    @Override
    public void onDestroy(GameMap gameMap) {
        gameMap.unsubscribe(getId());
    }

    @Override
    public void interact(Player player, Game game) {
        player.getInventory().getWeapon().use(game);
        onDestroy(game.getMap());
        game.destroyEntity(this);
    }

    @Override
    public boolean isInteractable(Player player) {
        return Position.isAdjacent(player.getPosition(), getPosition()) && player.hasWeapon();
    }
}
