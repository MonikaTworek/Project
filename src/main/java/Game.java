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
     * Obecna ilość kamieni czarnego gracza
     */
    private int currentStonesB;

    /**
     * Obecna ilość kamieni białego gracza
     */
    private int currentStonesW;

    /**
     * Ilość jeńcow białego gracza
     */
    private int capturedByW;

    /**
     * Ilość jeńców czarnego gracza
     */
    private int capturedByB;

    /**
     * Macierz kamieni (plansza)
     */
    private Stone[][] board;

    /**
     * Macierz planszy w konsoli
     */
    private char[][] consoleBoard;

    /**
     * Dwuwymiarowa tablica grup
     */
    private int[][] groupsBoard;

    /**
     * Wielowymiarowa tablica przechowująca kopię zapasową planszy (potrzebne do sprawdzania KO)
     */
    private int[][][] historyBoard;

    /**
     * Tablica pukntów terytorium
     */
    private char[][] territoryPointsBoard;

    /**
     * Stos wykonanych ruchów
     */
    private int[] movesStack;
    /**
     * Numer poprzedniej grupy
     */
    private int lastGroup;

    /**
     * Gracz biały
     */
    private boolean white;

    /**
     * Gracz czarny
     */
    private boolean black;

    /**
     * Obecny indeks ruchu
     */
    private int index;

    /**
     * Obecny ruch
     */
    private PlayerColor currentMove;

    /**
     * Obecny stan gry
     */
    private boolean activePlay;

    private String lastMove;

    /**
     * Inicjuje grę.
     *
     * @param dimension wielkość planszy - dopuszczalne 9, 13, 19
     */
    Game(int dimension) {
        dim = dimension;

        // Ustawia ilość kamieni na podstawie wielkości planszy
        setTotalStones(dim);
        currentStonesB = totalStonesB;
        currentStonesW = totalStonesW;
        capturedByB = 0;
        capturedByW = 0;

        board = new Stone[dim][dim];
        groupsBoard = new int[dim][dim];
        consoleBoard = new char[dim][dim];
        territoryPointsBoard = new char[dim][dim];
        currentMove = PlayerColor.BLACK;

        /*
		* Przechowuje historię stanu po wprowadzaniu kamieni ([indeks][lokalizacja][lokalizacja],
		* gdzie -1 czarny, 1 biały, 0 NULL.
		*/
        historyBoard = new int[dim * dim][dim][dim];
        movesStack = new int[3];

        index = 0;

        // Stwarza początkową czystą planszę konsolową
        consoleMatchPrinter(dim);

        activePlay = true;
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
     * Inicjuje macierz consoleBoard, która będzie służyła do wyświetlania obecnej sytuacji
     * na planszy w konsoli
     * @param dim wymiar planszy
     */
    private void consoleMatchPrinter(int dim) {
        for (int i = 0; i < dim; i++)
            for (int j = 0; j < dim; j++) {
                if (dim == 9) {
                    if (((i % 2) == 0) && ((i % 4) != 0) && ((j % 2) == 0) && ((j % 4) != 0))
                        consoleBoard[i][j] = '+';
                    else
                        consoleBoard[i][j] = '.';
                }
                else if (dim == 19){
                    if (((i % 3) == 0) && ((i % 6) != 0) && ((j % 3) == 0) && ((j % 6) != 0))
                        consoleBoard[i][j] = '+';
                    else
                        consoleBoard[i][j] = '.';
                }
                else {
                    if (((i % 3) == 0) && ((i % 2) != 0) && ((j % 3) == 0) && ((j % 2) != 0) || (i == 6 && j == 6))
                        consoleBoard[i][j] = '+';
                    else
                        consoleBoard[i][j] = '.';
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
    Stone updateBoard(PlayerColor stoneColor, int x, int y, String insert) {
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

            updateBoardHistory();

            if(koChecker(index)) {
                System.out.println(">> Ruch niedozwolony: powtórzone KO.");
                historyBoard[index][x][y] = 0;
                board[x][y] =  null;
                newStone = null;
            }
            else {
                board[x][y] = newStone;

                //próba samobójcza
                if(libertiesOfGroup(actualGroup) == 0) {
                    int[] groupsWithoutLiberties = groupsToKill(actualGroup, newStone.getColor());
                    if(groupsWithoutLiberties[0] == 0) {
                        System.out.println(">> Ruch niedozwolony: próba samobójcza");
                        board[x][y] = null;
                        groupsBoard[x][y] = 0;
                        newStone = null;
                    }
                    else {
                        if (groupsWithoutLiberties[0] >= 1) {
                            System.out.println(">> Wzięto do niewoli (sytuacja niejednoznaczna)");

                            for(int i =  0; i  <= groupsWithoutLiberties[0]; i++)
                                killGroup(groupsWithoutLiberties[i], newStone.getColor());
                            if(newStone.getColor() == PlayerColor.WHITE)
                                currentStonesW--;
                            else
                                currentStonesB--;

                            changeTurn();
                        }
                    }
                }
                else {
                    // Zmienne pomocnicze
                    int tmpW = capturedByW, tmpB = capturedByB;
                    captureChecker(actualGroup, newStone);
                    if(tmpW != capturedByB || tmpB != capturedByW)
                        System.out.println(">> Wzięto do niewoli");
                    if(newStone.getColor() == PlayerColor.WHITE)
                        currentStonesW--;
                    else
                        currentStonesB--;
                    changeTurn();
                }
            }

            if (newStone != null) {
                updateConsoleBoard(newStone);
                updateGroupBoard(newStone);
                updateCurrentIndex(true);
                updateMovesStack(1);
                lastMove = insert;
            }

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
    boolean positionIsFree(int x, int y) {
        return (board[x][y] == null);
    }

    /**
     * Aktualizuje macierz consoleBoard poprzez wstawienie w odpowiednim miejscu oznaczenia kamienia (w zależności
     * od jego koloru)
     * @param stone wstawiony kamień
     */
    private void updateConsoleBoard(Stone stone) {
        if (stone.getColor() == PlayerColor.WHITE)
            consoleBoard[stone.getX()][stone.getY()] = 'W';
        else if (stone.getColor() == PlayerColor.BLACK)
            consoleBoard[stone.getX()][stone.getY()] = 'B';
    }

    /**
     * Metoda zwraca numer grupy, która sąsiaduje z przekazanym kamieniem
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
                        groupsBoard[i][j] = actualGroup;
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
                    historyBoard[index][i][j] = 0;
                else if(board[i][j].getColor() == PlayerColor.BLACK)
                    historyBoard[index][i][j] = -1;
                else if(board[i][j].getColor() == PlayerColor.WHITE)
                    historyBoard[index][i][j] = 1;
            }
    }

    /**
     * Metoda kontroluje, czy gracz nie próbuje przekroczyć 'zasady ko'.
     * @param index indeks ruchu
     * @return <code>true</code>, jeżeli podjęto próbę złamania reguły,
     * <code>false</code> - w przeciwnym razie
     */
    private boolean koChecker(int index) {
        if(index >= 2) {
            for(int i = 0; i < dim; i++)
                for(int j = 0; j < dim; j++)
                    if(historyBoard[index][i][j] != historyBoard[index-1][i][j])
                        return false;
        }
        else
            return false;

        return true;
    }

    /**
     * Zlicza oddechy grupy o podanym numerze
     * @param group numer grupy
     * @return ilość 'oddechów' grupy
     */
    int libertiesOfGroup(int group) {
        boolean[][] libertyMatrix = new boolean[dim][dim];
        int counter = 0;

        for(int i = 0; i < dim; i++)
            for(int j = 0; j < dim; j++)
                if(board[i][j] != null && board[i][j].getGroup() == group)
                    counter += stoneLibertiesInGroup(i, j, libertyMatrix);
        return counter;
    }

    /**
     * Metoda zlicza ilośś oddechów kamienia w grupie. Bazuje na dodatkowej macierzy logicznej,
     * nie zlicza powtórnie jednego oddechu.
     * @param x współrzędna x kamienia
     * @param y współrzędna y kamienia
     * @param libMatrix maciaerz typu logicznego, startowo wypełniona wartościami false
     * @return ilość oddechów z perspektywy jednego kamienia w grupie
     */
    private int stoneLibertiesInGroup(int x, int y, boolean[][] libMatrix) {
        int counter = 0;

        // Warunki z każdym z czterech kierunków zapobiegają wyjściu poza planszę
        if(isValidLine(x, Direction.NORTH) && positionIsFree(x-1, y) && !libMatrix[x-1][y]) {
            counter++;
            libMatrix[x-1][y] = true;
        }
        if(isValidLine(x, Direction.SOUTH) && positionIsFree(x+1, y) && !libMatrix[x+1][y]) {
            counter++;
            libMatrix[x+1][y] = true;
        }
        if(isValidLine(y, Direction.WEST) && positionIsFree(x, y-1) && !libMatrix[x][y-1]) {
            counter++;
            libMatrix[x][y-1] = true;
        }
        if(isValidLine(y, Direction.EAST) && positionIsFree(x, y+1) && !libMatrix[x][y+1]) {
            counter++;
            libMatrix[x][y+1] = true;
        }
        return counter;
    }

    private int[] groupsToKill(int group, PlayerColor color) {
        int counter[] = new int[dim*dim];
        counter[0] = 0;
        int c = 0, liberties;
        boolean[] groups = new boolean[group+1];

        for(int i = 0; i < dim; i++)
            for(int j = 0; j < dim; j++)
                if(board[i][j] != null) {
                    if(board[i][j].getColor() != color) {
                        liberties = libertiesOfGroup(board[i][j].getGroup());
                        if(liberties == 0 && !groups[board[i][j].getGroup()] && board[i][j].getGroup() != group) {
                            counter[0]++;
                            counter[++c] = board[i][j].getGroup();
                            groups[board[i][j].getGroup()] = true;
                        }
                    }
                }
                return counter;
    }

    /**
     * Metoda negująca obecną wartość zmiennej - zmiana tury
     */
    void changeTurn() {
        if(currentMove == PlayerColor.BLACK)
            currentMove = PlayerColor.WHITE;
        else
            currentMove = PlayerColor.BLACK;
    }

    PlayerColor skipMove() {
        changeTurn();
        updateCurrentIndex(true);
        updateMovesStack(-1);
        lastMove = "skip";
        return getMove();
    }

    /**
     * Metoda usuwająca z planszy kamienie zbitej grupy, aktualizuje plansze
     * @param group usuwana grupa
     * @param color kolor kamieni
     */
    private void killGroup(int group, PlayerColor color) {
        for(int i = 0; i < dim; i++)
            for(int j = 0; j < dim; j++)
                if(board[i][j] != null && board[i][j].getGroup() == group) {
                    //Zwiększenie ilości jeńców
                    if(color == PlayerColor.WHITE)
                        capturedByB++;
                    else
                        capturedByW++;
                    groupsBoard[i][j] = 0;
                    board[i][j] = null;
                    consoleBoard[i][j] = '.';
                }
    }

    private void captureChecker(int actualGroup, Stone stone) {
        int north = getAdjacentGroup(stone, false, Direction.NORTH);
        int south = getAdjacentGroup(stone, false, Direction.SOUTH);
        int west = getAdjacentGroup(stone, false, Direction.WEST);
        int east = getAdjacentGroup(stone, false, Direction.EAST);

        if(north != actualGroup && libertiesOfGroup(north) == 0)
            killGroup(north, stone.getColor());
        if(south != actualGroup && libertiesOfGroup(south) == 0)
            killGroup(south, stone.getColor());
        if(west != actualGroup && libertiesOfGroup(west) == 0)
            killGroup(west, stone.getColor());
        if(east != actualGroup && libertiesOfGroup(east) == 0)
            killGroup(east, stone.getColor());
    }

    /**
     * Wstawia numer grupy do macierzy grup
     * @param stone kamień
     */
    private void updateGroupBoard(Stone stone) {
        groupsBoard[stone.getX()][stone.getY()] = stone.getGroup();
    }

    /**
     * Zwiększa obecny numer indeksu jeżeli przekazano true. W przeciwnym razie - zmniejsza
     * @param isOkey zmienna decydująca o zwiększeniu/zmniejszeniu indeksu
     */
    private void updateCurrentIndex(boolean isOkey) {
        if(isOkey)
            index++;
        else
            index--;
    }

    private boolean updateMovesStack(int value) {
        if(value == 1 || value == -1) {
            if(movesStack[0] == 1) {
                movesStack[2] = value;
                movesStack[0] = 2;
            }
            else {
                movesStack[1] = value;
                movesStack[0] = 1;
            }
            return true;
        }
        else
            return false;
    }

    boolean currentGame() {
        boolean end;
        if(activePlay) {
            if(movesStack[1] == -1 && movesStack[2] == -1) {
                System.out.println(">> Partia zakończona: spasowanie");
                end = false;
                activePlay = false;
            }
            else if(currentStonesW == 0 && currentStonesB == 0) {
                System.out.println(">> Partia zakończona: brak kamieni");
                end = false;
                activePlay = false;
            }
            else
                end = true;

            // Gra zakończyła się - podliczenie wyniku
            if(!end) {
                System.out.println(">> Obliczanie wyniku...");
//                int winner = foundWinner();

//                if(winner >= 0)
//                    System.out.println(">> Zwycięzca: Biały");
//                else if (winner == -1)
//                    System.out.println(">> Zwycięzca: Czarny");
            }
            return end;
        }
        else
            return false;
    }

//    private int foundWinner() {
//        int[] territoryPoints;
//
//        for(int i = 0; i < dim; i++)
//            for(int j = 0; j < dim; j++) {
//                if(board[i][j] != null && territoryPointsBoard[i][j] == 0) {
//                    territoryPoints =
//                }
//            }
//    }

//    private int[] function(int x, int y) {
//        int[] emptyPoints = new int[2];
//        int count = countIntersections(x, y, 0, )
//    }
//
//    private int countIntersections(int x, int y, int counter, PlayerColor color) {
//        int[] exp;
//        exp =
//    }

    /**
     * Drukuje w konsoli obecny wygląd macierzy
     */
    void showBoard() {
        int lines = 0;
        char[][] matrix = consoleBoard;

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

    PlayerColor getMove() {
        return currentMove;
    }

    Stone[][] getBoard() {
        return board;
    }

    char[][] getTerritoryPointsBoard() {
        return territoryPointsBoard;
    }
}