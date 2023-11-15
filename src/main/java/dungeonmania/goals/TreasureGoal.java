package dungeonmania.goals;

import dungeonmania.Game;

public class TreasureGoal extends Goal {
    public TreasureGoal(int target) {
        super(target);
    }

    @Override
    public boolean achieved(Game game) {
        return game.getCollectedTreasureCount() >= getTarget();
    }

    @Override
    public String toString(Game game) {
        if (achieved(game)) {
            return "";
        } else {
            return ":treasure";
        }
    }
}
