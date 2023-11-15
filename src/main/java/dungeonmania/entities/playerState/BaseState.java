package dungeonmania.entities.playerState;

import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.Player;

public class BaseState extends PlayerState {
    public BaseState(Player player) {
        super(player, false, false);
    }

    @Override
    public BattleStatistics getBattleStatistics(BattleStatistics origin) {
        return origin;
    }
}
