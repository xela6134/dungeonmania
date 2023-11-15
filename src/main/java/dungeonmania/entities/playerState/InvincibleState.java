package dungeonmania.entities.playerState;

import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.Player;

public class InvincibleState extends PlayerState {
    public InvincibleState(Player player) {
        super(player, true, false);
    }

    @Override
    public BattleStatistics getBattleStatistics(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(
            0,
            0,
            0,
            1,
            1,
            true,
            true));
    }
}
