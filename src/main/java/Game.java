import java.io.Serializable;

class Game implements Serializable {
    /**
     * Wymiar planszy
     */
    private int dim;

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
     * Wielowymiarowa tablica przechowująca kopię zapasową planszy (potrzebne do sprawdzania KO)
     */
    private int[][][] boardHistory;

    /**
     * Numer poprzedniej grupy
     */
    private int lastGroup;

    /**
     * Obecny indeks ruchu
     */
    private int index;

    /**
     * Inicjuje grę.
     *
     * @param dimension wielkość planszy - dopuszczalne 9, 13, 19
     */
    Game(int dimension) {
        dim = dimension;

        // Ustawia ilość kamieni na podstawie wielkości planszy
        setTotalStones(dim);

        board = new Stone[dim][dim];

        boardGroups = new int[dim][dim];

        boardConsole = new char[dim][dim];

        /*
		* Przechowuje historię stanu po wprowadzaniu kamieni ([indeks][lokalizacja][lokalizacja],
		* gdzie -1 czarny, 1 biały, 0 NULL.
		*/
        boardHistory = new int[dim * dim][dim][dim];

        index = 0;

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
        if(!isInsideBoardRange(x, y)) {
            System.out.println(">> Nieprawidłowy zakres");
            return (null);
        }
        //Sprawdź, czy pozycja jest wolna
        else if(!positionIsFree(x, y)) {
            System.out.println(">> Pozycja zajęta");
            return(null);
        }
        else {
            Stone newStone = new Stone(x, y, stoneColor, ++lastGroup);
            int actualGroup = newStone.getGroup();

            //Inicjalizacja sasiadów danego kamienia
            int northNeighbour = getAdjacentGroup(newStone, true, Direction.NORTH);
            int southNeighbour = getAdjacentGroup(newStone, true, Direction.SOUTH);
            int westNeighbour = getAdjacentGroup(newStone, true, Direction.WEST);
            int eastNeighbour = getAdjacentGroup(newStone, true, Direction.EAST);

            updateGroups(actualGroup, northNeighbour, southNeighbour, westNeighbour, eastNeighbour);

            // Wstaw kamień na planszę
            board[x][y] = newStone;

            // Dodaj wpis do kopii zapasowej
            updateBoardHistory();

            if (newStone != null)
                updateBoardShell(newStone);

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
    private boolean isInsideBoardRange(int x, int y) {
        return ((x < dim) && (x > -1) && (y < dim) && (y > -1));
    }

    /**
     * Sprawdza, czy pole na planszy jest niezajęte. Zakłada, że punkt (x,y) znajduje się w obrębie planszy.
     * @param x współrzędna X planszy
     * @param y współrzędna Y planszy
     * @return true, jeżeli pole jest wolne, false - w przeciwynm wypadku
     */
    private boolean positionIsFree(int x, int y) {
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

    /** Metoda zwraca numer grupy, która sąsiaduje z przekazanym kamieniem
     * @param stone kamień, którego sąsiada poszukujemy
     * @param hasSameColor oznacza, czy szukamy grupy w kolorze zgodnym z kolorem kamienia
     * @param direction kierunek
     * @return poprawny numer grupy (jeżeli istnieje), -100 - jezeli kierunek podano nieprawidłowo,
     * -1 - jeżeli brak sąsiada ze wskazanej strony.
     */
    int getAdjacentGroup(Stone stone, boolean hasSameColor, Direction direction) {
        int adjGroup = -100;
        PlayerColor stoneColor = stone.getColor();

        if (hasSameColor) {
            if (direction == Direction.NORTH) {
                if (isValidLine(stone.getX(), direction) && !positionIsFree(stone.getX() - 1, stone.getY()) && (stoneColor == board[stone.getX() - 1][stone.getY()].getColor()))
                    adjGroup = board[stone.getX() - 1][stone.getY()].getGroup();
                else
                    adjGroup = -1;
            } else if (direction == Direction.SOUTH) {
                if (isValidLine(stone.getX(), direction) && !positionIsFree(stone.getX() + 1, stone.getY()) && (stoneColor == board[stone.getX() + 1][stone.getY()].getColor()))
                    adjGroup = board[stone.getX() + 1][stone.getY()].getGroup();
                else
                    adjGroup = -1;
            } else if (direction == Direction.EAST) {
                if (isValidLine(stone.getY(), direction) && !positionIsFree(stone.getX(), stone.getY() + 1) && (stoneColor == board[stone.getX()][stone.getY() + 1].getColor()))
                    adjGroup = board[stone.getX()][stone.getY() + 1].getGroup();
                else
                    adjGroup = -1;
            } else if (direction == Direction.WEST) {
                if (isValidLine(stone.getY(), direction) && !positionIsFree(stone.getX(), stone.getY() - 1) && (stoneColor == board[stone.getX()][stone.getY() - 1].getColor()))
                    adjGroup = board[stone.getX()][stone.getY() - 1].getGroup();
                else
                    adjGroup = -1;
            }
        }
        else {
            if (direction == Direction.NORTH) {
                if (isValidLine(stone.getX(), direction) && !positionIsFree(stone.getX() - 1, stone.getY()) && (stoneColor != board[stone.getX() - 1][stone.getY()].getColor()))
                    adjGroup = board[stone.getX() - 1][stone.getY()].getGroup();
                else
                    adjGroup = -1;
            } else if (direction == Direction.SOUTH) {
                if (isValidLine(stone.getX(), direction) && !positionIsFree(stone.getX() + 1, stone.getY()) && (stoneColor != board[stone.getX() + 1][stone.getY()].getColor()))
                    adjGroup = board[stone.getX() + 1][stone.getY()].getGroup();
                else
                    adjGroup = -1;
            } else if (direction == Direction.EAST) {
                if (isValidLine(stone.getY(), direction) && !positionIsFree(stone.getX(), stone.getY() + 1) && (stoneColor != board[stone.getX()][stone.getY() + 1].getColor()))
                    adjGroup = board[stone.getX()][stone.getY() + 1].getGroup();
                else
                    adjGroup = -1;
            } else if (direction == Direction.WEST) {
                if (isValidLine(stone.getY(), direction) && !positionIsFree(stone.getX(), stone.getY() - 1) && (stoneColor != board[stone.getX()][stone.getY() - 1].getColor()))
                    adjGroup = board[stone.getX()][stone.getY() - 1].getGroup();
                else
                    adjGroup = -1;
            }
        }
        return(adjGroup);
    }

    /**
     * Zmienia grupę wszystkich sąsiednich kamieni tego samego koloru. Nowej grupie przypisywane
     * są kamienie dawnej grupy.
     * @param actualGroup grupa wstawianego kamienia
     * @param n grupa sąsiadująca (góra)
     * @param s grupa sąsiadująca (dół)
     * @param w grupa sąsiadująca (lewo)
     * @param e grupa sąsiadująca (prawo)
     */
    private void updateGroups(int actualGroup, int n, int s, int w, int e) {
        for(int i = 0; i < dim; i++)
            for(int j = 0; j < dim; j++)
                if(board[i][j] != null) {
                    if(board[i][j].getGroup() == n || board[i][j].getGroup() == s || board[i][j].getGroup() == w ||
                            board[i][j].getGroup() == e) {
                        boardGroups[i][j] = actualGroup;
                        board[i][j].setGroup(actualGroup);
                    }
                }
    }

    /**
     * Metoda sprawdzająca, czy z podanej strony(kierunku) może istnieć
     * @param n współrzędna kamienia
     * @param direction kierunek,
     * @return <code>false</code> w przypadku, gdy podana współrzędna kamienia znajduje sie
     * na brzegu planszy, <code>true</code> - w przeciwnym wypadku
     */
    boolean isValidLine(int n, Direction direction) {
        if(direction.equals(Direction.NORTH) || direction.equals(Direction.WEST))
            return (n > 0);
        //przypadek dla "South" i "East"
        else
            return (n < dim-1);
    }

    /**
     * Aktualizacja tablicy z kopię zapasową
     */
    private void updateBoardHistory() {
        for(int i = 0; i < dim; i++)
            for(int j = 0; j < dim; j++) {
                if(board[i][j] == null)
                    boardHistory[index][i][j] = 0;
                else if(board[i][j].getColor() == PlayerColor.BLACK)
                    boardHistory[index][i][j] = -1;
                else if(board[i][j].getColor() == PlayerColor.WHITE)
                    boardHistory[index][i][j] = 1;
            }
    }

    /**
     * Drukuje w konsoli obecny wygląd macierzy
     */
    void showBoard() {
        int lines = 0;
        char[][] matrix = boardConsole;

        String string19 = "     A B C D E F G H I J K L M N O P Q R S\n";
        String letters;
        if(dim == 19)
            letters = string19;
        else if (dim == 13)
            letters = string19.substring(0, 31) + '\n';
        else
            letters = string19.substring(0, 23) + "\n";

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