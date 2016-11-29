import java.io.Serializable;

class Game implements Serializable {
    /**
     * Wymiar planszy
     */
    private int dim;

    /**
     * Obecna ilość czarnych kamieni
     */
    private int currentStonesB;

    /**
     * Obecna ilość białych kamieni
     */
    private int currentStonesW;

    /**
     * Całkowita ilość czarnych kamieni
     */
    private static int totalStonesB;

    /**
     * Całkowita ilość białych kamieni
     */
    private static int totalStonesW;

    /**
     * Macierz kamieni (plansza)
     */
    private Stone[][] board;

    /**
     * Macierz planszy w konsoli
     */
    private char[][] boardConsole;

    /**
     * Dwuwymiarowa tablica grup
     */
    private int[][] boardGroups;

    /**
     * Numer poprzedniej grupy
     */
    private int lastGroup;

    /**
     * Inicjuje grę.
     *
     * @param dimension wielkość planszy - dopuszczalne 9, 13, 19
     */
    Game(int dimension) {
        // ustawia rozmiar planszy
        dim = dimension;

        // Ustawia ilość kamieni na podstawie wielkości planszy
        setTotalStones(dim);

        // Obecne czarne kamienie
        currentStonesB = totalStonesB;

        // Obecne białe kamienie
        currentStonesW = totalStonesW;

        // Dwuwymiarowa tablica kamieni
        board = new Stone[dim][dim];

        // Matryca planszy
        boardConsole = new char[dim][dim];

        // Stwarza początkową czystą planszę konsolową
        consoleMatchPrinter(dim);
    }

    /**
     * Metoda ustawiająca ilość kamieni graczy zależne od rozmiaru planszy.
     * @param dimension oznacza wymiar planszy
     */
    private void setTotalStones(int dimension) {
        totalStonesW = dimension*dimension - 1;
        totalStonesB = dimension* dimension - 1;
    }

    /**
     * Inicjuje macierz boardConsole, która będzie służyła do wyświetlania obecnej sytuacji
     * na planszy w konsoli
     * @param dim wymiar planszy
     */
    private void consoleMatchPrinter(int dim) {
        for (int i = 0; i < dim; i++)
            for (int j = 0; j < dim; j++) {
                if (dim == 9) {
                    if (((i % 2) == 0) && ((i % 4) != 0) && ((j % 2) == 0) && ((j % 4) != 0))
                        boardConsole[i][j] = '+';
                    else
                        boardConsole[i][j] = '.';
                }
                else if (dim == 19){
                    if (((i % 3) == 0) && ((i % 6) != 0) && ((j % 3) == 0) && ((j % 6) != 0))
                        boardConsole[i][j] = '+';
                    else
                        boardConsole[i][j] = '.';
                }
                else {
                    if (((i % 3) == 0) && ((i % 2) != 0) && ((j % 3) == 0) && ((j % 2) != 0) || (i == 6 && j == 6))
                        boardConsole[i][j] = '+';
                    else
                        boardConsole[i][j] = '.';
                }
            }
    }

    /**
     * Metoda wywołująca różne metody kontroli wstawiania kamienia. Po sprawdzeniu, że punkt (x,y) jest poprawny,
     * tworzy obiekt main.java.Stone i umieszcza go w (X,Y) planszy. Zwraca obiekt kamienia wpisanego lub NULL
     *
     * @param stoneColor oznacza kolor gracza, który położył kamień
     * @param x oznacza współrzędną X na planszy
     * @param y oznacza współrzędną Y na planszy
     * @return oznacza obiekt wstawionego kamienia, jeżeli pomyślnie lub NULL w przypadku niepowodzenia
     */
    Stone updateBoard(PlayerColor stoneColor, int x, int y) {
        // Sprawdź wprowadzone współrzędne
        if(!validBoardRange(x, y)) {
            System.out.println(">> Nieprawidłowy zakres");
            return (null);
        }
        //Sprawdź, czy pozycja jest wolna
        else if(!isPositionFree(x, y)) {
            System.out.println(">> Pozycja zajęta");
            return(null);
        }
        else {
            // Tworzy nowy kamień i wstawia do gry
            Stone newStone = new Stone(x, y, stoneColor, ++lastGroup);
            int actualGroup = newStone.getGroup();

//            int northNeighbour =

            // Wstaw kamień na planszę
            board[x][y] = newStone;

            if (newStone != null) {
                // Aktualizuje shell (tablicę char)
                updateBoardShell(newStone);
            }

            // Zwraca wstawiony kamień lub null, jeżeli kamień nie został wstawiony
            return(newStone);
        }
    }

    /**
     * Sprawdza, czy współrzędne (x,y) mieszczą się w obrębie planszy
     * @param x współrzędna X planszy
     * @param y współrzędna Y planszy
     * @return true, jeżli punkt (x,y) mieści się w planszy, w przeciwym wypadku false
     */
    private boolean validBoardRange(int x, int y) {
        return ((x < dim) && (x > -1) && (y < dim) && (y > -1));
    }

    /**
     * Sprawdza, czy pole na planszy jest niezajęte. Zakłada, że punkt (x,y) znajduje się w obrębie planszy.
     * @param x współrzędna X planszy
     * @param y współrzędna Y planszy
     * @return true, jeżeli pole jest wolne, false - w przeciwynm wypadku
     */
    private boolean isPositionFree(int x, int y) {
        return (board[x][y] == null);
    }

    /**
     * Aktualizuje macierz boardConsole poprzez wstawienie w odpowiednim miejscu oznaczenia kamienia (w zależności
     * od jego koloru)
     * @param stone wstawiony kamień
     */
    private void updateBoardShell(Stone stone) {
        if (stone.getColor() == PlayerColor.WHITE)
            boardConsole[stone.getX()][stone.getY()] = 'W';
        else if (stone.getColor() == PlayerColor.BLACK)
            boardConsole[stone.getX()][stone.getY()] = 'B';
    }

//    private int getAdjacentGroup(Stone stone, boolean sameColor, Direction direction) {
//        int adjGroup;
//        PlayerColor stoneColor = stone.getColor();
//
//        if(stoneColor == PlayerColor.WHITE) {
//            if(direction == Direction.NORTH) {
//            }
//        }
//    }

    boolean checkIfNotLineRow(int n, Direction direction) {
        if(direction.equals(Direction.NORTH) || direction.equals(Direction.WEST))
            return (n > 0);
        //przypadek dla "South" i "East"
        else
            return (n < dim - 1);
    }

    /**
     * Drukuje w konsoli obecny wygląd macierzy
     */
    void showBoard() {
        int lines = 0;
        char[][] matrix = boardConsole;

        String string19 = "     A B C D E F G H I J K L M N O P Q R S\n";
        String string13 = "     A B C D E F G H I J K L M\n";
        String string9 = "     A B C D E F G H I\n";
        String letters = "";
        if(dim == 19)
            letters = string19;
        else if (dim == 9)
            letters = string9;
        else
            letters = string13;

        System.out.print("\n" + letters);

        for (char[] m : matrix)
            for (int j = 0; j < matrix.length; j++) {
                if (j == 0) {
                    if (lines < 10) {
                        System.out.print("  " + lines + " ");
                        lines++;
                    }
                    else {
                        System.out.print(" " + lines + " ");
                        lines++;
                    }
                }
                if (j == matrix.length - 1)
                    System.out.println(" " + m[j] + "  " + (lines - 1));
                else
                    System.out.print(" " + m[j]);
            }
        System.out.print(letters);
    }

    int getTotalStonesB() {
        return totalStonesB;
    }

    int getTotalStonesW() {
        return totalStonesW;
    }
}