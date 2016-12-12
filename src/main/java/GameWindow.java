<<<<<<< HEAD
//import javafx.scene.input.MouseButton;
=======
import javafx.scene.input.MouseButton;
>>>>>>> master
import org.imgscalr.Scalr;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
<<<<<<< HEAD
=======
import java.util.Vector;
>>>>>>> master
import javax.imageio.*;

class GameWindow extends JFrame {
    private JCheckBoxMenuItem jCheckBoxMenuItem1;
    static MainPanel jPanel2;
    JTextPane jTextPane1;
    private JLabel jLabel17 = new JLabel();
    private JTabbedPane jTabbedPane1;
    private JTextField jTextField3;
    private JTextField jTextField4;
    private JTextField jTextField5;
    private JTextField jTextField6;
    private JTextField jTextField7;
    private JTextField jTextField9;
    private JTextField jTextField10;
    private JTextField jTextField11;
    private JTextField jTextField12;
    private JTextField jTextField13;
    private JTextField jTextField14;
    private JTextField jTextField15;

    static GameWindow window;
<<<<<<< HEAD
    ClientManager manager;
    boolean ok;
    boolean gameStopped = false;
    private int dimension;

    GameWindow(int dim) {
        window = this;
        dimension = dim;
        buildWindow();
=======
    GameMoveManager manager;
    boolean ok;
    private int dimension;

    GameWindow(int dim) {
        IPMainServer = "null";
        window = this;
        dimension = dim;
        initComponents();
>>>>>>> master
    }

    class MainPanel extends JPanel {
        private Image boardIMG;
        private Image blackIMG;
        private Image whiteIMG;
<<<<<<< HEAD
        private Image blackTerritoryIMG;
        private Image whiteTerritoryIMG;
        private Image markup;
=======
        private Image blackEndIMG;
        private Image whiteEndIMG;
>>>>>>> master

        private int height;
        private int base;
        private int leftX;
        private int topY;
        int lastX;
        int lastY;

<<<<<<< HEAD
        int sizeOfField;
        int stoneSize;
        int xAdd;
        int yAdd;
        int xTer;
        int yTer;
        int xViewfinder;
        int yViewfinder;

        MainPanel() {
            setGraphicVariables();
            loadGraphics();
            addMouseListener(new MouseListener() {
                public void mousePressed (MouseEvent e){
=======

        MainPanel() {
            try {
                if(dimension == 19) {
                    boardIMG = ImageIO.read(new File("grid.png"));
                    blackIMG = ImageIO.read(new File("black.png"));
                    whiteIMG = ImageIO.read(new File("white.png"));
                    blackEndIMG = ImageIO.read(new File("end_black.png"));
                    whiteEndIMG = ImageIO.read(new File("end_white.png"));
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }

            addMouseListener(new MouseListener() {
                public void mousePressed(MouseEvent e) {
>>>>>>> master
                    int coordX = e.getY();
                    int coordY = e.getX();
                    int click = e.getButton();
                    int line = 0;
                    int column = 0;
<<<<<<< HEAD
                    boolean sendClicked = false;

                    for (int i = 18; i < 533; i += sizeOfField, line++) {
                        for (int j = 18; j < 483; j += sizeOfField, column++) {
                            if ((coordX >= i) && (coordX < (i + sizeOfField)) && (coordY >= j) && (coordY < (j + sizeOfField))) {
                                try {
                                    if (manager == null)
                                        return;

                                    //rozgrywka klient-serwer
                                    if ((manager instanceof FirstClientManager) || (manager instanceof SecondClientManager)) {
                                        if (gameStopped) {
                                            manager.getBoard().deadStones[line][column] = true;
                                        }
//                                        //dopóki nie kliknie send: zaznacza pola + repaint (wraz z zaznaczeniem)
//                                        //gdy kliknie ok: wysyła na serwer w pętli te dane.
//                                        //Serwer wysyła do drugiego klienta itd
//                                        else if(click == 1)
                                        else
                                            manager.start(line, column);
                                    }
                                    //jednookienkowe (lewy i prawy klawisz)
                                    else {
                                        if (gameStopped && click == 3 && manager.getBoard().getBoard()[line][column] != null) {
                                            manager.getBoard().deadStones[line][column] = !manager.getBoard().deadStones[line][column];
                                        }
                                        else {
                                            if ((click == 1) && (manager.getBoard().getCurrentPlayer() == PlayerColor.WHITE))
                                                manager.getBoard().changeTurn();
                                            else if ((click == 3) && (manager.getBoard().getCurrentPlayer() == PlayerColor.BLACK))
                                                manager.getBoard().changeTurn();
                                            System.out.println("Przed wstawieniem kamienia");
                                            manager.start(line, column);
                                        }
                                    }
                                    reloadInfo();
                                    repaint();
                                }
                                catch (Exception ec) {
=======

                    for (int i = 18; i < 533; i += 25, line++) {
                        for (int j = 18; j < 483; j += 25, column++) {
                            if ((coordX >= i) && (coordX < (i + 25)) && (coordY >= j) && (coordY < (j + 25))) {
                                try {
                                    //wstawienie kamienia na planszę
                                    //jednookienkowe (lewy i prawy klawisz)
                                    }
                                    reload();
                                    repaint();
                                } catch (Exception ec) {
>>>>>>> master
                                    System.err.println("Error");
                                    ec.printStackTrace();
                                }
                            }
                        }
                        column = 0;
                    }
                }

                public void mouseClicked(MouseEvent e) {}
<<<<<<< HEAD

                public void mouseReleased(MouseEvent e) {}

                public void mouseEntered(MouseEvent e) {}

                public void mouseExited(MouseEvent e) {}
            });
            addMouseMotionListener(new MouseMotionListener() {
                public void mouseMoved (MouseEvent e){
=======
                public void mouseReleased(MouseEvent e) {}
                public void mouseEntered(MouseEvent e) {}
                public void mouseExited(MouseEvent e) {}
            });

            addMouseMotionListener(new MouseMotionListener() {
                public void mouseMoved(MouseEvent e) {
>>>>>>> master
                    if (!jCheckBoxMenuItem1.isSelected()) {
                        height = 0;
                        base = 0;
                        repaint();
                        return;
                    }

                    height = 10;
                    base = 10;
                    int coord_x = e.getY();
                    int coord_y = e.getX();

<<<<<<< HEAD
                    for (int i = 18; i < 533; i += sizeOfField)
                        for (int j = 18; j < 483; j += sizeOfField) {
                            if ((coord_x >= i) && (coord_x < (i + sizeOfField)) && (coord_y >= j) && (coord_y < (j + sizeOfField))) {
                                leftX = j + xViewfinder;
                                topY = i + yViewfinder;
=======
                    for (int i = 18; i < 533; i += 25)
                        for (int j = 18; j < 483; j += 25) {
                            if ((coord_x >= i) && (coord_x < (i+25)) && (coord_y >= j) && (coord_y < (j+25))) {
                                leftX = j+2;
                                topY = i+7;
>>>>>>> master
                                repaint();
                            }
                        }
                }

                public void mouseDragged(MouseEvent e) {}
            });
        }

<<<<<<< HEAD
        private void setGraphicVariables() {
            if(dimension == 19) {
                sizeOfField = 25;
                xAdd = 15;
                yAdd = 20;
                xTer = 21;
                yTer = 25;
                stoneSize = 20;
                xViewfinder = 2;
                yViewfinder = 7;
            }

            else if(dimension == 9) {
                sizeOfField = 52;
                xAdd = 22;
                yAdd = 27;
                xTer = 32;
                yTer = 35;
                stoneSize = 40;
                xViewfinder = 17;
                yViewfinder = 20;
            }
        }

        private void loadGraphics() {
            try {
                boardIMG = ImageIO.read(new File(dimension + "x" + dimension + ".png"));
                blackIMG = Scalr.resize(ImageIO.read(new File("stoneBlack.png")), stoneSize);
                whiteIMG = Scalr.resize(ImageIO.read(new File("stoneWhite.png")), stoneSize);
                blackTerritoryIMG = Scalr.resize(ImageIO.read(new File("stoneBlack.png")), stoneSize/2);
                whiteTerritoryIMG = Scalr.resize(ImageIO.read(new File("stoneWhite.png")), stoneSize/2);
                markup = Scalr.resize(ImageIO.read(new File("markup.png")), stoneSize/2);
            }
            catch(Exception exception){
                exception.printStackTrace();
            }
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g.drawImage(boardIMG, 10, 15, null);

            if(manager != null) {
//                if (manager.getBoard().currentGame() == GamePhase.CHOOSING) {
//                    char[][] tmp2 = manager.getBoard().getTerritoryPointsBoard();
//                    insertTerritoryStones(tmp2, g);
//                }
                insertStoneGraphics(manager.getBoard().getBoard(), g);

                if (gameStopped) {
                    for (int i = 0; i < dimension; i++)
                        for (int j = 0; j < dimension; j++) {
                            if (manager.getBoard().deadStones[i][j])
                                g.drawImage(markup, (j * sizeOfField) + xTer, (i * sizeOfField) + yTer, null);
                        }
=======
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            g.drawImage(boardIMG, 10, 15, null);

            if (manager != null && manager.getBoard().currentGame() == GamePhase.CHOOSING) {
                char[][] tmp2 = manager.getBoard().getTerritoryPointsBoard();
                insertTerritoryStones(tmp2, g);
            }
            if (manager != null) {
                insertStoneGraphics(manager.getBoard().getBoard(), g);
            }
            if(manager != null && gameStopped) {
                for(Point p: deadStones) {
>>>>>>> master
                }
            }

            Rectangle2D rect = new Rectangle2D.Double(leftX, topY, base, height);
            g2.setPaint(Color.RED);
            g2.draw(rect);

            Rectangle2D circle = new Rectangle2D.Double(lastX, lastY, height, base);
            g2.setPaint(Color.GREEN);
            g2.fill(circle);

            try {
<<<<<<< HEAD
                reloadInfo();
=======
                reload();
>>>>>>> master
            }
            catch (Exception ignored) {}
        }

        void insertStoneGraphics(Stone[][] temp, Graphics g) {
<<<<<<< HEAD
            for (int i = 0; i < dimension; i++)
                for (int j = 0; j < dimension; j++) {
                    if ((temp[i][j] != null) && temp[i][j].getColor() == PlayerColor.WHITE)
                        g.drawImage(whiteIMG, (j * sizeOfField) + xAdd, (i * sizeOfField) + yAdd, null);
                    else if ((temp[i][j] != null) && temp[i][j].getColor() == PlayerColor.BLACK)
                        g.drawImage(blackIMG, (j * sizeOfField) + xAdd, (i * sizeOfField) + yAdd, null);
=======
            for (int i = 0; i < 19; i++)
                for (int j = 0; j < 19; j++) {
                    if ((temp[i][j] != null) && temp[i][j].getColor() == PlayerColor.WHITE)
                        g.drawImage(whiteIMG, (j * 25) + 15, (i * 25) + 20, null);
                    else if ((temp[i][j] != null) && temp[i][j].getColor() == PlayerColor.BLACK)
                        g.drawImage(blackIMG, (j * 25) + 15, (i * 25) + 20, null);
>>>>>>> master
                }
        }

        void insertTerritoryStones(char[][] temp, Graphics g) {
<<<<<<< HEAD
            for (int i = 0; i < dimension; i++)
                for (int j = 0; j < dimension; j++) {
                    if (temp[i][j] == 'W')
                        g.drawImage(whiteTerritoryIMG, (j * sizeOfField) + xTer, (i * sizeOfField) + yTer, null);
                    else if (temp[i][j] == 'B')
                        g.drawImage(blackTerritoryIMG, (j * sizeOfField) + xTer, (i * sizeOfField) + yTer, null);
=======
            for (int i = 0; i < 19; i++)
                for (int j = 0; j < 19; j++) {
                    if (temp[i][j] == 'W')
                        g.drawImage(whiteEndIMG, (j * 25) + 21, (i * 25) + 25, null);
                    else if (temp[i][j] == 'B')
                        g.drawImage(blackEndIMG, (j * 25) + 21, (i * 25) + 25, null);
>>>>>>> master
                }
        }
    }

<<<<<<< HEAD
    private void buildWindow() {
=======
    private void initComponents() {
>>>>>>> master
        JPanel jPanel1 = new JPanel();
        jPanel2 = new MainPanel();
        JPanel jPanel3 = new JPanel();
        JPanel jPanel4 = new JPanel();
        JPanel jPanel5 = new JPanel();
        JPanel jPanel6 = new JPanel();

        JScrollPane jScrollPane1 = new JScrollPane();
        jTextPane1 = new JTextPane();

        jTabbedPane1 = new JTabbedPane();
        JLayeredPane jLayeredPane1 = new JLayeredPane();
        JLayeredPane jLayeredPane2 = new JLayeredPane();
        JLayeredPane jLayeredPane3 = new JLayeredPane();

        JLabel jLabel1 = new JLabel();
        JLabel jLabel4 = new JLabel();
        JLabel jLabel5 = new JLabel();
        JLabel jLabel6 = new JLabel();
        JLabel jLabel7 = new JLabel();
        JLabel jLabel9 = new JLabel();
        JLabel jLabel8 = new JLabel();
        JLabel jLabel10 = new JLabel();
        JLabel jLabel11 = new JLabel();
        JLabel jLabel12 = new JLabel();
        JLabel jLabel13 = new JLabel();
        JLabel jLabel14 = new JLabel();
        JLabel jLabel15 = new JLabel();
        JLabel jLabel16 = new JLabel();
        JLabel jLabel18 = new JLabel();

        jTextField3 = new JTextField();
        jTextField4 = new JTextField();
        jTextField5 = new JTextField();
        jTextField6 = new JTextField();
        jTextField7 = new JTextField();
        JTextField jTextField8 = new JTextField();
        jTextField9 = new JTextField();
        jTextField10 = new JTextField();
        jTextField11 = new JTextField();
        jTextField12 = new JTextField();
        jTextField13 = new JTextField();
        jTextField14 = new JTextField();
        jTextField15 = new JTextField();

        JButton buttonPass = new JButton();
        JButton buttonResign = new JButton();
        JButton buttonResume = new JButton();
        JButton buttonAgree = new JButton();
        JButton buttonSend = new JButton();

        JSeparator jSeparator1 = new JSeparator();
        JSeparator jSeparator2 = new JSeparator();
        JSeparator jSeparator3 = new JSeparator();
        JMenuBar jMenuBar1 = new JMenuBar();
        JMenu jMenu1 = new JMenu();
        JMenu jMenu2 = new JMenu();
        JMenu jMenu3 = new JMenu();
        jCheckBoxMenuItem1 = new JCheckBoxMenuItem();
        JMenuItem jMenuItem1 = new JMenuItem();
        JMenuItem jMenuItem2 = new JMenuItem();
        JMenuItem jMenuItem4 = new JMenuItem();
        JMenuItem jMenuItem5 = new JMenuItem();
        JMenuItem jMenuItem3 = new JMenuItem();

        //TODO wysłanie info do serwera - podczas rozgrywki oznacza to poddanie partii
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                String p = "Do you want to exit the program";
                int c = JOptionPane.showConfirmDialog(null, p, "Information", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
<<<<<<< HEAD
=======

>>>>>>> master
                if (c == JOptionPane.OK_OPTION)
                    System.exit(1);
            }
        });

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("GoProject 1.0");
        setResizable(false);

        getContentPane().setLayout(null);
        jPanel1.setLayout(null);
        jPanel2.setLayout(null);
        jPanel3.setLayout(null);
        jPanel4.setLayout(null);
        jPanel5.setLayout(new FlowLayout(FlowLayout.CENTER));
        jPanel6.setLayout(null);

        setPanelBorder(jPanel2, "Playing area");
        setPanelBorder(jPanel3, "Game stats");
        setPanelBorder(jPanel4, "Log");
        setPanelBorder(jPanel5, "Current turn");

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setViewportView(jTextPane1);

        jTextPane1.setEditable(false);
        initStylesForTextPane(jTextPane1);

        jPanel2.add(jLabel1);
        jPanel2.setBounds(310, 0, 500, 510);
        jLabel1.setBounds(10, 20, 480, 480);
        jPanel1.add(jPanel2);

        jPanel1.add(jPanel3);
        jPanel3.setBounds(0, 220, 310, 290);

        jPanel4.add(jScrollPane1);
        jScrollPane1.setBounds(20, 20, 270, 115);
        jPanel1.add(jPanel4);
        jPanel4.setBounds(0, 0, 310, 145);

        jPanel5.setBounds(0, 145, 310, 75);
        jLabel17.setFont(new Font("Courier New", Font.BOLD, 30));
        jLabel17.setBounds(0, 0, 20, 70);
        jPanel5.add(jLabel17);
        jPanel1.add(jPanel5);

        jTabbedPane1.setFont(new Font("Dialog", 0, 12));
        setLayeredPane(jLayeredPane1, "Statistic", buttonPass);
        setLayeredPane(jLayeredPane1, "Statistic", buttonResign);
        setLayeredPane(jLayeredPane2, "End-game info", jTextField7);
        setLayeredPane(jLayeredPane3, "DeadStonesBar", buttonResume);
        setLayeredPane(jLayeredPane3, "DeadStonesBar", buttonAgree);
        setLayeredPane(jLayeredPane3, "DeadStonesBar", buttonSend);

        setStatistics(jLabel9, 10, "Board size", jLayeredPane1);
        setStatistics(jLabel10, 30, "Stones on the board", jLayeredPane1);
        setStatistics(jLabel11, 50, "Black stones on the board", jLayeredPane1);
        setStatistics(jLabel12, 70, "White stones on the board", jLayeredPane1);
        setStatistics(jLabel13, 90, "Available black stones", jLayeredPane1);
        setStatistics(jLabel14, 110, "Available white stones", jLayeredPane1);
        setStatistics(jLabel15, 130, "Captured by black", jLayeredPane1);
        setStatistics(jLabel16, 150, "Captured by white", jLayeredPane1);

        setTextFields(jTextField8, 10, jLayeredPane1);
        setTextFields(jTextField9, 30, jLayeredPane1);
        setTextFields(jTextField10, 50, jLayeredPane1);
        setTextFields(jTextField11, 70, jLayeredPane1);
        setTextFields(jTextField12, 90, jLayeredPane1);
        setTextFields(jTextField13, 110, jLayeredPane1);
        setTextFields(jTextField14, 130, jLayeredPane1);
        setTextFields(jTextField15, 150, jLayeredPane1);

<<<<<<< HEAD
        jTextField8.setText("" + dimension);
=======
        jTextField8.setText("19");
>>>>>>> master

        buttonPass.setFont(new Font("Dialog", 0, 12));
        buttonPass.setText("Pass");
        buttonPass.setBounds(26, 180, 100, 25);
        buttonPass.addActionListener(e -> {
            try {
                manager.start(200, 200);
                //jeżeli gra zatrzymana
                if(manager != null && manager.getBoard().currentGame() == GamePhase.CHOOSING) {
                    gameStopped = true;
<<<<<<< HEAD
                    System.out.println("GameStopped");
=======
>>>>>>> master
                    jTabbedPane1.setEnabledAt(2, true);
                    jTabbedPane1.setSelectedIndex(2);
                }
                repaint();
            }
            catch (Exception ignored) {}
        });

        buttonResign.setFont(new Font("Dialog", 0, 12));
        buttonResign.setText("Resign");
        buttonResign.setBounds(152, 180, 100, 25);
        buttonResign.addActionListener(e -> {
            try {
                //TODO wysyła sygnał poddania aktywnego gracza
//                manager.start(100, 100);
                repaint();
            }
            catch (Exception ignored) {}
        });

        jLabel18.setFont(new Font("Dialog", 0, 12));
        jLabel18.setText("<html>Right-click on stones, those, in your opinion, are dead and click 'Send'." +
                "If you agree with situation on the board, click 'Agree'. If you want to resume the game, " +
                "choose 'Resume' button.</html>");
        jLabel18.setBounds(20, 20, 250, 100);
        jLayeredPane3.add(jLabel18, JLayeredPane.DEFAULT_LAYER);

        buttonResume.setFont(new Font("Dialog", 0, 12));
        buttonResume.setText("Resume");
        buttonResume.setBounds(10, 180, 80, 25);
        buttonAgree.setFont(new Font("Dialog", 0, 12));
        buttonAgree.setText("Agree");
        buttonAgree.setBounds(100, 180, 80, 25);
        buttonSend.setFont(new Font("Dialog", 0, 12));
        buttonSend.setText("Send");
        buttonSend.setBounds(190, 180, 80, 25);

        buttonSend.addActionListener(e -> {
            try {
                System.out.println("SEND");
<<<<<<< HEAD
                //TODO Wyślij do przeciwnika
                manager.getBoard().updateDeadStoneDecision(1);
                manager.getBoard().changeTurn();
                repaint();
            }
            catch (Exception ignored) {}
        });

        buttonAgree.addActionListener(e -> {
            try {
                System.out.println("AGREE");
                manager.getBoard().updateDeadStoneDecision(-1);
                System.out.println("STATE: " + manager.getBoard().currentGame());
                manager.getBoard().changeTurn();
                repaint();
            }
            catch (Exception ignored) {}
        });

        buttonResume.addActionListener(e -> {
            try {
                System.out.println("RESUME");
                gameStopped = false;
                System.out.println("Game Stopped = " + gameStopped);
                manager.getBoard().returnToMainPhase();
                manager.getBoard().changeTurn();
                jTabbedPane1.setEnabledAt(2, false);
                jTabbedPane1.setSelectedIndex(0);
=======
>>>>>>> master
                repaint();
            }
            catch (Exception ignored) {}
        });

        jTabbedPane1.setEnabledAt(2, false);

        setStatistics(jLabel4, 10, "Territory for black", jLayeredPane2);
        setStatistics(jLabel5, 30, "Territory for white", jLayeredPane2);
        setStatistics(jLabel6, 50, "Black score", jLayeredPane2);
        setStatistics(jLabel7, 70, "White score", jLayeredPane2);
        setStatistics(jLabel8, 120, "Winner", jLayeredPane2);

        setTextFields(jTextField3, 10, jLayeredPane2);
        setTextFields(jTextField4, 30, jLayeredPane2);
        setTextFields(jTextField5, 50, jLayeredPane2);
        setTextFields(jTextField6, 70, jLayeredPane2);

        jTextField7.setEditable(false);
        jTextField7.setBounds(150, 120, 110, 23);

        jPanel3.add(jTabbedPane1);
        jTabbedPane1.setBounds(20, 20, 280, 250);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 0, 810, 510);

        jMenu1.setText("File");
        jMenu1.setFont(new Font("Dialog", 0, 12));

        setMenuItems(jMenuItem1, jMenu1, "New game", e -> {
            check();
<<<<<<< HEAD
            manager = new ClientManager();
=======
            manager = new GameMoveManager();
>>>>>>> master
            repaint();
            ok = true;
        });
        setMenuItems(jMenuItem2, jMenu1, "Clear board", e -> {
<<<<<<< HEAD
            if(manager != null) {
                manager.destroyBoard();
                manager = null;
                repaint();
                zeroInfo();
                ok = true;
            }
=======
            manager.destroyBoard();
            manager = null;
            repaint();
            zeroInfo();
            ok = true;
>>>>>>> master
        });
        jMenu1.add(jSeparator1);
        setMenuItems(jMenuItem3, jMenu1, "New client/server", e -> new OpenSocketSession());
        setMenuItems(jMenuItem4, jMenu1, "Close connection", e -> {
            check();
            repaint();
        });
        jMenu1.add(jSeparator2);
        setMenuItems(jMenuItem5, jMenu1, "Exit", e -> {
            String p = "Do you want to exit the program";
            int c = JOptionPane.showConfirmDialog(null, p, "Information", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

            if (c == JOptionPane.OK_OPTION)
                System.exit(1);
        });

        jMenu2.setText("Modify");
        jMenu2.setFont(new Font("Dialog", 0, 12));
        jCheckBoxMenuItem1.setFont(new Font("Dialog", 0, 12));
        jCheckBoxMenuItem1.setText("Viewfinder");
        jMenu2.add(jCheckBoxMenuItem1);

        setGUIStyle();

        jMenu3.setText("Help");
        jMenu3.setFont(new Font("Dialog", 0, 12));
        jMenu3.add(jSeparator3);

        jMenuBar1.add(jMenu1);
        jMenuBar1.add(jMenu2);
        jMenuBar1.add(jMenu3);
        setJMenuBar(jMenuBar1);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-822)/2, (screenSize.height-561)/2, 822, 561);

        setVisible(true);
    }

    private void setPanelBorder(JPanel panel, String panelName) {
        panel.setBorder(BorderFactory.createTitledBorder(null, panelName, TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, new Font("Dialog", 0, 11)));
    }

    private void setStatistics(JLabel label, int position, String text, JLayeredPane layer) {
        label.setFont(new Font("Dialog", 0, 12));
        label.setText(text);
        label.setBounds(10, position, 170, 15);
        layer.add(label, JLayeredPane.DEFAULT_LAYER);
    }

    private void setTextFields(JTextField field, int position, JLayeredPane layer) {
        field.setFont(new Font("Courier", Font.BOLD, 9));
        field.setEditable(false);
        field.setBounds(200, position, 60, 19);
        layer.add(field, JLayeredPane.DEFAULT_LAYER);
    }

    private void setLayeredPane(JLayeredPane layer, String name, Component toAdd) {
        layer.setBackground(new Color(238, 238, 238));
        layer.setOpaque(true);
        layer.add(toAdd, JLayeredPane.DEFAULT_LAYER);
        jTabbedPane1.addTab(name, layer);
    }

    private void setMenuItems(JMenuItem item, JMenu menu, String name, ActionListener listener) {
        item.setFont(new Font("Dialog", 0, 12));
        item.setText(name);
        menu.add(item);
        item.addActionListener(listener);
    }

<<<<<<< HEAD
    private void reloadInfo() {
        if(manager.getBoard().getCurrentPlayer() == PlayerColor.BLACK)
=======
    private void reload() {
        if(manager.boardGraphic.getCurrentPlayer() == PlayerColor.BLACK)
>>>>>>> master
            jLabel17.setText("BLACK");
        if(manager.getBoard().getCurrentPlayer() == PlayerColor.WHITE)
            jLabel17.setText("WHITE");
        jTextField9.setText(manager.getBoard().getNumberStonesOnBoard() + "");
        jTextField10.setText(manager.getBoard().getNumberBlackStonesOnBoard() + "");
        jTextField11.setText(manager.getBoard().getNumberWhiteStonesOnBoard() + "");
        jTextField12.setText(manager.getBoard().getCurrentStonesB() + "");
        jTextField13.setText(manager.getBoard().getCurrentStonesW() + "");
        jTextField14.setText(manager.getBoard().getCapturedStonesB() + "");
        jTextField15.setText(manager.getBoard().getCapturedStonesW() + "");

        jTextField3.setText(manager.getBoard().getBlackTerritoryPoints() + "");
        jTextField4.setText(manager.getBoard().getWhiteTerritoryPoints() + "");
        jTextField5.setText(manager.getBoard().getBlackScore() + "");
        jTextField6.setText(manager.getBoard().getWhiteScore() + "");
<<<<<<< HEAD


        //TODO wyświetlanie zwycięzcy
//        if (!manager.getBoard().currentGame() && ok) {
//            gameStopped = true;
//            jTabbedPane1.setEnabledAt(2, true);
//            double b = manager.getBoard().getBlackScore();
//            double w = manager.getBoard().getWhiteScore();
//
//            if (b > w) {
//                ok = false;
//                jTextField7.setText("Black");
////                new ThreadForJOptionPane("Black");
//            }
//            else {
//                ok = false;
//                jTextField7.setText("White");
////                new ThreadForJOptionPane("White");
//            }
//        }
=======
>>>>>>> master
    }

    private void zeroInfo() {
        jTextField9.setText("");
        jTextField10.setText("");
        jTextField11.setText("");
        jTextField12.setText("");
        jTextField13.setText("");
        jTextField14.setText("");
        jTextField15.setText("");

        jTextField3.setText("");
        jTextField4.setText("");
        jTextField5.setText("");
        jTextField6.setText("");

        jTextField7.setText("");
    }

    private void initStylesForTextPane(JTextPane jTextPane1) {
        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

        Style regular = jTextPane1.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "SansSerif");

        Style red = jTextPane1.addStyle("bold_red", regular);
        StyleConstants.setForeground(red, new Color(255, 0, 0));
        StyleConstants.setBold(red, true);

        Style blue = jTextPane1.addStyle("bold_blue", regular);
        StyleConstants.setForeground(blue, new Color(0, 0, 255));
        StyleConstants.setBold(blue, true);
    }

    static void check() {
        try {
            if (window.manager != null) {
                if (window.manager.s != null)
                    window.manager.s.close();

                if (window.manager.socket != null)
                    window.manager.socket.close();

                window.manager = null;
            }

<<<<<<< HEAD
//            if (window.chat != null) {
//                if (window.chat.s != null)
//                    window.chat.s.close();
//
//                if (window.chat.socket != null)
//                    window.chat.socket.close();
//
//                window.chat = null;
//            }
=======
>>>>>>> master
        }
        catch (Exception ec) {
            ec.printStackTrace();
        }
    }

    private void setGUIStyle() {
        try	{
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
