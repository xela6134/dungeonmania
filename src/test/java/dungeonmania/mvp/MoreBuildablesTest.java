package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MoreBuildablesTest {
    @Test
    @Tag("17-1")
    @DisplayName("Test if sun stone can be picked up, and clears treasure goal")
    public void testSunStonePickup() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_a_treasure_basic", "c_a_default");
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));

        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @Tag("17-2")
    @DisplayName("Test sceptre build - 1 wood, 1 treasure, 1 sun stone")
    public void testSceptreBuild1() throws IllegalArgumentException, InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_a_sceptrebuild_1", "c_a_default");

        res = dmc.tick(Direction.RIGHT);
        assertThrows(InvalidActionException.class, () -> dmc.build("sceptre"));
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.build("sceptre");

        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
    }

    @Test
    @Tag("17-3")
    @DisplayName("Test sceptre build - 1 wood, 2 sun stone")
    public void testSceptreBuild2() throws IllegalArgumentException, InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_a_sceptrebuild_2", "c_a_default");

        res = dmc.tick(Direction.RIGHT);
        assertThrows(InvalidActionException.class, () -> dmc.build("sceptre"));
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.build("sceptre");

        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
    }

    @Test
    @Tag("17-4")
    @DisplayName("Test sceptre build - 2 arrows, 1 key, 1 sun stone")
    public void testSceptreBuild1a() throws IllegalArgumentException, InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_a_sceptrebuild_1_1", "c_a_default");
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.build("sceptre");

        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(0, TestUtils.getInventory(res, "arrow").size());
        assertEquals(0, TestUtils.getInventory(res, "key").size());
    }

    @Test
    @Tag("17-5")
    @DisplayName("Test sceptre build - 2 arrows, 2 sun stones")
    public void testSceptreBuild2a() throws IllegalArgumentException, InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_a_sceptrebuild_2_1", "c_a_default");
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.build("sceptre");

        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(0, TestUtils.getInventory(res, "arrow").size());
    }

    @Test
    @Tag("17-6")
    @DisplayName("Test midnight armour build - 1 sun stone, 1 sword")
    public void testMidnightArmourBuild() throws IllegalArgumentException, InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_a_midnightarmourbuild", "c_a_default");

        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertThrows(InvalidActionException.class, () -> dmc.build("midnight_armour"));
        res = dmc.tick(Direction.RIGHT);
        res = assertDoesNotThrow(() -> dmc.build("midnight_armour"));
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(0, TestUtils.getInventory(res, "sword").size());
        assertEquals(1, TestUtils.getInventory(res, "midnight_armour").size());
    }

    @Test
    @Tag("17-7")
    @DisplayName("Test if midnight armour gives protection or not")
    public void testMidnightArmourStats() throws IllegalArgumentException, InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_a_armourprotection", "c_a_armourtest");

        assertTrue(TestUtils.getGoals(res).contains(":enemies"));
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.build("midnight_armour");
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        // Since player's health is 5 without armour
        // Player would have been killed already after battling 2 mercenaries
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @Tag("17-8")
    @DisplayName("Test mercenary mind control behaviour")
    public void testMercenaryMindControl() throws IllegalArgumentException, InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_a_mindcontrol", "c_a_squishyplayer");
        String merc = TestUtils.getEntities(res, "mercenary").get(0).getId();

        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.build("sceptre");
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        // mind controlled
        res = dmc.interact(merc);

        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.LEFT);
        assertEquals(1, TestUtils.getEntities(res, "player").size());

        // player is now dead
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getEntities(res, "player").size());
    }

    @Test
    @Tag("17-9")
    @DisplayName("Midnight armour unable to build when zombies are present in map")
    public void armourCraftUnavailable() throws IllegalArgumentException, InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_a_armourunable", "c_a_default");
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertThrows(InvalidActionException.class, () -> dmc.build("midnight_armour"));
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "sword").size());
    }

    @Test
    @Tag("17-10")
    @DisplayName("Invalid sceptre build")
    public void invalidSceptre() throws IllegalArgumentException, InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        dmc.newGame("d_a_sceptrebuild_1", "c_a_default");
        assertThrows(InvalidActionException.class, () -> dmc.build("sceptre"));
    }

    @Test
    @Tag("17-11")
    @DisplayName("Test mercenary mind control behaviour, sceptre does not break after one use")
    public void testMercenaryMindControlSub() throws IllegalArgumentException, InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_a_mindcontrol", "c_a_squishyplayer_sub");
        String merc = TestUtils.getEntities(res, "mercenary").get(0).getId();

        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.build("sceptre");
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        // mind controlled
        res = dmc.interact(merc);

        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.LEFT);
        assertEquals(1, TestUtils.getEntities(res, "player").size());

        // player is now dead
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getEntities(res, "player").size());
    }

    @Test
    @Tag("17-12")
    @DisplayName("Sun stone opens three doors")
    public void testSunStoneOpenDoor() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_a_sunstone_door", "c_a_squishyplayer_sub");
        res = dmc.tick(Direction.UP);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        res = dmc.tick(Direction.UP);
        assertEquals(TestUtils.getPlayerPos(res).getX(), 0);
        assertEquals(TestUtils.getPlayerPos(res).getY(), 0);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(TestUtils.getPlayerPos(res).getX(), 2);
        assertEquals(TestUtils.getPlayerPos(res).getY(), 0);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
    }
}
