import Basics.PlayerColor;
import Basics.Stone;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.StringTokenizer;

<<<<<<< HEAD
class Client extends ClientManager {
=======
<<<<<<< HEAD
class Client extends ClientManager {
=======
//TODO częściej repaint (szczególnie dla klasy czekającej na ruch)
//TODO co jak brak możliwych ruchów dla gracza - auto-pass ??
class Client extends ClientManager {
    /**
     * port serwera
     */
>>>>>>> master
>>>>>>> master
    private int port;
    private PlayerColor currentColor;
    PlayerColor playerColor;
    String socketName;
    int dim;
    Random generator = new Random();

    /**
     * Tworzenie obiektu klienta
     *
     * @param _ip_server adres IP przeciwnika
     * @param _port port przeciwnika
     */
    Client(String _ip_server, int _port, boolean firstPlayer) throws IOException {
        port = _port;

        if(firstPlayer) {
            playerColor = PlayerColor.BLACK;
            System.out.print("[GAME] ClientManager started \n");
            System.out.println("Black player: " + _port);
<<<<<<< HEAD
=======
<<<<<<< HEAD
=======
            System.out.println("Socket: " + socket);
>>>>>>> master
>>>>>>> master
            Wait wait = new Wait();
            wait.start();
        }

        if(!firstPlayer) {
            System.out.print("Client two go to game \n");
            socketName = _ip_server;
            playerColor = PlayerColor.WHITE;
            System.out.println("White player: " + _port);
            System.out.println("Socket: " + socket);
            try {
                socket = new Socket(socketName, port);
                System.out.println("[GAME] ClientManager connect ");
                GameWindow.window.jTextPane1.setCaretPosition(GameWindow.window.jTextPane1.getDocument().getLength());

                String a = "The game is began";
                String b = "Game started";
                JOptionPane.showMessageDialog(null, a, b, JOptionPane.INFORMATION_MESSAGE);
                new WaitMove();
            } catch (Exception e) {
                System.out.println("-->  Error in the network connection.");
            }

        }
        GameWindow.logArea.sendLogText(playerColor + ": Entered the game\n");
    }

    public Client(int port, int dim) {
        this.port=port;
        socketName="localhost";
        this.dim=dim;
        playerColor=PlayerColor.WHITE;
        System.out.print("Bot enteres the game \n");
        try {
            socket = new Socket(socketName, port);
            System.out.println("[GAME] Bot connect ");

            new BotWaitMove();
        } catch (Exception e) {
            System.out.println("-->  Error in the network connection.");
        }
        GameWindow.logArea.sendLogText(playerColor + ": Entered the game\n");
    }
    public void move(int x, int y) throws Exception {
        System.out.println("Bot entered start");
        currentColor = boardGraphic.getCurrentPlayer();
        if (currentColor == playerColor) {
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
                    new BotWaitMove();
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
                    new BotWaitMove();
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
            new BotWaitMove();
        }
    }
    private class BotWaitMove extends Thread {
        /**
         * Buduje obiekt i uruchamia wątek
         */
        BotWaitMove() {
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
                if (playerColor != currentColor) {
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
                                move(100, 2);
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
                            move(20,20);
                            boardGraphic.updateDeadStoneDecision(1);
                            GameWindow.gameStopped = true;
                            boardGraphic.changeTurn();
                            return;
                        }
                        //received AGREE ==> AGREE
                        else if (x == 30) {
                            if (y == 2) {
                                move(30,2);
                                GameWindow.gameStopped = false;
                                boardGraphic.endGame();
                            }
                            move(30,1);
                            boardGraphic.updateDeadStoneDecision(-1);
                            boardGraphic.changeTurn();
                            return;
                        }
                        //received Resume ==> do nothing, just play
                        else if (x == 40 && y == 40) {
                            move(generator.nextInt(dim), generator.nextInt(dim));
                            GameWindow.window.changePhase(false);
                            boardGraphic.returnToMainPhase();
                            boardGraphic.changeTurn();
                            return;
                        }
                        //received normal move
                        Stone p = boardGraphic.updateBoard(currentColor, x, y);
                    } else
                        socket = null;
                }
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Metoda uruchamiana, gdy użytkownik kliknie w punkt lub spasuje.
     *
     * @param x współrzędna x
     * @param y współrzędna y
     * @throws Exception Gdy wystąpi błąd
     */
    public void start(int x, int y) throws Exception {
        System.out.println("Socket: " + socket);
        System.out.println(playerColor + " entered start");
        currentColor = boardGraphic.getCurrentPlayer();
        GameWindow.window.changeAgreeState(true);
        String coord = x + "-" + y;
        //clicked RESIGN
        if(x == 50 && y == 50) {
            if(playerColor == PlayerColor.BLACK)
                new ThreadForJOptionPane("White");
            else
                new ThreadForJOptionPane("Black");
//             System.exit(0);
        }
        if (currentColor == playerColor) {
            if (boardGraphic != null) {
                System.out.println(playerColor + ": " + coord);
                //clicked pass
                if(x == 100) {
                    GameWindow.logArea.sendLogText(currentColor + ": Passed\n");
                    PrintWriter out_txt = new PrintWriter(socket.getOutputStream(), true);
                    out_txt.println(coord);
                    if(y == 2) {
                        GameWindow.logArea.sendLogText(currentColor + ": Entered dead stones pointing\n");
                        GameWindow.window.changePhase(true);
                    }
                    boardGraphic.skipMove();
                    paintLastMove(x, y);
                    new WaitMove();
                    return;
                }

                //clicked to delete
                else if(x >= 200 && y >= 200) {
                    PrintWriter out_txt = new PrintWriter(socket.getOutputStream(), true);
                    out_txt.println(coord);
                    GameWindow.window.changeAgreeState(false);
                    return;
                }
                //clicked SEND
                else if (x == 20 && y == 20) {
                    GameWindow.logArea.sendLogText(currentColor + ": Sent dead stones\n");
                    PrintWriter out_txt = new PrintWriter(socket.getOutputStream(), true);
                    out_txt.println(coord);
                    boardGraphic.updateDeadStoneDecision(1);
                    boardGraphic.changeTurn();
                    new WaitMove();
                    return;
                }
                //clicked AGREE
                else if (x == 30) {
                    GameWindow.logArea.sendLogText(currentColor + ": Agreed with dead stones\n");
                    PrintWriter out_txt = new PrintWriter(socket.getOutputStream(), true);
                    out_txt.println(coord);
                    boardGraphic.updateDeadStoneDecision(-1);
                    boardGraphic.changeTurn();
                    if(y == 2) {
                        GameWindow.logArea.sendLogText(currentColor + ": Game over\n");
                        GameWindow.gameStopped = false;
                        boardGraphic.endGame();
                        paintLastMove(x, y);
                    }
                    new WaitMove();
                    return;

                }
                //clicked RESUME
                else if(x == 40 && y == 40) {
                    GameWindow.logArea.sendLogText(currentColor + ": Resuming the game\n");
                    PrintWriter out_txt = new PrintWriter(socket.getOutputStream(), true);
                    out_txt.println(coord);
                    GameWindow.window.changePhase(false);
                    boardGraphic.returnToMainPhase();
                    boardGraphic.changeTurn();
                    new WaitMove();
                    return;
                }

                //clicked normal move
                Stone p = boardGraphic.updateBoard(currentColor, x, y);
                if (p != null) {
                    PrintWriter out_txt = new PrintWriter(socket.getOutputStream(), true);
                    out_txt.println(coord);
                    GameWindow.logArea.sendLogText(currentColor + ": Placed the stone at " + x + ", " + y + "\n");
                    paintLastMove(x, y);
                }
                else {
                    GameWindow.logArea.sendLogText(currentColor + ": Wrong place for stone\n");
                    return;
                }
            }
            else
                socket = null;
            new WaitMove();
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
            try {
                currentColor = boardGraphic.getCurrentPlayer();
                if(playerColor != currentColor) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String tmp = in.readLine();
                    StringTokenizer t = new StringTokenizer(tmp, "-");
                    int x = Integer.parseInt(t.nextToken());
                    int y = Integer.parseInt(t.nextToken());
                    System.out.println(playerColor + ": " + x + ", " + y);

                    if (boardGraphic != null) {
                        //received Pass
                        if (x == 100) {
                            if(y == 1) {
                                System.out.println("Received first pass");
                            }
                            if (y == 2) {
                                System.out.println("Received second pass");
                                GameWindow.window.changePhase(true);
                            }
                            GameWindow.logArea.sendLogText(currentColor + ": Passed\n");
                            boardGraphic.skipMove();
                            paintLastMove(x, y);
                            GameWindow.window.repaint();
                            return;
                        }
                        //received to delete
                        else if (x >= 200 && y >= 200) {
                            boardGraphic.addToDeadStones(x - 200, y - 200);
                            new WaitMove();
                            return;
                        }

                        //received SEND
                        else if (x == 20 && y == 20) {
                            GameWindow.logArea.sendLogText(currentColor + ": Sent dead stones\n");
                            boardGraphic.updateDeadStoneDecision(1);
                            GameWindow.gameStopped = true;
                            boardGraphic.changeTurn();
                            GameWindow.window.repaint();
                            return;
                        }
                        //received AGREE
                        else if (x == 30) {
                            GameWindow.logArea.sendLogText(currentColor + ": Agreed with dead stones\n");
                            if(y == 2) {
                                GameWindow.logArea.sendLogText(currentColor + ": Game over\n");
                                GameWindow.gameStopped = false;
                                boardGraphic.endGame();
                                GameWindow.window.repaint();
                                paintLastMove(x, y);
                            }
                            boardGraphic.updateDeadStoneDecision(-1);
                            boardGraphic.changeTurn();
                            return;
                        }
                        //received Resume
                        else if(x == 40 && y == 40) {
                            GameWindow.logArea.sendLogText(currentColor + ": Resuming the game\n");
                            GameWindow.window.changePhase(false);
                            boardGraphic.returnToMainPhase();
                            boardGraphic.changeTurn();
                            GameWindow.window.repaint();
                            return;
                        }
                        //received normal move
                        Stone p = boardGraphic.updateBoard(currentColor, x, y);
                        if (p != null) {
                            GameWindow.logArea.sendLogText(currentColor + ": Placed the stone at " + x + ", " + y + "\n");
                            paintLastMove(x, y);
                        }
                    } else
                        socket = null;
                }
            } catch (Exception ignored) {
            }
        }
    }

    class Wait extends Thread {
        /**
         * Wątek, który czeka na klienta
         */
        public void run() {
            try {
                s = new ServerSocket(port);
                socket = s.accept();
                GameWindow.window.jTextPane1.setCaretPosition(GameWindow.window.jTextPane1.getDocument().getLength());
                String a = "The game is began. Black starts";
                String b = "Game started";
                JOptionPane.showMessageDialog(null, a, b, JOptionPane.INFORMATION_MESSAGE);
            }
            catch (Exception ignored) {}
        }
    }
}
