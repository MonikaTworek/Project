import org.junit.Test;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

public class GameTest {
    @Test
    public void testCreateNew19x19Game() {
        int dim = 19;
        Game newGame = createNewGame(dim);
        assertNotNull("Game is not exist", newGame);
    }

    @Test
    public void testNumberOfTotalBlackStones() {
        int dim = 9;
        Game newGame = createNewGame(dim);
        assertEquals("Invalid number of black stones", 80, newGame.getTotalStonesB());
        assertEquals("Invalid number of white stones", 80, newGame.getTotalStonesW());
    }

    @Test
    public void testShow13x13BoardInConsole() {
        int dim = 13;
        Game newGame = createNewGame(dim);
        newGame.showBoard();
    }

    @Test
    public void testAddingBlackStoneToBoard() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        int dim = 19;
        Game newGame = createNewGame(dim);
        newGame.updateBoard(PlayerColor.BLACK, 0, 3, "");
        assertNull(newGame.updateBoard(PlayerColor.WHITE, 0, 3, ""));
        String expectedValue = ">> Pozycja zajęta\n";
        assertEquals(outputStream.toString(), expectedValue);
    }

    @Test
    public void testAddingWhiteStoneOutOfRange() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        int dim = 9;
        Game newGame = createNewGame(dim);
        assertNull(newGame.updateBoard(PlayerColor.WHITE, -1, -1, ""));
        String expectedValue = ">> Nieprawidłowy zakres\n";
        assertEquals(outputStream.toString(), expectedValue);
    }

    @Test
    public void testLineRows() {
        int dim = 9;
        Game newGame = createNewGame(dim);
        Point point = new Point(0, 3);
        assertEquals(false, newGame.isValidLine(point.x, Direction.NORTH));
        assertEquals(true, newGame.isValidLine(point.y, Direction.WEST));
        assertEquals(true, newGame.isValidLine(point.x, Direction.SOUTH));
        assertEquals(true, newGame.isValidLine(point.y, Direction.EAST));
    }

    @Test
    public void testGetAdjacentGroup() {
        int dim = 9;
        Game newGame = createNewGame(dim);
        Stone stone1 = newGame.updateBoard(PlayerColor.BLACK, 1, 1, "");
        Stone stone2 = newGame.updateBoard(PlayerColor.BLACK, 0, 1, "");
        newGame.updateBoard(PlayerColor.WHITE, 2, 1, "");
        newGame.updateBoard(PlayerColor.WHITE, 1, 2, "");
        newGame.showBoard();
        assertEquals(2, newGame.getAdjacentGroup(stone1, true, Direction.NORTH));
        assertEquals(3, newGame.getAdjacentGroup(stone1, false, Direction.SOUTH));
        assertEquals(-1, newGame.getAdjacentGroup(stone1, true, Direction.WEST));
        assertEquals(-1, newGame.getAdjacentGroup(stone1, true, Direction.EAST));
        assertEquals(-1, newGame.getAdjacentGroup(stone2, true, Direction.NORTH));
    }

    @Test
    public void testGroupLiberties() {
        int dim = 19;
        Game newGame = createNewGame(dim);
        Stone stone1 = newGame.updateBoard(PlayerColor.BLACK, 1, 1, "");
        newGame.updateBoard(PlayerColor.WHITE, 0, 1, "");
        newGame.updateBoard(PlayerColor.WHITE, 2, 1, "");
        newGame.updateBoard(PlayerColor.WHITE, 1, 2, "");
        newGame.showBoard();
        assertEquals(1, newGame.libertiesOfGroup(stone1.getGroup()));
    }

    @Test
    public void testKillGroup() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        int dim = 9;
        Game newGame = createNewGame(dim);
        Stone stone1 = newGame.updateBoard(PlayerColor.BLACK, 1, 2, "");
        Stone stone2 = newGame.updateBoard(PlayerColor.BLACK, 1, 1, "");
        newGame.updateBoard(PlayerColor.WHITE, 0, 2, "");
        newGame.updateBoard(PlayerColor.WHITE, 2, 2, "");
        newGame.updateBoard(PlayerColor.WHITE, 1, 3, "");
        newGame.updateBoard(PlayerColor.WHITE, 1, 0, "");
        newGame.updateBoard(PlayerColor.WHITE, 0, 1, "");
        newGame.updateBoard(PlayerColor.WHITE, 2, 1, "");
        assertTrue(newGame.positionIsFree(stone1.getX(), stone1.getY()));
        assertTrue(newGame.positionIsFree(stone2.getX(), stone2.getY()));
        String expectedValue = ">> Wzięto do niewoli\n";
        assertEquals(outputStream.toString(), expectedValue);
    }

    @Test
    public void testPreventSuicide() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        int dim = 9;
        Game newGame = createNewGame(dim);
        newGame.updateBoard(PlayerColor.BLACK, 0, 1, "");
        newGame.updateBoard(PlayerColor.BLACK, 1, 0, "");
        newGame.updateBoard(PlayerColor.BLACK, 1, 2, "");
        newGame.updateBoard(PlayerColor.BLACK, 2, 1, "");
        newGame.updateBoard(PlayerColor.WHITE, 1, 1, "");
        String expectedValue = ">> Ruch niedozwolony: próba samobójcza\n";
        assertEquals(outputStream.toString(), expectedValue);
    }

    private Game createNewGame(int dim) {
        return new Game(dim);
    }
}