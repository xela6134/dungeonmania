package dungeonmania.behaviors;

import dungeonmania.battles.BattleStatistics;

public interface Buffable {
    BattleStatistics applyBuff(BattleStatistics origin);
}
