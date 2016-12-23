import Basics.PlayerColor;
import Basics.Stone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.StringTokenizer;

class Bot extends Client {
    Random generator = new Random();
    int dim;
    /**
     * port serwera
     */
    private int port;
    /**
     * Wskazuje czyja kolej
     */
    private PlayerColor currentColor;

    /**
     * Kolor gracza
     */
    private PlayerColor botColor = PlayerColor.WHITE;

    /**
     * Tworzenie obiektu klienta
     *
     * @param _ip_server adres IP przeciwnika
     * @param _port      port przeciwnika
     */
    Bot(String _ip_server, int _port, int _dim) throws IOException {
        super();
        /**
         * IP serwera
         */
        String socketName = _ip_server;
        /**
         * port serwera
         */
        port = _port;
        dim = _dim;
        System.out.print("Bot enteres the game \n");
        try {
            socket = new Socket(socketName, port);
            System.out.println("[GAME] Bot connect ");

            new WaitMove();
        } catch (Exception e) {
            System.out.println("-->  Error in the network connection.");
        }
        GameWindow.logArea.sendLogText(botColor + ": Entered the game\n");
    }

    public void move(int x, int y) throws Exception {
        System.out.println("Bot entered start");
        currentColor = boardGraphic.getCurrentPlayer();
        if (currentColor == botColor) {
            if (boardGraphic != null) {
                String coord = x + "-" + y;

                //TODO pass jest odpowiedzią na pas
                //clicked pass
                if (x == 100) {
                    PrintWriter out_txt = new PrintWriter(socket.getOutputStream(), true);
                    out_txt.println(coord);
                    if (y == 2) {
                        GameWindow.gameStopped = true;
                        GameWindow.window.changePhase(true);
                    }
                    boardGraphic.skipMove();
                    new Bot.WaitMove();
                    return;
                }
                //TODO agree zawsze w fazie wskazywania kamieni martwych
                //clicked AGREE
                else if (x == 30) {
                    PrintWriter out_txt = new PrintWriter(socket.getOutputStream(), true);
                    out_txt.println(coord);
                    boardGraphic.updateDeadStoneDecision(-1);
                    boardGraphic.changeTurn();
                    if (y == 2) {
                        GameWindow.gameStopped = false;
                        boardGraphic.endGame();
                    }
                    new Bot.WaitMove();
                    return;

                }
                //clicked normal move
                Stone p = boardGraphic.updateBoard(currentColor, x, y);
                if (p != null) {
                    PrintWriter out_txt = new PrintWriter(socket.getOutputStream(), true);
                    out_txt.println(coord);
                } else {
                    return;
                }
            } else
                socket = null;
            new Bot.WaitMove();
        }
    }

    /**
     * Oczekuje na ruch przeciwnika
     */
    private class WaitMove extends Thread {
        /**
         * Buduje obiekt i uruchamia wątek
         */
        WaitMove() {
            start();
        }

        /**
         * Wątek czeka na ruch
         */
        public void run() {
            /**
             * otrzymanie ruchu = wykonanie ruchu w randomowym miejscu
             * otrzymanie 1 pass = odpowiedz pass
             * otrzymanie 2 pass = agree
             * otrzymanie send = agree
             * otrzymanie resume = kolejny ruch
             * otrzymanie 1 agree = agree
             * otrzymanie 2 agree = agree
             */
            try {
                currentColor = boardGraphic.getCurrentPlayer();
                if (botColor != currentColor) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String tmp = in.readLine();
                    StringTokenizer t = new StringTokenizer(tmp, "-");
                    int x = Integer.parseInt(t.nextToken());
                    int y = Integer.parseInt(t.nextToken());

                    if (boardGraphic != null) {
                        //received Pass ==> PASS
                        if (x == 100) {
                            System.out.println("Received first pass");
                            boardGraphic.skipMove();
                            if (y == 2) {
                                System.out.print(x + " + " + y + " \n");
                                move(x, y);
                                System.out.println("Received second pass");
                                GameWindow.gameStopped = true;
                                GameWindow.window.changePhase(true);
                            }
                        }
                        //received to delete
                        else if (x >= 200 && y >= 200) {
                            boardGraphic.addToDeadStones(x - 200, y - 200);
                            new WaitMove();
                            return;
                        }

                        //received SEND ==> agree
                        else if (x == 20 && y == 20) {
                            move(x, y);
                            System.out.print(x + " + " + y + " \n");
                            boardGraphic.updateDeadStoneDecision(1);
                            GameWindow.gameStopped = true;
                            boardGraphic.changeTurn();
                            return;
                        }
                        //received AGREE ==> AGREE
                        else if (x == 30) {
                            if (y == 2) {
                                System.out.print(x + " + " + y + " \n");
                                move(x, y);
                                GameWindow.gameStopped = false;
                                boardGraphic.endGame();
                            }
                            y = 1;
                            System.out.print(x + " + " + y + " \n");
                            move(x, y);
                            boardGraphic.updateDeadStoneDecision(-1);
                            boardGraphic.changeTurn();
                            return;
                        }
                        //received Resume ==> do nothing, just play
                        else if (x == 40 && y == 40) {
                            x = generator.nextInt(dim);
                            y = generator.nextInt(dim);
                            System.out.print(x + " + " + y + " \n");
                            move(x, y);
                            GameWindow.window.changePhase(false);
                            boardGraphic.returnToMainPhase();
                            boardGraphic.changeTurn();
                            return;
                        }
                        //received normal move
                        x = generator.nextInt(dim);
                        y = generator.nextInt(dim);
                        System.out.print(x + " + " + y + " \n");
                        move(x, y);
                        Stone p = boardGraphic.updateBoard(currentColor, x, y);
                    } else
                        socket = null;
                }
            } catch (Exception ignored) {
            }
        }
    }
}

