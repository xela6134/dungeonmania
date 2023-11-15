package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.battles.Battleable;
import dungeonmania.behaviors.Destroyable;
import dungeonmania.behaviors.Overlappable;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.entities.enemies.state.State;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public abstract class Enemy extends Entity implements Battleable, Overlappable, Destroyable {
    private BattleStatistics battleStatistics;
    private boolean isAdjacentToPlayer = false;
    private State state;

    public Enemy(Position position, double health, double attack, State state) {
        super(position.asLayer(Entity.CHARACTER_LAYER));
        battleStatistics = new BattleStatistics(health, attack, 0, BattleStatistics.DEFAULT_DAMAGE_MAGNIFIER,
                BattleStatistics.DEFAULT_ENEMY_DAMAGE_REDUCER);
        this.state = state;
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return entity instanceof Player;
    }

    @Override
    public BattleStatistics getBattleStatistics() {
        return battleStatistics;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            map.battleInGame(player, this);
        }
    }

    @Override
    public void onDestroy(GameMap map) {
        Game g = map.getGame();
        g.unsubscribe(getId());
        map.incrementEnemiesKilled();
    }

    public abstract void move(Game game);

    public boolean getIsAdjacentToPlayer() {
        return isAdjacentToPlayer;
    }

    public void setIsAdjacentToPlayer(boolean isAdjacentToPlayer) {
        this.isAdjacentToPlayer = isAdjacentToPlayer;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public double getBattleHealth() {
        return battleStatistics.getHealth();
    }

    public void setBattleHealth(double health) {
        BattleStatistics newStatistics = this.battleStatistics;
        newStatistics.setHealth(health);
        this.battleStatistics = newStatistics;
    }

    public Position getNexPosition(GameMap map) {
        return state.getPosition(map, this, getPosition(), map.getPlayer());
    }
}
