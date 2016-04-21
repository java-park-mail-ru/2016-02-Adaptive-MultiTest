package mechanics;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Sasha on 21.04.16.
 */
public class MovingTest {
    private static GameSession game;

    @Before
    public void setUp() {
        game = new GameSession(1, 2);
    }

    @Test
    public void testOccupy() {
        assertFalse(game.getAllRed()[1][1]);
        assertFalse(game.getAllBlue()[1][1]);

        Move.occupy(new Coords(1,1), game);

        assertTrue(game.getAllRed()[1][1]);
        assertTrue(game.getAllBlue()[1][1]);

        Move.occupy(new Coords(1,1), game);

        assertTrue(game.getAllRed()[1][1]);
        assertTrue(game.getAllBlue()[1][1]);
    }

    @Test
    public void testMove() {
        Coords blueStart = new Coords(4,3);
        Coords redStart = new Coords(3,2);
        game.setLastBlue(blueStart);
        game.setLastRed(redStart);

        Move.occupy(blueStart, game);
        Move.occupy(redStart, game);

        PossibleCourses bluePC = Move.getPossibleCourses(game);
        assertEquals(bluePC.getLeft(), new Coords(3,3));
        assertEquals(bluePC.getTop(), new Coords(4,2));
        assertEquals(bluePC.getRight(), new Coords(5,3));
        assertEquals(bluePC.getBottom(), new Coords(4,4));

        Move.occupy(bluePC.getLeft(), game);

        PossibleCourses redPC = Move.getPossibleCourses(game);
        assertEquals(redPC.getLeft(), new Coords(2,2));
        assertEquals(redPC.getTop(), new Coords(3,1));
        assertEquals(redPC.getRight(), new Coords(4,2));
        assertEquals(redPC.getBottom(), new Coords(-1,-1));

        Move.occupy(redPC.getRight(), game);

        bluePC = Move.getPossibleCourses(game);
        assertEquals(bluePC.getLeft(), new Coords(2,3));
        assertEquals(bluePC.getTop(), new Coords(-1,-1));
        assertEquals(bluePC.getRight(), new Coords(-1,-1));
        assertEquals(bluePC.getBottom(), new Coords(3,4));

        assertTrue(game.getAllBlue()[4][3]);
        assertTrue(game.getAllRed()[4][3]);
        assertTrue(game.getAllBlue()[3][2]);
        assertTrue(game.getAllRed()[3][2]);
        assertTrue(game.getAllBlue()[3][3]);
        assertTrue(game.getAllRed()[3][3]);
        assertTrue(game.getAllBlue()[4][2]);
        assertTrue(game.getAllRed()[4][2]);
    }

    @Test
    public void testRedBorders() {
        Coords start = new Coords(1,1);
        game.setLastBlue(start);
        game.setLastRed(start);

        Move.occupy(start, game);
        PossibleCourses redPC = Move.getPossibleCourses(game);
        assertEquals(redPC.getLeft(), new Coords(-1,-1));
        assertEquals(redPC.getTop(), new Coords(-1,-1));

        Move.occupy(start, game);
        PossibleCourses bluePC = Move.getPossibleCourses(game);
        assertEquals(bluePC.getLeft(), new Coords(0,1));
        assertEquals(bluePC.getTop(), new Coords(1,0));
    }

    @Test
    public void testBlueBorders() {
        Coords start = new Coords(GameSession.FIELD_SIZE - 2, GameSession.FIELD_SIZE - 2);
        game.setLastBlue(start);
        game.setLastRed(start);

        Move.occupy(start, game);
        PossibleCourses redPC = Move.getPossibleCourses(game);
        assertEquals(redPC.getRight(), new Coords(GameSession.FIELD_SIZE - 1, GameSession.FIELD_SIZE - 2));
        assertEquals(redPC.getBottom(), new Coords(GameSession.FIELD_SIZE - 2, GameSession.FIELD_SIZE - 1));

        Move.occupy(start, game);
        PossibleCourses bluePC = Move.getPossibleCourses(game);
        assertEquals(bluePC.getRight(), new Coords(-1,-1));
        assertEquals(bluePC.getBottom(), new Coords(-1,-1));
    }
}
