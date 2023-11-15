package dungeonmania.goals;

import dungeonmania.Game;
import dungeonmania.entities.Player;
import dungeonmania.entities.enemies.ZombieToastSpawner;

public class EnemyGoal extends Goal {
    public EnemyGoal(int target) {
        super(target);
    }

    @Override
    public boolean achieved(Game game) {
        Player p = game.getPlayer();
        int enemiesKilled = p.getEnemiesKilled();
        int numOfSpawners = game.getMapEntities(ZombieToastSpawner.class).size();

        return (numOfSpawners == 0 && enemiesKilled >= getTarget()) ? true : false;
    }

    @Override
    public String toString(Game game) {
        if (achieved(game)) {
            return "";
        } else {
            return ":enemies";
        }
    }
}
