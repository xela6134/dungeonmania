package dungeonmania.goals;

import dungeonmania.Game;
import dungeonmania.entities.logical.Switch;

public class BouldersGoal extends Goal {
    public BouldersGoal() {
        super();
    }

    @Override
    public boolean achieved(Game game) {
        return game.getMap().getEntities(Switch.class).stream().allMatch(s -> s.isActivated());
    }

    @Override
    public String toString(Game game) {
        if (achieved(game)) {
            return "";
        } else {
            return ":boulders";
        }
    }
}
