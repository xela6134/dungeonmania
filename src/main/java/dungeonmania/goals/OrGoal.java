package dungeonmania.goals;

import dungeonmania.Game;

public class OrGoal extends Goal {
    public OrGoal(Goal goal1, Goal goal2) {
        super(goal1, goal2);
    }

    @Override
    public boolean achieved(Game game) {
        return getGoal1().achieved(game) || getGoal2().achieved(game);
    }

    @Override
    public String toString(Game game) {
        if (achieved(game)) {
            return "";
        } else {
            return "(" + getGoal1().toString(game) + " OR " + getGoal2().toString(game) + ")";
        }
    }
}
