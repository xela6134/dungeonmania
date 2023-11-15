package dungeonmania.goals;

import java.util.List;

import dungeonmania.entities.Entity;
import dungeonmania.entities.Exit;
import dungeonmania.entities.Player;
import dungeonmania.util.Position;
import dungeonmania.Game;

public class ExitGoal extends Goal {
    public ExitGoal() {
        super();
    }

    @Override
    public boolean achieved(Game game) {
        Player character = game.getPlayer();
        Position pos = character.getPosition();
        List<Exit> es = game.getMapEntities(Exit.class);
        if (es == null || es.size() == 0)
            return false;
        return es.stream().map(Entity::getPosition).anyMatch(pos::equals);

    }

    @Override
    public String toString(Game game) {
        if (achieved(game)) {
            return "";
        } else {
            return ":exit";
        }
    }
}
