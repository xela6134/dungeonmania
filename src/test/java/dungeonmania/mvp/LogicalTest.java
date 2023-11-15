package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


public class LogicalTest {
    @Test
    @Tag("18-1")
    @DisplayName("Test XOR")
    public void simpleLightBulbTest() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_a_light_xor", "c_a_default");
        res = dmc.tick(Direction.UP);
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @Tag("18-2")
    @DisplayName("Simple light bulb test with wires")
    public void wiresLightBulbTest() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicSwitchTest_simpleBulbWire", "c_a_default");
        // Initially off
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Now on
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());

        // Now off
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @Tag("18-3")
    @DisplayName("Complex AND light bulb")
    public void complexAndLightBulbTest() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicSwitchTest_complexAnd", "c_a_default");
        // Initially off
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // One switch active, light should still be off
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Second switch active, light should be on
        res = dmc.tick(Direction.DOWN);
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());

        // Third switch active, light should still be on
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());

        // Turn off second and third switch, light should be off
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @Tag("18-4")
    @DisplayName("Exploding bombs with wires")
    public void explodingBombsWithWiresTest() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicSwitchTest_explodingBombs", "c_a_default");
        // Bomb exists
        assertEquals(1, TestUtils.getEntities(res, "bomb").size());
        assertEquals(2, TestUtils.getEntities(res, "wire").size());
        assertEquals(1, TestUtils.getEntities(res, "switch").size());

        // Activate switch and check exploded
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getEntities(res, "bomb").size());
        assertEquals(0, TestUtils.getEntities(res, "wire").size());
        assertEquals(0, TestUtils.getEntities(res, "switch").size());
    }

    @Test
    @Tag("18-5")
    @DisplayName("Testing AND switchdoor is invalid")
    public void switchDoorAndFail() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_a_switchdoor_and_fail", "c_a_default");
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        assertEquals(TestUtils.getPlayerPos(res).getX(), 4);
        assertEquals(TestUtils.getPlayerPos(res).getY(), 2);
    }

    @Test
    @Tag("18-6")
    @DisplayName("Simple OR switch door")
    public void simpleSwitchDoorTest() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_logicSwitchTest_simpleSwitchDoor", "c_a_default");
        EntityResponse initPlayer = TestUtils.getPlayer(initDungonRes).get();

        // Create the expected result
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(6, 2),
                false);

        DungeonResponse actualDungonRes = dmc.tick(Direction.LEFT);
        EntityResponse actualPlayer = TestUtils.getPlayer(actualDungonRes).get();

        // Assert after movement
        assertTrue(TestUtils.entityResponsesEqual(expectedPlayer, actualPlayer));

        // Activate switch to open door
        dmc.tick(Direction.UP);
        dmc.tick(Direction.DOWN);

        // Create the expected result
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(5, 2),
                false);

        actualDungonRes = dmc.tick(Direction.LEFT);
        actualPlayer = TestUtils.getPlayer(actualDungonRes).get();

        // Assert after movement
        assertTrue(TestUtils.entityResponsesEqual(expectedPlayer, actualPlayer));
    }

    @Test
    @Tag("18-7")
    @DisplayName("Testing CO_AND lightbulb fails")
    public void lightBulbCoAndFail() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_a_coand_fails", "c_a_default");
        res = dmc.tick(Direction.UP);
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @Tag("18-8")
    @DisplayName("Testing CO_AND lightbulb works")
    public void lightBulbCoAndWork() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_a_coand_works", "c_a_default");
        res = dmc.tick(Direction.UP);
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @Tag("18-9")
    @DisplayName("Testing CO_AND switchdoor")
    public void lightBulbCoAndSwitchDoor() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_a_coand_switchdoor", "c_a_default");
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.LEFT);
        assertEquals(TestUtils.getPlayerPos(res).getX(), 2);
        assertEquals(TestUtils.getPlayerPos(res).getY(), 1);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        assertEquals(TestUtils.getPlayerPos(res).getX(), 2);
        assertEquals(TestUtils.getPlayerPos(res).getY(), 2);
    }

    @Test
    @Tag("18-10")
    @DisplayName("Wire destruction interferes with bomb detonation")
    public void bombExplosionInterfered() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_a_complexbomb", "c_a_bombradius1");
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.UP);
        assertEquals(1, TestUtils.getEntities(res, "bomb").size());
    }

    @Test
    @Tag("18-11")
    @DisplayName("4 wires for switch door")
    public void fourWireDoor() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_a_coand_switchdoor_4", "c_a_bombradius1");
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getEntities(res, "switch_door").size());
    }

    @Test
    @Tag("18-12")
    @DisplayName("Switch door activated & deactivated with only switch")
    public void switchActivationSwitchDoor() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_a_switchdoor_switch", "c_a_bombradius1");
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getEntities(res, "switch_door").size());
        assertEquals(TestUtils.getPlayerPos(res).getX(), 2);
        assertEquals(TestUtils.getPlayerPos(res).getY(), 2);
    }

    @Test
    @Tag("18-13")
    @DisplayName("Bomb picked up near switch and wire")
    public void bombPickedUpNearElectrics() throws IllegalArgumentException, InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_a_bombpickup", "c_a_bombradius1");
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getEntities(res, "bomb").size());
        assertEquals(1, TestUtils.getInventory(res, "bomb").size());

        res = dmc.tick(TestUtils.getInventory(res, "bomb").get(0).getId());
        assertEquals(1, TestUtils.getEntities(res, "bomb").size());
        assertEquals(0, TestUtils.getInventory(res, "bomb").size());
    }

    @Test
    @Tag("18-14")
    @DisplayName("Unsubscription behaviour for SwitchDoor and LightBulb")
    public void unsubscriptionBehaviour() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_a_unsubscribe", "c_a_bombradius1");
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getEntities(res, "bomb").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "switch_door").size());
    }
}
