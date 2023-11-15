package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.entities.enemies.state.DefaultZombieToastState;
import dungeonmania.entities.enemies.state.PlayerInvincibleState;
import dungeonmania.entities.enemies.statechangers.ChangeToInvincible;
import dungeonmania.entities.enemies.statechangers.ChangeToNormal;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class ZombieToast extends Enemy implements ChangeToInvincible, ChangeToNormal {
    public static final double DEFAULT_HEALTH = 5.0;
    public static final double DEFAULT_ATTACK = 6.0;

    public ZombieToast(Position position, double health, double attack) {
        super(position, health, attack, new DefaultZombieToastState());
    }

    @Override
    public void move(Game game) {
        GameMap map = game.getMap();
        Position nextPos = getNexPosition(map);
        game.moveTo(this, nextPos);
    }

    @Override
    public void changeToNormalState() {
        setState(new DefaultZombieToastState());
    }

    @Override
    public void changeToInvincibleState() {
        setState(new PlayerInvincibleState());
    }
}
