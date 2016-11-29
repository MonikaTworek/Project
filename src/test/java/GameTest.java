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
        assertEquals(false, newGame.checkIfNotLineRow(point.x, Direction.NORTH));
        assertEquals(true, newGame.checkIfNotLineRow(point.y, Direction.WEST));
        assertEquals(true, newGame.checkIfNotLineRow(point.x, Direction.SOUTH));
        assertEquals(true, newGame.checkIfNotLineRow(point.y, Direction.EAST));
    }

    private Game createNewGame(int dim) {
        return new Game(dim);
    }

}