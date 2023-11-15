package dungeonmania.goals;

import dungeonmania.Game;

public abstract class Goal {
    private int target;
    private Goal goal1;
    private Goal goal2;

    public Goal() {

    }

    public Goal(int target) {
        this.target = target;
    }

    public Goal(Goal goal1, Goal goal2) {
        this.goal1 = goal1;
        this.goal2 = goal2;
    }

    public int getTarget() {
        return this.target;
    }

    public Goal getGoal1() {
        return this.goal1;
    }

    public Goal getGoal2() {
        return this.goal2;
    }

    /**
     * @return true if the goal has been achieved, false otherwise
     */
    public abstract boolean achieved(Game game);

    public abstract String toString(Game game);
}
