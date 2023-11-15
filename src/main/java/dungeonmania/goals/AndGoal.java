package dungeonmania.goals;

import dungeonmania.Game;

public class AndGoal extends Goal {

    public AndGoal(Goal goal1, Goal goal2) {
        super(goal1, goal2);
    }

    @Override
    public boolean achieved(Game game) {
        if (getGoal2() instanceof BouldersGoal) {
            boolean hi = getGoal2().achieved(game);
            System.out.print(hi);
        }
        return getGoal1().achieved(game) && getGoal2().achieved(game);
    }

    @Override
    public String toString(Game game) {
        if (achieved(game)) {
            return "";
        } else {
            return "(" + getGoal1().toString(game) + " AND " + getGoal2().toString(game) + ")";
        }
    }
}
