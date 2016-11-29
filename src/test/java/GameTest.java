import org.junit.Test;

import java.awt.*;
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
        int dim = 19;
        Game newGame = createNewGame(dim);
        newGame.updateBoard(PlayerColor.BLACK, 0, 3);
        newGame.showBoard();
        assertNull(newGame.updateBoard(PlayerColor.WHITE, 0, 3));
    }

    @Test
    public void testAddingWhiteStoneOutOfRange() {
        int dim = 9;
        Game newGame = createNewGame(dim);
        assertNull(newGame.updateBoard(PlayerColor.WHITE, -1, -1));
        newGame.showBoard();
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
        Game newGame = new Game(dim);
        Stone stone1 = newGame.updateBoard(PlayerColor.BLACK, 1, 1);
        Stone stone2 = newGame.updateBoard(PlayerColor.BLACK, 0, 1);
        newGame.updateBoard(PlayerColor.WHITE, 2, 1);
        newGame.updateBoard(PlayerColor.WHITE, 1, 2);
        newGame.showBoard();
        assertEquals(2, newGame.getAdjacentGroup(stone1, true, Direction.NORTH));
        assertEquals(3, newGame.getAdjacentGroup(stone1, false, Direction.SOUTH));
        assertEquals(-1, newGame.getAdjacentGroup(stone1, true, Direction.WEST));
        assertEquals(-1, newGame.getAdjacentGroup(stone1, true, Direction.EAST));
        assertEquals(-1, newGame.getAdjacentGroup(stone2, true, Direction.NORTH));
    }

    private Game createNewGame(int dim) {
        return new Game(dim);
    }
}