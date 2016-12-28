import Basics.Direction;
import Basics.PlayerColor;
import Basics.Stone;

import java.io.Serializable;

/**
 * Klasa czuwająca nad przebiegiem rozgrywki, posiadająca zaimplementowane reguły gry GO.
 */
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
     * Tablica punktów terytorium
     */
    private char[][] territoryPointsBoard;

    /**
     * Stos wykonanych ruchów
     */
    private int[] movesStack;

    /**
     * Tablica ze stanem kamieni (żywy, martwy)
     */
    boolean[][] deadStones;

    /**
     * Ostatnie dwie decyzje podjęte przez graczy w fazie 'Choosing'
     */
    private int[] deadStoneDecision;

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
    private PlayerColor currentPlayer;

    /**
     * Wynik gracza czarnego
     */
    private double blackScore;

    /**
     * Wynik gracza białego
     */
    private double whiteScore;

    /**
     * Punkty terytorium gracza czarnego
     */
    private int blackTerritoryPoints;

    /**
     * Punkty terytorium gracza białego
     */
    private int whiteTerritoryPoints;

    /**
     * Inicjuje grę oraz składowe gry
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
        whiteScore = 0;
        blackScore = 0;

        board = new Stone[dim][dim];
        groupsBoard = new int[dim][dim];
        consoleBoard = new char[dim][dim];
        territoryPointsBoard = new char[dim][dim];
        deadStones = new boolean[dim][dim];
//        for(int i = 0; i < dim; i++)
//            for(int j = 0; j < dim; j++)
//                deadStones[i][j] = false;
        deadStoneDecision = new int[3];
        currentPlayer = PlayerColor.BLACK;
        /*
		* Przechowuje historię stanu po wprowadzaniu kamieni ([indeks][lokalizacja][lokalizacja],
		* gdzie -1 czarny, 1 biały, 0 NULL.
		*/
        historyBoard = new int[dim * dim][dim][dim];
        movesStack = new int[3];
        index = 0;

//         Stwarza początkową czystą planszę konsolową
//        consoleMatchPrinter(dim);
    }

    /**
     * Metoda ustawiająca ilość kamieni graczy zależne od rozmiaru planszy.
     * @param dimension oznacza wymiar planszy
     */
    private void setTotalStones(int dimension) {
        totalStonesW = dimension * dimension / 2 - 1;
        totalStonesB = dimension * dimension / 2 ;
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
     * tworzy obiekt typu Basics.Stone i umieszcza go w (X,Y) planszy. Zwraca obiekt kamienia wpisanego lub NULL
     *
     * @param stoneColor oznacza kolor gracza, który położył kamień
     * @param x oznacza współrzędną X na planszy
     * @param y oznacza współrzędną Y na planszy
     * @return oznacza obiekt wstawionego kamienia, jeżeli pomyślnie lub NULL w przypadku niepowodzenia
     */
    Stone updateBoard(PlayerColor stoneColor, int x, int y) {
        // Sprawdź wprowadzone współrzędne
        if(!isInsideBoardRange(x, y)) {
            System.out.print(">> Nieprawidłowy zakres\n");
            return (null);
        }
        //Sprawdź, czy pozycja jest wolna
        else if(!positionIsFree(x, y)) {
            System.out.print(">> Pozycja zajęta\n");
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
                System.out.print(">> Ruch niedozwolony: powtórzone KO\n");
                historyBoard[index][x][y] = 0;
                board[x][y] =  null;
                newStone = null;
            }
            else {
                board[x][y] = newStone;

                //próba samobójcza
                if(libertiesOfGroup(actualGroup) == 0) {
                    int[] groupsWithoutLiberties = getGroupsToKill(actualGroup, newStone.getColor());
                    if(groupsWithoutLiberties[0] == 0) {
                        System.out.print(">> Ruch niedozwolony: próba samobójcza\n");
                        board[x][y] = null;
                        groupsBoard[x][y] = 0;
                        newStone = null;
                    }
                    else {
                        if (groupsWithoutLiberties[0] >= 1) {
                            System.out.print(">> Wzięto do niewoli (sytuacja niejednoznaczna)\n");
                            for(int i =  1; i  <= groupsWithoutLiberties[0]; i++)
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
                    if(tmpW != capturedByW || tmpB != capturedByB)
                        System.out.print(">> Wzięto do niewoli\n");
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
                updateCurrentIndex();
                updateMovesStack(1);
            }
            return(newStone);
        }
    }

//    public void matrixIntToChar() {
//        int[][] int_mat = groupsBoard;
//        char[][] char_mat = new char[int_mat.length][int_mat.length];
//        for (int i = 0; i < int_mat.length; i++)
//            for (int j = 0; j < int_mat.length; j++)
//                char_mat[i][j] = (char) (int_mat[i][j] + '0');
//
//        int lines = 0;
//        char[][] matrix = char_mat;
//
//        String string19 = "     A B C D E F G H I J K L M N O P Q R S\n";
//        String letters;
//        if(dim == 19)
//            letters = string19;
//        else if (dim == 13)
//            letters = string19.substring(0, 31) + '\n';
//        else
//            letters = string19.substring(0, 23) + "\n";
//
//        System.out.print("\n" + letters);
//        for (char[] m : matrix)
//            for (int j = 0; j < matrix.length; j++) {
//                if (j == 0) {
//                    if (lines < 10) {
//                        System.out.print("  " + lines + " ");
//                        lines++;
//                    }
//                    else {
//                        System.out.print(" " + lines + " ");
//                        lines++;
//                    }
//                }
//                if (j == matrix.length - 1)
//                    System.out.println(" " + m[j] + "  " + (lines - 1));
//                else
//                    System.out.print(" " + m[j]);
//            }
//        System.out.print(letters);
//
//    }
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
    private boolean isValidLine(int n, Direction direction) {
        //przypadek dla "North" i "West"
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

        // Warunki dla czterech kierunków zapobiegają wyjściu poza planszę
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

    /**
     * Metoda tworząca tablicę grup, które należy usunąć z planszy
     * @param group aktualna grupa wstawionego kamienia
     * @param color kolor gracza
     * @return tablica o indeksach odpowiadających kolejnym grupom na planszy; wartość true oznacza grupę,
     * którą należy usunąć
     */
    private int[] getGroupsToKill(int group, PlayerColor color) {
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
     * Metoda usuwająca z planszy kamienie zbitej grupy, aktualizuje planszę
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

    /**
     * Metoda zmieniająca obecną turę.
     */
    void changeTurn() {
        if(currentPlayer == PlayerColor.BLACK)
            currentPlayer = PlayerColor.WHITE;
        else
            currentPlayer = PlayerColor.BLACK;
    }

    /**
     * Metoda kończąca trwającą turę jako spasowaną - zmiana aktywnego gracza, obecnego indeksu
     * @return kolor aktywnego obecnie gracza
     */
    PlayerColor skipMove() {
        changeTurn();
        updateCurrentIndex();
        updateMovesStack(-1);
        return getCurrentPlayer();
    }

    /**
     * Metoda sprawdza, czy w wyniku dostawienia nowego kamienia na planszę konieczne jest usunięcie
     * jakiejś grupy.
     * @param actualGroup aktualna grupa kamienia
     * @param stone dostawiony na plansze kamień
     */
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
     * Zwiększa obecny numer indeksu.
     */
    private void updateCurrentIndex() {
        index++;
    }

    /**
     * Metoda zapamiętująca w tablicy typ ostatnio wykonanego ruchu - 1: wstawiono kamień, -1: spasowano
     * @param value wartość do wstawienia
     */
    private void updateMovesStack(int value) {
        //movesStack[0] - 1, gdy kamień stawiał BLACK, 2, gdy kamień dodał WHITE
        if (currentPlayer == PlayerColor.BLACK) {
            movesStack[2] = value;
            movesStack[0] = 2;
        }
        else {
            movesStack[1] = value;
            movesStack[0] = 1;
        }
    }

    /**
     * Metoda dodająca na stos nową decyzję gracza
     * @param move wykonany przez gracza ruch
     */
    void updateDeadStoneDecision(int move) {
        if (currentPlayer == PlayerColor.WHITE) {
            deadStoneDecision[2] = move;
            deadStoneDecision[0] = 2;
        }
        else {
            deadStoneDecision[1] = move;
            deadStoneDecision[0] = 1;
        }
    }

    /**
     * Metoda sprawdzająca, czy poprzedni użytkownik spasował
     * @return <code>true</code> w przypadku spasowania, <code>false</code> w przeciwnym razie
     */
    boolean ifDoublePass(PlayerColor currentTurn) {
        if(currentTurn == PlayerColor.WHITE)
            return (movesStack[1] == -1 && movesStack[0] == 1);
        else
            return (movesStack[2] == -1 && movesStack[0] == 2);
    }

    /**
     * Metoda sprawdzająca, czy poprzedni użytkownik zgodził się na zakończenie rozgrywki
     * @return <code>true</code> w przypadku zgody, <code>false</code> w przeciwnym razie
     */
    boolean ifDoubleAgree(PlayerColor currentTurn) {
        if(currentTurn == PlayerColor.WHITE)
            return (deadStoneDecision[1] == -1 && deadStoneDecision[0] == 1);
        else
            return (deadStoneDecision[2] == -1 && deadStoneDecision[0] == 2);
    }

    /**
     * Metoda wykonująca
     */
    void endGame() {
        removeDeadStones();
        FindWinner winnerChooser = new FindWinner();
        if(winnerChooser.getWinner() == PlayerColor.WHITE)
            System.out.println(">> Zwycięzca: Biały");
        else
            System.out.println(">> Zwycięzca: Czarny");
    }

    /**
     * Klasa ustalająca zwycięzcę rozgrywki na podstawie zdobytych punktów
     */
    private class FindWinner {
        /**
         * Zwycięzca rozgrywki
         */
        PlayerColor winner;
        /**
         * Pomocnicza macierz z wartościami dla pól z określonym właścicielem terytorium
         */
        private int[][] infoMatrix = new int[dim][dim];

        private FindWinner() {
            int[] territoryPoints;
            double komi = 6.5;

            for(int i = 0; i < dim; i++)
                for(int j = 0; j < dim; j++) {
                    if(board[i][j] == null && territoryPointsBoard[i][j] == 0) {
                        territoryPoints = territoryOwner(i, j);

                        if (territoryPoints[0] == 1)
                            whiteTerritoryPoints += territoryPoints[1];
                        else if(territoryPoints[0] == -1)
                            blackTerritoryPoints += territoryPoints[1];

                        updateTerritoryPointsMatrix(territoryPoints[0]);
                    }
                    else if(board[i][j] != null && territoryPointsBoard[i][j] == 0) {
                        if(board[i][j].getColor() == PlayerColor.WHITE) {
                            territoryPointsBoard[i][j] = 'W';
                        }
                        else
                            territoryPointsBoard[i][j] = 'B';
                    }
                }
            //wyniki
            whiteScore = whiteTerritoryPoints + totalStonesW - currentStonesW - capturedByB + komi;
            blackScore = blackTerritoryPoints + totalStonesB - currentStonesB - capturedByW;

            if(whiteScore < blackScore)
                winner = PlayerColor.BLACK;
            else
                winner = PlayerColor.WHITE;
        }

        /**
         * Metoda zwraca dwuelementową tablicę (gracz wiodący na danym terytorium oraz ilość jego punktów terytorium)
         * dla podanych współrzędnych
         * @param x współrzędna x
         * @param y współrzędna y
         * @return tablica dwuelementowa (gracz, wielkość terytorium)
         */
        private int[] territoryOwner(int x, int y) {
            //Tablica o dwóch elementach: gracz wiodący ('1' - biały, '-1' - czarny) oraz ilość punktów terytorium
            int[] territoryPoints = new int[2];
            //Ilość punktów tego terytorium dla danego gracza
            int count = countCrossings(x, y, 0);

            if (black == white) {
                //terytorium neutralne
                territoryPoints[0] = 2;
                territoryPoints[1] = count;
            }
            if(!black && white) {
                //terytorium gdzie wygrywa biały
                territoryPoints[0] = 1;
                territoryPoints[1] = count;
            }
            else if(black && !white) {
                //terytorium gdzie wygrywa czarny
                territoryPoints[0] = -1;
                territoryPoints[1] = count;
            }
            //wyzerwoanie wartości
            white = false;
            black = false;

            return territoryPoints;
        }

        /**
         * Metoda uzupełniająca tablicę punktów terytorium
         * @param pointsOwner gracz prowadzący na danym terytorium
         *      2 - brak prowadzącego
         *      1 - biały gracz
         *      -1 - czarny gracz
         */
        private void updateTerritoryPointsMatrix(int pointsOwner) {
            for (int i = 0; i < dim; i++)
                for (int j = 0; j < dim; j++) {
                    if (infoMatrix[i][j] == 3) {
                        if (pointsOwner == 2)
                            territoryPointsBoard[i][j] = '.';
                        else if (pointsOwner == 1)
                            territoryPointsBoard[i][j] = 'W';
                        else if (pointsOwner == -1)
                            territoryPointsBoard[i][j] = 'B';
                    }
                }

            for (int i = 0; i < infoMatrix.length; i++)
                for (int j = 0; j < infoMatrix.length; j++)
                    infoMatrix[i][j] = 0;
        }

        /**
         * Metoda ustalająca ilość punktów za terytorium danego gracza
         * @param x oznacza współrzędną x punktu terytorium
         * @param y oznacza współrzędna y punktu terytorium
         * @param counter oznacza ilość punktów terytorium (zapamiętanie dla rekurencji)
         * @return ilość skrzyżowań dla tego terytorium gracza
         */
        private int countCrossings(int x, int y, int counter) {
            int[] neighboursInfo = neighboursInfo(x, y);

            if(infoMatrix[x][y] != 3 && board[x][y] == null) {
                infoMatrix[x][y] = 3;
                counter = countCrossings(x, y, ++counter);
            }
            else {
                //północ
                //jeżeli istnieje ten sąsiad
                if(neighboursInfo[0] != -2) {
                    //jeżeli pole jest puste i nie zostało wcześniej sprawdzone
                    if(neighboursInfo[0] == 0 && infoMatrix[x-1][y] != 3) {
                        infoMatrix[x-1][y] = 3;
                        //rekurencyjne wywołanie dla tego sąsiada
                        counter = countCrossings(x-1, y, ++counter);
                    }
                    //jeżeli na polu stoi kamień biały
                    else if(neighboursInfo[0] == 1)
                        white = true;
                        //jeżeli na polu stoi kamień czarny
                    else if(neighboursInfo[0] == -1)
                        black = true;
                }
                //wschód
                if(neighboursInfo[3] != -2) {
                    if (neighboursInfo[3] == 0 && infoMatrix[x][y + 1] != 3) {
                        infoMatrix[x][y+1] = 3;
                        counter = countCrossings(x, y+1, ++counter);
                    }
                    else if (neighboursInfo[3] == 1)
                        white = true;
                    else if (neighboursInfo[3] == -1)
                        black = true;
                }
                //południe
                if(neighboursInfo[1] != -2) {
                    if (neighboursInfo[1] == 0 && infoMatrix[x+1][y] != 3) {
                        infoMatrix[x+1][y] = 3;
                        counter = countCrossings(x+1, y, ++counter);
                    }
                    else if (neighboursInfo[1] == 1)
                        white = true;
                    else if (neighboursInfo[1] == -1)
                        black = true;
                }
                //północ
                if(neighboursInfo[2] != -2) {
                    if (neighboursInfo[2] == 0 && infoMatrix[x][y-1] != 3) {
                        infoMatrix[x][y-1] = 3;
                        counter = countCrossings(x, y-1, ++counter);
                    }
                    else if (neighboursInfo[2] == 1)
                        white = true;
                    else if (neighboursInfo[2] == -1)
                        black = true;
                }
            }
            return counter;
        }

        /**
         * Metoda tworzy tablicę czteroelenentową (po jednym elemencie dla każdego kierunku) i umieszcza w niej:
         *      -2 - jeżeli nie istnieje ten sąsiad (brzeg)
         *      0 - jeżeli pole jest puste
         *      1 - jeżeli na polu stoi biały kamień
         *      -1 - jeżeli na polu stoi czarny kamień
         * @param x współrzędna x
         * @param y współrzędna y
         * @return tablica z informacjami o sąsiadach danego pola
         */
        private int[] neighboursInfo(int x, int y) {
            int exp[] = new int[4];

            if(!isValidLine(x, Direction.NORTH))
                exp[0] = -2;
            else if(positionIsFree(x-1, y))
                exp[0] = 0;
            else if(board[x-1][y].getColor() == PlayerColor.WHITE)
                exp[0] = 1;
            else
                exp[0] = -1;

            if(!isValidLine(x, Direction.SOUTH))
                exp[1] = -2;
            else if(positionIsFree(x+1, y))
                exp[1] = 0;
            else if(board[x+1][y].getColor() == PlayerColor.WHITE)
                exp[1] = 1;
            else
                exp[1] = -1;

            if(!isValidLine(y, Direction.WEST))
                exp[2] = -2;
            else if(positionIsFree(x, y-1))
                exp[2] = 0;
            else if(board[x][y-1].getColor() == PlayerColor.WHITE)
                exp[2] = 1;
            else
                exp[2] = -1;

            if(!isValidLine(y, Direction.EAST))
                exp[3] = -2;
            else if(positionIsFree(x, y+1))
                exp[3] = 0;
            else if(board[x][y+1].getColor() == PlayerColor.WHITE)
                exp[3] = 1;
            else
                exp[3] = -1;

            return exp;
        }

        /**
         * Zwraca zwycięzce partii
         * @return kolor zwycięzcy
         */
        private PlayerColor getWinner() {
            return winner;
        }
    }

    /**
     * Metoda przywraca grę do poprzedniej fazy wstawiania kamieni
     */
    void returnToMainPhase() {
        movesStack = new int[3];
        deadStones = new boolean[dim][dim];
        deadStoneDecision = new int[3];
    }

    /**
     * Metoda dodająca do tablicy martwych kamieni wybór gracza
     * @param x współrzędna x kamienia
     * @param y współrzędna y kamienia
     */
    void addToDeadStones(int x, int y) {
        deadStones[x][y] = !deadStones[x][y];
    }

    /**
     * Metoda usuwająca z planszy kamienie martwe
     */
    private void removeDeadStones() {
        for(int i = 0; i < dim; i++) {
            for(int j = 0; j < dim; j++) {
                if(deadStones[i][j]) {
                    board[i][j] = null;
                    consoleBoard[i][j] = '.';
                }
            }
        }
    }

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

    //Gettery

    int getTotalStonesB() {
        return totalStonesB;
    }

    int getTotalStonesW() {
        return totalStonesW;
    }

    PlayerColor getCurrentPlayer() {
        return currentPlayer;
    }

    Stone[][] getBoard() {
        return board;
    }

    char[][] getTerritoryPointsBoard() {
        return territoryPointsBoard;
    }

    int getNumberStonesOnBoard() {
        return totalStonesB-currentStonesB-capturedByW+totalStonesW-currentStonesW-capturedByB;
    }

    int getNumberBlackStonesOnBoard() {
        return totalStonesB-currentStonesB-capturedByB;
    }

    int getNumberWhiteStonesOnBoard() {
        return totalStonesW-currentStonesW-capturedByW;
    }

    int getCurrentStonesB() {
        return currentStonesB;
    }

    int getCurrentStonesW() {
        return currentStonesW;
    }

    int getCapturedStonesW() {
        return capturedByB;
    }

    int getCapturedStonesB() {
        return capturedByW;
    }

    int getBlackTerritoryPoints() {
        return blackTerritoryPoints;
    }

    int getWhiteTerritoryPoints() {
        return whiteTerritoryPoints;
    }

    double getBlackScore() {
        return blackScore;
    }

    double getWhiteScore() {
        return whiteScore;
    }
}