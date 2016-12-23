import Basics.PlayerColor;
import Basics.Stone;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

//TODO częściej repaint (szczególnie dla klasy czekającej na ruch)
//TODO co jak brak możliwych ruchów dla gracza - auto-pass ??
public class Client extends ClientManager {
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
    PlayerColor playerColor;

    /**
     * Tworzenie obiektu klienta
     *
     * @param _ip_server adres IP przeciwnika
     * @param _port port przeciwnika
     */
    Client(String _ip_server, int _port, boolean firstPlayer) throws IOException {
		/**
	  	* IP serwera
	 	*/
        String socketName;
		/**
		* port serwera
	 	*/
        port = _port;

        if(firstPlayer) {
            playerColor = PlayerColor.BLACK;
            System.out.print("[GAME] ClientManager started \n");
            Wait wait = new Wait();
            wait.start();
        }

        if(!firstPlayer) {
            System.out.print("Client two go to game \n");
            socketName = _ip_server;
            playerColor = PlayerColor.WHITE;
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

    public Client() {
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
        GameWindow.window.changeAgreeState(true);
        if (currentColor == playerColor) {
            if (boardGraphic != null) {
                String coord = x + "-" + y;

                //clicked pass
                if(x == 100) {
                    GameWindow.logArea.sendLogText(currentColor + ": Passed\n");
                    PrintWriter out_txt = new PrintWriter(socket.getOutputStream(), true);
                    out_txt.println(coord);
                    if(y == 2) {
                        GameWindow.gameStopped = true;
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
                    //TODO wysyłać z Game przyczynę niemożliwości wstawienia kamienia (wyjątki ?)
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

                    if (boardGraphic != null) {
                        //received Pass
                        if (x == 100) {
                            System.out.println("Received first pass");
                            GameWindow.logArea.sendLogText(currentColor + ": Passed\n");
                            boardGraphic.skipMove();
                            paintLastMove(x, y);
                            if (y == 2) {
                                System.out.println("Received second pass");
                                GameWindow.gameStopped = true;
                                GameWindow.window.changePhase(true);
                            }
                            GameWindow.window.repaint();
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
