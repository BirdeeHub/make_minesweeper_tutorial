import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.util.TimerTask;
import java.util.Timer;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JScrollBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
//This is the main game board display window. contains action listeners and displays control buttons, and a Grid instance, which is the game board.
public class MainGameWindow extends javax.swing.JFrame {
    //--------------Initialize-----------------------------
    private final int Fieldx, Fieldy, bombCount, lives;
    private final Color PURPLE = new Color(58, 0, 82);
    private final Color GREEN = new Color(0, 255, 0);
    private final Dimension DefaultWindowSize = new Dimension(830, 830);
    private JLabel timeDisplay = new JLabel();  // Create Timer Displayer
    private final Timer displayTimer = new Timer();
    private final TimerTask timeDisplayTask = new TimerTask() {
        public void run() {
            timeDisplay.setText(grid.getTime());//if you want to add a time format you can do that here
        }
    };
    private JLabel GameOverDisplay = new JLabel();
    private JLabel BombsFoundDisplay = new JLabel();
    private JLabel livesLostDisplay = new JLabel();//our 3 display labels and functions to set them.
    private final String highScoreMessage = "Record Time!";//to change text of messages in menubar display, edit from here
    private final String newBoardSizeAndWonMessage = "New Board Cleared!";
    private final String wonAndNotHighScoreMessage = "Cleared!";//compiler will treat it the same as having it actually in setGameOverDisplay()
    private final String diedButNewBoardMessage = "1st Board Death";//so having these here is just for readability
    private final String diedAndNotNewBoardMessage = "Exploded...";
    private void setBombsFoundDisplay(){BombsFoundDisplay.setText("M:" + Integer.toString(grid.getBombsFound()) + "/" + Integer.toString(bombCount));}
    private void setLivesLostDisplay(){livesLostDisplay.setText("L:" + Integer.toString(grid.getLivesLeft()) + "/" + Integer.toString(lives));}//and end edit here
    private void setGameOverDisplay(){
        int[] GOIndex = new int[2];//GOIndex[0] is message index, GOIndex[1] is won value.
        GOIndex = grid.getGameOverIndex();//if not in game over state, these will both be 3.
        if(GOIndex[1]==1){ GameOverDisplay.setText((GOIndex[0]==2)?highScoreMessage:((GOIndex[0]==1)?newBoardSizeAndWonMessage:wonAndNotHighScoreMessage));
        }else if(GOIndex[1]==0){ GameOverDisplay.setText((GOIndex[0]==1)?diedButNewBoardMessage:diedAndNotNewBoardMessage);
        }else GameOverDisplay.setText("");
    }
    private Grid grid;//<-- Game Board Class (action listeners not included)
    //things for listeners that needed to persistance across buttons or are used in different scopes
    private JScrollPane scrollPane;
    private JToggleButton markToggle = new JToggleButton("Mark");
    private JToggleButton chordToggle = new JToggleButton("Chord");
    private boolean LMB = false;
    private boolean RMB = false;
    private JButton currentButton = null;
//---------------------MainGameWindow CONSTRUCTOR----------------------MainGameWindow CONSTRUCTOR----------------------------MainGameWindow CONSTRUCTOR------------------------------
    public MainGameWindow(int w, int h, int bombNum, int lives) {
        Fieldx = w;
        Fieldy = h;
        this.lives = lives;
        bombCount = bombNum;
        grid = new Grid(Fieldx, Fieldy, bombCount, lives);//<-- Generate game board
        displayTimer.scheduleAtFixedRate(timeDisplayTask, 0, 50);//this just displays the time in Minefield answers.
        scrollPane = new JScrollPane(grid);
        addGridActionListeners();
        initComponents();
    }
    private void addGridActionListeners(){
        grid.addCellListener(new MouseAdapter() {//add our listener to each cell.
            @Override
            public void mouseEntered(MouseEvent e){//allows the 1.5 click trick by actually getting the component the mouse is over rather than just
                currentButton=(JButton)e.getSource();// getting the component that fired the mousePressed and released actions which will always be the same
            }//                                         ^ this is because each listener is associated with a particular button, and contains both methods
            @Override
            public void mousePressed(MouseEvent e) {
                if(SwingUtilities.isLeftMouseButton(e)){
                    LMB = true;
                    if(currentButton!=null){
                        if(chordToggle.isSelected()){
                            grid.doClickType(currentButton, 2);
                        }else if(markToggle.isSelected()){
                            grid.doClickType(currentButton, 1);//mark
                        }else if(RMB){
                            grid.doClickType(currentButton, 2);//Chord
                        }else{
                            grid.doClickType(currentButton, 0);//regular
                        }
                    }
                }
                if(SwingUtilities.isRightMouseButton(e)){//same thing but for right click, so no need for clickType 0 or marktoggle check
                    RMB = true;
                    if(currentButton!=null){
                        if(LMB){
                            grid.doClickType(currentButton, 2);
                        }else{
                            grid.doClickType(currentButton, 1);
                        }
                    }
                }
                setBombsFoundDisplay();
                setLivesLostDisplay();
                setGameOverDisplay();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if(SwingUtilities.isLeftMouseButton(e)){
                    LMB = false;
                }
                if(SwingUtilities.isRightMouseButton(e)){
                    RMB = false;
                }
            }
        });
        grid.addMouseWheelListener(new MouseWheelListener() {//zoom and scroll (only active over grid)
            JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
            JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if(e.isControlDown()){
                    //initialize stuff we need to know for recentering before changing zoom
                    PointerInfo pointerInfo = MouseInfo.getPointerInfo();
                    Point mouseLocation = pointerInfo.getLocation();
                    SwingUtilities.convertPointFromScreen(mouseLocation, grid);//<-- find mouse relative to grid at function call
                    int mouseX1 = mouseLocation.x;
                    int mouseY1 = mouseLocation.y;
                    int sizeX1 = grid.getWidth();//mouse location wont change after pack even if remeasured apparently but size will?
                    int sizeY1 = grid.getHeight();
                    getContentPane().setPreferredSize(MainGameWindow.this.getContentPane().getSize());//<-- stop it from reverting to old size
                    //get wheel and cell size info and do zoom
                    grid.doZoom(e.getWheelRotation());
                    pack();
                    getContentPane().revalidate();
                    //make sure it keeps the spot the mouse is over in more or less the same place on the board. not exact due to integer division
                    int mouseX2 = (grid.getWidth() * mouseX1)/sizeX1;
                    int mouseY2 = (grid.getHeight() * mouseY1)/sizeY1;
                    int scrollAmountX = mouseX2 - mouseX1;
                    int scrollAmountY = mouseY2 - mouseY1;
                    verticalScrollBar.setValue(verticalScrollBar.getValue() + scrollAmountY);
                    horizontalScrollBar.setValue(horizontalScrollBar.getValue() + scrollAmountX);
                } else {//if no control key, do scroll
                    verticalScrollBar.setUnitIncrement(20);
                    verticalScrollBar.setValue(verticalScrollBar.getValue() + (e.getUnitsToScroll() * verticalScrollBar.getUnitIncrement()));
                }
            }
        });
    }
    private void initComponents() {//-------------------initComponents() on window below-----------------------------------------------------------------------------
        scrollPane.setBackground(PURPLE);
        JMenuBar menuBar = new JMenuBar();
        JPanel menuPanel = new JPanel(new GridBagLayout());
        GridBagConstraints menuBagConstraints = new GridBagConstraints();
        menuBar.add(menuPanel);
        JButton NewGame = new JButton("New Game");
        JButton Reset = new JButton("Reset");
        JButton HowToPlay = new JButton("Help");
        JToggleButton toggleQuestionMarking = new JToggleButton("?'s?");
        JButton ScoreBoard = new JButton("HiSc");
        setBombsFoundDisplay();
        setLivesLostDisplay();
        setGameOverDisplay();
        BombsFoundDisplay.setHorizontalAlignment(SwingConstants.CENTER);
        livesLostDisplay.setHorizontalAlignment(SwingConstants.CENTER);
        GameOverDisplay.setHorizontalAlignment(SwingConstants.CENTER);
        timeDisplay.setHorizontalAlignment(SwingConstants.CENTER);
        Font ScoreAreaFontSize = new Font("Tahoma", 0, 20);
        BombsFoundDisplay.setFont(ScoreAreaFontSize);
        livesLostDisplay.setFont(ScoreAreaFontSize);
        GameOverDisplay.setFont(ScoreAreaFontSize);
        timeDisplay.setFont(ScoreAreaFontSize);
        menuPanel.setBackground(PURPLE);
        timeDisplay.setForeground(GREEN);
        BombsFoundDisplay.setForeground(GREEN);
        livesLostDisplay.setForeground(GREEN);
        GameOverDisplay.setForeground(GREEN);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setBackground(PURPLE);
        scrollPane.getHorizontalScrollBar().setBackground(PURPLE);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setPreferredSize(DefaultWindowSize);
        //component adding and layout managing
        menuBagConstraints.gridx =0;
        menuBagConstraints.gridy =0;
        menuBagConstraints.gridwidth =1;
        menuBagConstraints.gridheight =1;
        menuBagConstraints.weightx = 0.0;
        menuBagConstraints.fill = GridBagConstraints.BOTH;
        menuPanel.add(markToggle, menuBagConstraints);
        menuBagConstraints.gridx =1;
        menuPanel.add(chordToggle, menuBagConstraints);
        menuBagConstraints.gridx =2;
        menuPanel.add(toggleQuestionMarking, menuBagConstraints);
        menuBagConstraints.gridx =3;
        menuBagConstraints.weightx = 0.25;
        menuPanel.add(BombsFoundDisplay, menuBagConstraints);
        menuBagConstraints.gridx =4;
        menuPanel.add(livesLostDisplay, menuBagConstraints);
        menuBagConstraints.gridx =5;
        menuBagConstraints.weightx = 1.0;
        menuPanel.add(timeDisplay, menuBagConstraints);
        menuBagConstraints.weightx = 0.75;
        menuBagConstraints.gridx =6;
        menuPanel.add(GameOverDisplay, menuBagConstraints);
        menuBagConstraints.gridx =7;
        menuBagConstraints.weightx = 0.0;
        menuPanel.add(ScoreBoard, menuBagConstraints);
        menuBagConstraints.gridx =8;
        menuPanel.add(HowToPlay, menuBagConstraints);
        menuBagConstraints.gridx =9;
        menuPanel.add(Reset, menuBagConstraints);
        menuBagConstraints.gridx =10;
        menuPanel.add(NewGame, menuBagConstraints);
        setJMenuBar(menuBar);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        pack();
        getContentPane().revalidate();
        grid.setCellFontSize();
        //------------------misc action listeners----------------------misc action listeners-------------misc action listeners-------------misc action listeners------
        Reset.addActionListener(new ActionListener() {//reset button
            long clickmemory = System.currentTimeMillis();//<-- this is for protection from spamming
            public void actionPerformed(ActionEvent evt) {
                getContentPane().setPreferredSize(MainGameWindow.this.getContentPane().getSize());//<-- stop it from reverting to old size
                if((System.currentTimeMillis()-clickmemory)>1000){//<-- this means 1s has passed since last click
                    clickmemory = System.currentTimeMillis();//<-- if so, update clickmemory
                    grid.ResetBoard();
                    setBombsFoundDisplay();
                    setLivesLostDisplay();
                    setGameOverDisplay();
                    MainGameWindow.this.pack();
                    getContentPane().revalidate();
                }else clickmemory = System.currentTimeMillis();//if you spammed, update clickmemory
            }
        });
        toggleQuestionMarking.addActionListener(new ActionListener(){//----toggles the ? option for marking cells on and off
            public void actionPerformed(ActionEvent e){
                grid.toggleQuestionMarks();
            }
        });
        NewGame.addActionListener(new ActionListener() {//new game
            public void actionPerformed(ActionEvent evt) {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        new OpeningWindow().setVisible(true);
                    }
                });
                MainGameWindow.this.dispose();
            }
        });
        HowToPlay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        new instructionsWindow().setVisible(true);
                    }
                });
            }
        });
        ScoreBoard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        new ScoresWindow(Fieldx, Fieldy, bombCount, lives, MainGameWindow.this).setVisible(true);
                    }//clickable is false at start so you dont cancel ur game on accident.
                });
            }
        });
        KeyAdapter keyAdapter = new KeyAdapter() {//enter key functionality
            public void keyPressed(KeyEvent evt) {
                // Check if the Enter key is pressed
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    // get focused component source
                    Component CurrComp = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                    if(CurrComp instanceof JToggleButton){
                        ((JToggleButton)CurrComp).doClick();
                    }else ((JButton)CurrComp).doClick();
                }
            }
        };
        ScoreBoard.addKeyListener(keyAdapter);
        NewGame.addKeyListener(keyAdapter);
        Reset.addKeyListener(keyAdapter);
        HowToPlay.addKeyListener(keyAdapter);
        markToggle.addKeyListener(keyAdapter);
        toggleQuestionMarking.addKeyListener(keyAdapter);
        chordToggle.addKeyListener(keyAdapter);

        getContentPane().setVisible(true);
    }
}