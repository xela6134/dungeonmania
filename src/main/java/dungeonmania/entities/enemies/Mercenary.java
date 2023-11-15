package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Interactable;
import dungeonmania.entities.Player;
import dungeonmania.entities.buildables.Sceptre;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.enemies.state.AlliedMercenaryState;
import dungeonmania.entities.enemies.state.DefaultMercenaryState;
import dungeonmania.entities.enemies.state.PlayerInvincibleState;
import dungeonmania.entities.enemies.state.PlayerInvisibleState;
import dungeonmania.entities.enemies.statechangers.ChangeToInvincible;
import dungeonmania.entities.enemies.statechangers.ChangeToInvisible;
import dungeonmania.entities.enemies.statechangers.ChangeToNormal;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Mercenary extends Enemy implements Interactable, ChangeToNormal, ChangeToInvincible, ChangeToInvisible {
    public static final int DEFAULT_BRIBE_AMOUNT = 1;
    public static final int DEFAULT_BRIBE_RADIUS = 1;
    public static final double DEFAULT_ATTACK = 5.0;
    public static final double DEFAULT_HEALTH = 10.0;

    private int bribeAmount = Mercenary.DEFAULT_BRIBE_AMOUNT;
    private int bribeRadius = Mercenary.DEFAULT_BRIBE_RADIUS;

    private double allyAttack;
    private double allyDefence;
    private boolean allied = false;
    private boolean isMindControlled = false;
    private int mindControlDuration = 0;

    public Mercenary(Position position, double health, double attack, int bribeAmount, int bribeRadius,
            double allyAttack, double allyDefence) {
        super(position, health, attack, new DefaultMercenaryState());
        this.bribeAmount = bribeAmount;
        this.bribeRadius = bribeRadius;
        this.allyAttack = allyAttack;
        this.allyDefence = allyDefence;
    }

    public boolean isAllied() {
        return allied;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (allied)
            return;
        super.onOverlap(map, entity);
    }

    /**
     * check whether the current merc can be bribed
     * @param player
     * @return
     */
    private boolean canBeBribed(Player player) {
        return bribeRadius >= 0 && player.countEntityOfType(Treasure.class) >= bribeAmount;
    }

    /**
     * bribe the merc
     */
    private void bribe(Player player) {
        for (int i = 0; i < bribeAmount; i++) {
            player.use(Treasure.class);
        }
        setState(new AlliedMercenaryState());
    }

    public boolean getIsMindControlled() {
        return isMindControlled;
    }

    public void setIsMindControlled(boolean isMindControlled) {
        this.isMindControlled = isMindControlled;
        this.allied = false;
    }

    public int getMindControlDuration() {
        return mindControlDuration;
    }

    public void setMindControlDuration(int mindControlDuration) {
        this.mindControlDuration = mindControlDuration;
    }

    public void decreaseMindControlDuration() {
        mindControlDuration--;
    }

    @Override
    public void interact(Player player, Game game) {
        if (Position.isAdjacent(player.getPosition(), getPosition()) && player.hasSceptre()) {
            // mind control case
            setIsMindControlled(true);
            Sceptre sceptre = player.getInventory().getSceptre();
            setMindControlDuration(sceptre.getMindControlDuration());
            sceptre.use(game);
        } else {
            // bribe case
            bribe(player);
        }
        setState(new AlliedMercenaryState());
        allied = true;
        if (!getIsAdjacentToPlayer() && Position.isAdjacent(player.getPosition(), getPosition()))
            setIsAdjacentToPlayer(true);
    }

    @Override
    public void move(Game game) {
        GameMap map = game.getMap();
        Position nextPos = getNexPosition(map);
        map.moveTo(this, nextPos);
    }

    @Override
    public boolean isInteractable(Player player) {
        boolean interactable = false;
        if (!allied && canBeBribed(player)) {
            interactable = true;
        } else if (Position.isAdjacent(player.getPosition(), getPosition()) && player.hasSceptre()) {
            interactable = true;
        }
        return interactable;
    }

    @Override
    public BattleStatistics getBattleStatistics() {
        if (!allied)
            return super.getBattleStatistics();
        return new BattleStatistics(0, allyAttack, allyDefence, 1, 1);
    }

    @Override
    public void changeToNormalState() {
        if (allied) setState(new AlliedMercenaryState());
        else setState(new DefaultMercenaryState());
    }

    @Override
    public void changeToInvisibleState() {
        if (allied) setState(new AlliedMercenaryState());
        else setState(new PlayerInvisibleState());
    }

    @Override
    public void changeToInvincibleState() {
        if (allied) setState(new AlliedMercenaryState());
        else setState(new PlayerInvincibleState());
    }
}
