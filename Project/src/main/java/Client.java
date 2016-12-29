import Basics.PlayerColor;
import Basics.Stone;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.StringTokenizer;


//TODO częściej repaint (szczególnie dla klasy czekającej na ruch)
//TODO co jak brak możliwych ruchów dla gracza - auto-pass ??
class Client extends ClientManager {
    public boolean yesYouCan=false;
    private int port;
    PlayerColor currentColor;
    PlayerColor playerColor;
    private String socketName;
    private int dim;
    private Random generator = new Random();

    /**
     * Tworzenie obiektu klienta
     *
     * @param _ip_server adres IP przeciwnika
     * @param _port port przeciwnika
     */
    Client(String _ip_server, int _port, boolean firstPlayer, GameWindow gameWindow) throws IOException {
        super(gameWindow);
        port = _port;

        if(firstPlayer) {
            playerColor = PlayerColor.BLACK;
            System.out.print("[GAME] ClientManager started \n");
            System.out.println("Black player: " + _port);
            System.out.println("Socket: " + socket);
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
                gameWindow.jTextPane1.setCaretPosition(gameWindow.jTextPane1.getDocument().getLength());

                String a = "The game is began";
                String b = "Game started";
                JOptionPane.showMessageDialog(null, a, b, JOptionPane.INFORMATION_MESSAGE);
                new WaitMove();
            } catch (Exception e) {
                System.out.println("-->  Error in the network connection.");
            }

        }
        gameWindow.logArea.sendLogText(playerColor + ": Entered the game\n");
    }

    Client(int port, int dim, GameWindow gameWindow) {
        super(gameWindow);
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
        gameWindow.logArea.sendLogText(playerColor + ": Entered the game\n");
    }

    /**
     * wykonuje ruchy dla bota
     * @param x
     * @param y
     * @throws Exception
     */
    public void move(int x, int y) throws Exception {
        System.out.println("Bot entered start");
        currentColor = boardGraphic.getCurrentPlayer();
//        if (currentColor == playerColor) {
            if (boardGraphic != null) {
                String coord = x + "-" + y;
                System.out.println(coord);
                //clicked pass
                if (x == 100) {
                    PrintWriter out_txt = new PrintWriter(socket.getOutputStream(), true);
                    //TODO: sprawdzanie co wysłano
                    System.out.println("dostałem pas");
                    out_txt.println(coord);
                    if (y == 2) {
                        gameWindow.gameStopped = true;
                        gameWindow.window.changePhase(true);
                    }
                    boardGraphic.skipMove();
                    new BotWaitMove();
                    return;
                }
                //clicked AGREE
                else if (x == 30) {
                    PrintWriter out_txt = new PrintWriter(socket.getOutputStream(), true);
                    out_txt.println(coord);
                    boardGraphic.updateDeadStoneDecision(-1);
                    boardGraphic.changeTurn();
                    if (y == 2) {
                        gameWindow.gameStopped = false;
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
                }

            } else {
                move(100,1);
            }
            boardGraphic.changeTurn();
            new BotWaitMove();
//        }
    }
    public void yesYou(){
        yesYouCan=true;
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
                System.out.println("[Bot] I'm waiting here...");
                do {
                    if(yesYouCan){break;}
                    currentColor = boardGraphic.getCurrentPlayer();
                } while (currentColor == playerColor);
                System.out.println("[Bot] Oh look! Its my turn now!");
                if (playerColor != currentColor) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String tmp = in.readLine();
                    StringTokenizer t = new StringTokenizer(tmp, "-");
                    int x = Integer.parseInt(t.nextToken());
                    int y = Integer.parseInt(t.nextToken());
                    System.out.println("[Bot] " + playerColor + ": " + x + ", " + y);
                    if(yesYouCan){
                        move(100,1);
                        yesYouCan=false;
                        return;
                    }
                    if (boardGraphic != null) {
                        //received Pass ==> PASS
                        if (x == 100) {
                            if(y==1) {
                                System.out.println("BOT: I've got: Received first pass");
                                move(100, 2);
                                boardGraphic.skipMove();
                                return;
                            }
                            if (y == 2) {
                                System.out.println("Received second pass");
                                move(20,20);
                                gameWindow.gameStopped = true;
                                gameWindow.window.changePhase(true);
                                return;
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
                            gameWindow.gameStopped = true;
                            boardGraphic.changeTurn();
                            return;
                        }
                        //received AGREE ==> AGREE
                        else if (x == 30) {
                            if (y == 2) {
                                move(30,2);
                                gameWindow.gameStopped = false;
                                boardGraphic.endGame();
                            }
                            move(30,1);
                            boardGraphic.updateDeadStoneDecision(-1);
                            boardGraphic.changeTurn();
                            return;
                        }
                        //received Resume ==> do nothing, just play
                        else if (x == 40 && y == 40) {
                            x=generator.nextInt(dim);
                            y=generator.nextInt(dim);
                            System.out.print(x+ " + "+y);
                            boolean check =boardGraphic.positionIsFree(x,y);
                            if (!check){
                                move(100,1);
                            }
                            move(x,y);
                            gameWindow.window.changePhase(false);
                            boardGraphic.returnToMainPhase();
                            boardGraphic.changeTurn();
                            return;
                        }
                        //received normal move
                        Stone p = boardGraphic.updateBoard(currentColor, x, y);
                        if (p != null) {
                            paintLastMove(x, y);
                        }


                        x=generator.nextInt(dim);
                        y=generator.nextInt(dim);
                        System.out.println("[Bot] i'll move to: " + x + " : " + y);
                        boolean check =boardGraphic.positionIsFree(x,y);
                        if (!check){
                            System.out.println("CHECK");
                            move(100,1);
                        }
                        move(x,y);

                        gameWindow.window.changePhase(false);
                        boardGraphic.returnToMainPhase();
                        boardGraphic.changeTurn();
                    } else
                        socket = null;
                }
            } catch (Exception ignored) {
                System.out.println("[Bot] I'm done...");
            }
            System.out.println("[Bot] I did my duty, bye!");
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
        System.out.println(playerColor + " entered start");
        currentColor = boardGraphic.getCurrentPlayer();
        gameWindow.window.changeAgreeState(true);
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
                    gameWindow.logArea.sendLogText(currentColor + ": Passed\n");
                    PrintWriter out_txt = new PrintWriter(socket.getOutputStream(), true);
                    out_txt.println(coord);
                    if(y == 2) {
                        gameWindow.logArea.sendLogText(currentColor + ": Entered dead stones pointing\n");
                        gameWindow.window.changePhase(true);
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
                    gameWindow.window.changeAgreeState(false);
                    return;
                }
                //clicked SEND
                else if (x == 20 && y == 20) {
                    gameWindow.logArea.sendLogText(currentColor + ": Sent dead stones\n");
                    PrintWriter out_txt = new PrintWriter(socket.getOutputStream(), true);
                    out_txt.println(coord);
                    boardGraphic.updateDeadStoneDecision(1);
                    boardGraphic.changeTurn();
                    new WaitMove();
                    return;
                }
                //clicked AGREE
                else if (x == 30) {
                    gameWindow.logArea.sendLogText(currentColor + ": Agreed with dead stones\n");
                    PrintWriter out_txt = new PrintWriter(socket.getOutputStream(), true);
                    out_txt.println(coord);
                    boardGraphic.updateDeadStoneDecision(-1);
                    boardGraphic.changeTurn();
                    if(y == 2) {
                        gameWindow.logArea.sendLogText(currentColor + ": Game over\n");
                        gameWindow.gameStopped = false;
                        boardGraphic.endGame();
                        paintLastMove(x, y);
                    }
                    new WaitMove();
                    return;

                }
                //clicked RESUME
                else if(x == 40 && y == 40) {
                    gameWindow.logArea.sendLogText(currentColor + ": Resuming the game\n");
                    PrintWriter out_txt = new PrintWriter(socket.getOutputStream(), true);
                    out_txt.println(coord);
                    gameWindow.window.changePhase(false);
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
                    gameWindow.logArea.sendLogText(currentColor + ": Placed the stone at " + x + ", " + y + "\n");
                    paintLastMove(x, y);
                }
                else {
                    gameWindow.logArea.sendLogText(currentColor + ": Wrong place for stone\n");
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
        WaitMove() {start();}

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
                                gameWindow.window.changePhase(true);
                            }
                            gameWindow.logArea.sendLogText(currentColor + ": Passed\n");
                            boardGraphic.skipMove();
                            paintLastMove(x, y);
                            gameWindow.window.repaint();
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
                            gameWindow.logArea.sendLogText(currentColor + ": Sent dead stones\n");
                            boardGraphic.updateDeadStoneDecision(1);
                            gameWindow.gameStopped = true;
                            boardGraphic.changeTurn();
                            gameWindow.window.repaint();
                            return;
                        }
                        //received AGREE
                        else if (x == 30) {
                            gameWindow.logArea.sendLogText(currentColor + ": Agreed with dead stones\n");
                            if(y == 2) {
                                gameWindow.logArea.sendLogText(currentColor + ": Game over\n");
                                gameWindow.gameStopped = false;
                                boardGraphic.endGame();
                                gameWindow.window.repaint();
                                paintLastMove(x, y);
                            }
                            boardGraphic.updateDeadStoneDecision(-1);
                            boardGraphic.changeTurn();
                            return;
                        }
                        //received Resume
                        else if(x == 40 && y == 40) {
                            gameWindow.logArea.sendLogText(currentColor + ": Resuming the game\n");
                            gameWindow.window.changePhase(false);
                            boardGraphic.returnToMainPhase();
                            boardGraphic.changeTurn();
                            gameWindow.window.repaint();
                            return;
                        }
                        //received normal move
                        System.out.println("[User] I'm placing the rock!");
                        Stone p = boardGraphic.updateBoard(currentColor, x, y);
                        if (p != null) {
                            gameWindow.logArea.sendLogText(currentColor + ": Placed the stone at " + x + ", " + y + "\n");
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
                gameWindow.window.jTextPane1.setCaretPosition(gameWindow.window.jTextPane1.getDocument().getLength());
                String a = "The game is began. Black starts";
                String b = "Game started";
                JOptionPane.showMessageDialog(null, a, b, JOptionPane.INFORMATION_MESSAGE);
            }
            catch (Exception ignored) {}
        }
    }
}
