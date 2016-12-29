import Basics.PlayerColor;
import Basics.Stone;
import org.imgscalr.Scalr;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import javax.imageio.*;

//TODO window może być singletonem!
//TODO automatyczne przełączanie pomiędzy zakładkami podczas przejścia z fazy głównej do wybierania i odwrotnie
//TODO okno(gra) jako obiekt posiadający stany (wzorzec). Send i inne przyciski powodują przejścia pomiędzy stanami
class GameWindow extends JFrame {
    private JCheckBoxMenuItem jCheckBoxMenuItem1;
    public MainPanel jPanel2;
    JTextPane jTextPane1;
    private JLabel jLabel16;
    public static JTabbedPane jTabbedPane1;
    private JTextField jTextField1;
    private JTextField jTextField2;
    private JTextField jTextField3;
    private JTextField jTextField4;
    private JTextField jTextField5;
    private JTextField jTextField6;
    private JTextField jTextField7;
    private JTextField jTextField8;
    private JTextField jTextField9;
    private JTextField jTextField10;
    private JTextField jTextField11;
    private JTextField jTextField12;
    public Log logArea;
    private JButton buttonAgree;
    private JButton buttonSend;
    private JButton buttonResume;
    private JButton buttonPass;

    public GameWindow window;
    Client manager;
    public boolean gameStopped;
    private int dimension;

    //TODO:ZARYS SINGLETONA. można poprawić, by było lepiej...
    /*static GameWindow instance;
    public static GameWindow GetInstance(int size){
        if (instance==null){
            if (instance.dimension != size)
                instance=new GameWindow(size);
        }
        return instance;
    }*/
    GameWindow(int dim) {
        window = this;
        dimension = dim;
        gameStopped = false;
        buildWindow();
    }

    class MainPanel extends JPanel {
        private Image boardIMG;
        private Image blackIMG;
        private Image whiteIMG;
        private Image blackTerritoryIMG;
        private Image whiteTerritoryIMG;
        private Image markup;

        private int height;
        private int base;
        private int leftX;
        private int topY;
        int lastX;
        int lastY;

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
                    int coordX = e.getY();
                    int coordY = e.getX();
                    int line = 0;
                    int column = 0;

                    for (int i = 18; i < 533; i += sizeOfField, line++) {
                        for (int j = 18; j < 483; j += sizeOfField, column++) {
                            if ((coordX >= i) && (coordX < (i + sizeOfField)) && (coordY >= j) && (coordY < (j + sizeOfField))) {
                                try {
                                    if (manager == null)
                                        return;

                                    //TODO pomyśleć nad tym, bo nie za ładne
                                    if (manager instanceof Client) {
                                        if (gameStopped) {
                                            if (manager.getBoard().getBoard()[line][column] != null && manager.playerColor == manager.getBoard().getCurrentPlayer()) {
                                                window.changeAgreeState(false);
                                                manager.getBoard().deadStones[line][column] = !manager.getBoard().deadStones[line][column];
                                                manager.start(line+200, column+200);
                                            }
                                        }
                                        else
                                            manager.start(line, column);
                                    }
                                    reloadInfo();
                                    repaint();
                                }
                                catch (Exception ec) {
                                    System.err.println("Error");
                                    ec.printStackTrace();
                                }
                            }
                        }
                        column = 0;
                    }
                }

                public void mouseClicked(MouseEvent e) {}
                public void mouseReleased(MouseEvent e) {}
                public void mouseEntered(MouseEvent e) {}
                public void mouseExited(MouseEvent e) {}
            });
            addMouseMotionListener(new MouseMotionListener() {
                public void mouseMoved (MouseEvent e){
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

                    for (int i = 18; i < 533; i += sizeOfField)
                        for (int j = 18; j < 483; j += sizeOfField) {
                            if ((coord_x >= i) && (coord_x < (i + sizeOfField)) && (coord_y >= j) && (coord_y < (j + sizeOfField))) {
                                leftX = j + xViewfinder;
                                topY = i + yViewfinder;
                                repaint();
                            }
                        }
                }

                public void mouseDragged(MouseEvent e) {}
            });
        }

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
                char[][] tmp2 = manager.getBoard().getTerritoryPointsBoard();
                insertTerritoryStones(tmp2, g);
                insertStoneGraphics(manager.getBoard().getBoard(), g);
                insertDeadMarksStones(manager.getBoard().deadStones, g);
            }
            Rectangle2D rect = new Rectangle2D.Double(leftX, topY, base, height);
            g2.setPaint(Color.RED);
            g2.draw(rect);

            Rectangle2D circle = new Rectangle2D.Double(lastX, lastY, height, base);
            g2.setPaint(Color.GREEN);
            g2.fill(circle);

            try {
                reloadInfo();
            }
            catch (Exception ignored) {}
        }

        void insertStoneGraphics(Stone[][] temp, Graphics g) {
            for (int i = 0; i < dimension; i++)
                for (int j = 0; j < dimension; j++) {
                    if ((temp[i][j] != null) && temp[i][j].getColor() == PlayerColor.WHITE)
                        g.drawImage(whiteIMG, (j * sizeOfField) + xAdd, (i * sizeOfField) + yAdd, null);
                    else if ((temp[i][j] != null) && temp[i][j].getColor() == PlayerColor.BLACK)
                        g.drawImage(blackIMG, (j * sizeOfField) + xAdd, (i * sizeOfField) + yAdd, null);
                }
        }

        void insertTerritoryStones(char[][] temp, Graphics g) {
            for (int i = 0; i < dimension; i++)
                for (int j = 0; j < dimension; j++) {
                    if (temp[i][j] == 'W')
                        g.drawImage(whiteTerritoryIMG, (j * sizeOfField) + xTer, (i * sizeOfField) + yTer, null);
                    else if (temp[i][j] == 'B')
                        g.drawImage(blackTerritoryIMG, (j * sizeOfField) + xTer, (i * sizeOfField) + yTer, null);
                }
        }

        void insertDeadMarksStones(boolean[][] tmp, Graphics g) {
            if (gameStopped) {
                for (int i = 0; i < dimension; i++)
                    for (int j = 0; j < dimension; j++) {
                        if (tmp[i][j])
                            g.drawImage(markup, (j * sizeOfField) + xTer, (i * sizeOfField) + yTer, null);
                    }
            }
        }
    }

    private void buildWindow() {
        JPanel jPanel1 = new JPanel();
        jPanel2 = new MainPanel();
        JPanel jPanel3 = new JPanel();
        JPanel jPanel4 = new JPanel();
        JPanel jPanel5 = new JPanel();
        JPanel jPanel6 = new JPanel();
        jTextPane1 = new JTextPane();

        jTabbedPane1 = new JTabbedPane();
        JLayeredPane jLayeredPane1 = new JLayeredPane();
        JLayeredPane jLayeredPane2 = new JLayeredPane();
        JLayeredPane jLayeredPane3 = new JLayeredPane();

        JLabel jLabel1 = new JLabel();
        JLabel jLabel2 = new JLabel();
        JLabel jLabel3 = new JLabel();
        JLabel jLabel4 = new JLabel();
        JLabel jLabel5 = new JLabel();
        JLabel jLabel6 = new JLabel();
        JLabel jLabel7 = new JLabel();
        JLabel jLabel8 = new JLabel();
        JLabel jLabel9 = new JLabel();
        JLabel jLabel10 = new JLabel();
        JLabel jLabel11 = new JLabel();
        JLabel jLabel12 = new JLabel();
        JLabel jLabel13 = new JLabel();
        JLabel jLabel14 = new JLabel();
        JLabel jLabel15 = new JLabel();
        jLabel16 = new JLabel();

        jTextField1 = new JTextField();
        jTextField2 = new JTextField();
        jTextField3 = new JTextField();
        jTextField4 = new JTextField();
        jTextField6 = new JTextField();
        jTextField5 = new JTextField();
        JTextField jTextField8 = new JTextField();
        jTextField6 = new JTextField();
        jTextField7 = new JTextField();
        this.jTextField8 = new JTextField();
        jTextField9 = new JTextField();
        jTextField10 = new JTextField();
        jTextField11 = new JTextField();
        jTextField12 = new JTextField();

        buttonPass = new JButton();
        JButton buttonResign = new JButton();
        buttonResume = new JButton();
        buttonAgree = new JButton();
        buttonSend = new JButton();
        buttonResume = new JButton();

        JSeparator jSeparator3 = new JSeparator();
        JMenuBar jMenuBar1 = new JMenuBar();
        JMenu jMenu1 = new JMenu();
        JMenu jMenu2 = new JMenu();
        JMenu jMenu3 = new JMenu();
        jCheckBoxMenuItem1 = new JCheckBoxMenuItem();
        JMenuItem jMenuItem5 = new JMenuItem();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                String p = "Do you want to exit the program";
                int c = JOptionPane.showConfirmDialog(null, p, "Information", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (c == JOptionPane.OK_OPTION) {
                    if(manager.playerColor == PlayerColor.BLACK)
                        new ThreadForJOptionPane("Black", window);
                    else
                        new ThreadForJOptionPane("White", window);
                }
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

        jTextPane1.setEditable(false);
        initStylesForTextPane(jTextPane1);

        jPanel2.add(jLabel1);
        jPanel2.setBounds(310, 0, 500, 510);
        jLabel1.setBounds(10, 20, 480, 480);
        jPanel1.add(jPanel2);

        jPanel1.add(jPanel3);
        jPanel3.setBounds(0, 220, 310, 290);

        logArea = new Log(5, 10);
        jPanel4.setBounds(0, 0, 310, 145);
        logArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.getViewport().setViewPosition(new Point(0, logArea.getDocument().getLength()));
        scroll.setBounds(5, 15, 300, 122);
        DefaultCaret caret = (DefaultCaret)logArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        jPanel4.add(scroll);
        jPanel1.add(jPanel4);

        jPanel5.setBounds(0, 145, 310, 75);
        jLabel16.setFont(new Font("Courier New", Font.BOLD, 30));
        jLabel16.setBounds(0, 0, 20, 70);
        jPanel5.add(jLabel16);
        jPanel1.add(jPanel5);

        jTabbedPane1.setFont(new Font("Dialog", 0, 12));
        setLayeredPane(jLayeredPane1, "Game", buttonPass);
        setLayeredPane(jLayeredPane1, "Game", buttonResign);
        setLayeredPane(jLayeredPane3, "DeadStones", buttonResume);
        setLayeredPane(jLayeredPane3, "DeadStones", buttonAgree);
        setLayeredPane(jLayeredPane3, "DeadStones", buttonSend);
        setLayeredPane(jLayeredPane2, "End-game", jTextField5);

        setStatistics(jLabel6, 10, "Board size", jLayeredPane1);
        setStatistics(jLabel7, 30, "Stones on the board", jLayeredPane1);
        setStatistics(jLabel8, 50, "Black stones on the board", jLayeredPane1);
        setStatistics(jLabel9, 70, "White stones on the board", jLayeredPane1);
        setStatistics(jLabel10, 90, "Available black stones", jLayeredPane1);
        setStatistics(jLabel11, 110, "Available white stones", jLayeredPane1);
        setStatistics(jLabel12, 130, "Captured by black", jLayeredPane1);
        setStatistics(jLabel13, 150, "Captured by white", jLayeredPane1);

        setTextFields(jTextField8, 10, jLayeredPane1);
        setTextFields(jTextField6, 30, jLayeredPane1);
        setTextFields(jTextField7, 50, jLayeredPane1);
        setTextFields(this.jTextField8, 70, jLayeredPane1);
        setTextFields(jTextField9, 90, jLayeredPane1);
        setTextFields(jTextField10, 110, jLayeredPane1);
        setTextFields(jTextField11, 130, jLayeredPane1);
        setTextFields(jTextField12, 150, jLayeredPane1);

        jTextField8.setText("" + dimension);

        buttonPass.setFont(new Font("Dialog", 0, 12));
        buttonPass.setText("Pass");
        buttonPass.setBounds(26, 180, 100, 25);
        buttonPass.addActionListener(e -> {
            //double-pass - przeciwnik wysłał wcześniej pass
            if(manager.getBoard().ifDoublePass(manager.playerColor)) {
                try {
                    manager.start(100, 2);
                }
                catch (Exception ignored) {
                }
            }
            //one pass
            else {
                try {
                    manager.start(100, 1);
                } catch (Exception ignored) {
                }
            }
            repaint();
        });

        buttonResign.setFont(new Font("Dialog", 0, 12));
        buttonResign.setText("Resign");
        buttonResign.setBounds(152, 180, 100, 25);
        buttonResign.addActionListener(e -> {
            try {
                //TODO wysyła sygnał poddania aktywnego gracza
//                manager.start(100, 100);
                repaint();
            } catch (Exception ignored) {
            }
        });

        jLabel14.setFont(new Font("Dialog", 0, 12));
        jLabel14.setText("<html>Right-click on stones, those, in your opinion, are dead and click 'Send'." +
                "If you agree with situation on the board, click 'Agree'. If you want to resume the game, " +
                "choose 'Resume' button.</html>");
        jLabel14.setBounds(20, 20, 250, 100);
        jLayeredPane3.add(jLabel14, JLayeredPane.DEFAULT_LAYER);

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
                manager.start(20, 20);
                repaint();
            } catch (Exception ignored) {
            }
        });

        buttonAgree.addActionListener(e -> {
            try {
                //double-agree
                if(manager.getBoard().ifDoubleAgree(manager.playerColor))
                    manager.start(30, 2);
                //one agree
                else
                    manager.start(30, 1);
                repaint();
            } catch (Exception ignored) {
            }
        });

        buttonResume.addActionListener(e -> {
            try {
                manager.start(40, 40);
                repaint();
            } catch (Exception ignored) {
            }
        });

//        jTabbedPane1.setEnabledAt(1, false);
        buttonAgree.setEnabled(false);
        buttonSend.setEnabled(false);
        buttonResume.setEnabled(false);

        setStatistics(jLabel2, 10, "Territory for black", jLayeredPane2);
        setStatistics(jLabel3, 30, "Territory for white", jLayeredPane2);
        setStatistics(jLabel4, 50, "Black score", jLayeredPane2);
        setStatistics(jLabel5, 70, "White score", jLayeredPane2);
        setStatistics(jLabel15, 120, "Winner", jLayeredPane2);

        setTextFields(jTextField1, 10, jLayeredPane2);
        setTextFields(jTextField2, 30, jLayeredPane2);
        setTextFields(jTextField3, 50, jLayeredPane2);
        setTextFields(jTextField4, 70, jLayeredPane2);

        jTextField5.setEditable(false);
        jTextField5.setBounds(150, 120, 110, 23);

        jPanel3.add(jTabbedPane1);
        jTabbedPane1.setBounds(20, 20, 280, 250);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 0, 810, 510);

        jMenu1.setText("File");
        jMenu1.setFont(new Font("Dialog", 0, 12));

        setMenuItems(jMenuItem5, jMenu1, "Exit", e -> {
            String p = "Do you want to exit the program";
            int c = JOptionPane.showConfirmDialog(null, p, "Information", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (c == JOptionPane.OK_OPTION) {
                if(manager.playerColor == PlayerColor.BLACK)
                    new ThreadForJOptionPane("Black", window);
                else
                    new ThreadForJOptionPane("White", window);
            }
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
        setBounds((screenSize.width - 822) / 2, (screenSize.height - 561) / 2, 822, 561);
        setVisible(true);
    }

    void changeAgreeState(boolean b) {
        if(gameStopped) {
            buttonAgree.setEnabled(b);
        }
    }

    void changePhase(boolean toChoosing) {
        System.out.println(manager.playerColor + ": " + toChoosing);
        gameStopped = toChoosing;
        buttonSend.setEnabled(toChoosing);
        buttonResume.setEnabled(toChoosing);
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

    private void reloadInfo()  {
        try {
            if (manager.getBoard().getCurrentPlayer() == PlayerColor.BLACK)
                jLabel16.setText("BLACK");
            if (manager.getBoard().getCurrentPlayer() == PlayerColor.WHITE)
                jLabel16.setText("WHITE");
            jTextField6.setText(manager.getBoard().getNumberStonesOnBoard() + "");
            jTextField7.setText(manager.getBoard().getNumberBlackStonesOnBoard() + "");
            jTextField8.setText(manager.getBoard().getNumberWhiteStonesOnBoard() + "");
            jTextField9.setText(manager.getBoard().getCurrentStonesB() + "");
            jTextField10.setText(manager.getBoard().getCurrentStonesW() + "");
            jTextField11.setText(manager.getBoard().getCapturedStonesB() + "");
            jTextField12.setText(manager.getBoard().getCapturedStonesW() + "");
            jTextField1.setText(manager.getBoard().getBlackTerritoryPoints() + "");
            jTextField2.setText(manager.getBoard().getWhiteTerritoryPoints() + "");
            jTextField3.setText(manager.getBoard().getBlackScore() + "");
            jTextField4.setText(manager.getBoard().getWhiteScore() + "");

            if (manager.getBoard().getBlackScore() > manager.getBoard().getWhiteScore())
                jTextField5.setText("Black");
            else if(manager.getBoard().getBlackScore() < manager.getBoard().getWhiteScore())
                jTextField5.setText("White");
        }
        catch(Exception ignored){}
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

    private void check() {
        try {
            if (window.manager != null) {
                if (window.manager.s != null)
                    window.manager.s.close();
                if (window.manager.socket != null)
                    window.manager.socket.close();
                window.manager = null;
            }
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
