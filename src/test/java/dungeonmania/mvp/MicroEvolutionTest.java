package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MicroEvolutionTest {
    @Test
    @Tag("16-1")
    @DisplayName("Testing if a certain amount of enemies have been killed")
    public void threeEnemiesKilled() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_microEvolutionTest_threeEnemies", "c_microEvolutionTest_threeEnemies");

        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // 1 Mercenary Killed
        res = dmc.tick(Direction.DOWN);
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // 2 Mercenaries Killed
        res = dmc.tick(Direction.DOWN);
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // 3 Mercenaries Killed
        res = dmc.tick(Direction.DOWN);
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @Tag("16-2")
    @DisplayName("Testing if all spawners have been destroyed")
    public void spawnersDestroyed() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();

        DungeonResponse res = dmc.newGame("d_microEvolutionTest_spawners", "c_microEvolutionTest_spawners");
        assertEquals(2, TestUtils.getEntities(res, "zombie_toast_spawner").size());
        String spawner1 = TestUtils.getEntities(res, "zombie_toast_spawner").get(0).getId();
        String spawner2 = TestUtils.getEntities(res, "zombie_toast_spawner").get(1).getId();

        res = dmc.tick(Direction.UP);
        res = assertDoesNotThrow(() -> dmc.interact(spawner2));
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = assertDoesNotThrow(() -> dmc.interact(spawner1));
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @Tag("16-3")
    @DisplayName("Testing bomb kills being counted towards enemy kills")
    public void bombZombieKills() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_a_microEvolutionTest_bombKills", "c_a_microEvolutionTest_bombKills");
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        res = dmc.tick(Direction.UP);
        assertFalse(TestUtils.getGoals(res).contains(":enemies"));
        assertEquals("", TestUtils.getGoals(res));
    }
}
