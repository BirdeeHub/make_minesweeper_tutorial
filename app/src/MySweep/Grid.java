package MySweep;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseListener;
import java.util.Stack;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import java.awt.Graphics;
import javax.swing.SwingConstants;
import javax.swing.Icon;
import java.awt.geom.AffineTransform;
import java.awt.Component;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import javax.swing.ImageIcon;

//This class controls the behavior of the game board. Contains cell display and action logic, and uses Minefield to keep track of the game state
public class Grid extends JPanel {
    //--------------Initialize-Colors--------Many are the same color, but now theyre easily changed?
    private final Icon DefaultButtonIcon = (new JButton()).getIcon();//<-- used for non darkmode
    private final Color LightModeTextColor = new Color(0);//<-- LightMode button foreground
    private final Color DarkModeTextColor = new Color(255, 255, 255);//<-- DarkMode button foreground
    private final Color BLACK = new Color(0);//<-- default button background color in dark mode
    private final Color GRASS = new Color(31, 133, 28);//<-- grass
    private final Color MAGENTA = new Color(255,0,255);//<-- game over marked status color 1
    private final Color BLUE = new Color(0,0,255);//<-- game over marked status color 2
    private final Color RED = new Color(255,0,0);//<-- in game exploded bomb background
    private final Color ChGO_RED = new Color(255,0,0);//<-- game over indicator on chord number foreground
    private final Color CYAN = new Color(0,255,200);//<-- exploded bomb foreground
    private final Color QSTNMARKCOLOR = new Color(133, 95, 227);//<-- question mark color
    private final Color MARKCOLOR = new Color(255,0,0);//<-- color of marks
    private final Color BORDERYELLOW = new Color(255, 255, 0);//<-- border colors 2
    private final Color BORDERORANGE = new Color(255, 160, 0);//<-- 3
    private final Color BORDERORANGERED = new Color(255,95,0);//<-- 4
    private final Color BORDERRED = new Color(255,0,0);//<-- end of border colors
    private final Color defaultBorderColor = new Color(126, 126, 126);//<-- default border color
    private final Insets CellInset = new Insets(-20, -20, -20, -20);//<-- leave this alone unless you want dots instead of numbers
    private boolean DarkMode = true;//<-- starts in darkMode by default.(toggle in help window)
    //--------------------get custom image files----------------------------------------------
    private final String classpath = System.getProperty("java.class.path");//<-- added this for people who use the run command in their IDE
    private final boolean inAJar = classpath.contains(".jar");//<-- this might make some bugs. I hope not though. Works fine for me.
    //if you are down to just run the compile script and run it from the jar, you can replace the above 2 lines with: private boolean inAJar = true;
    private final Image EXPicon = new ImageIcon(getClass().getResource(((inAJar)?"/src/MySweep/":"") + "Icons/GameOverExplosion.png")).getImage();
    private final Image RVLicon = new ImageIcon(getClass().getResource(((inAJar)?"/src/MySweep/":"") + "Icons/MineSweeperIcon.png")).getImage();
    //-------------logic initializing-----------------------------logic initializing--------------logic initializing---------------------------------logic initializing-----
    private final int Fieldx, Fieldy, bombCount, lives;
    private final String scoresFileNameWindows = System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Roaming" + File.separator + "minesweeperScores" + File.separator + "Leaderboard.txt";
    private final String scoresFileNameOther = System.getProperty("user.home") + File.separator + ".minesweeper" + File.separator + "Leaderboard.txt";
    private boolean cancelQuestionMarks = true;//<-- boolean for toggling ? marks on bombs
    private int GameOverMessageIndex = 3;
    private int wonValue = 3;
    private int BombsFound = 0;
    private int livesLeft = 0;
    private Minefield answers;//<-- this one is the data class for game logic
    //-----------------------------------CellButton();-----------------------------------------CellButton();-----------------------------------------
    private class CellButton extends JButton {//these are our game board buttons. This class was created to allow the 
        private int borderWeight = 1;         //^setting of color and thickness of cell borders, now it knows where it is.
        private Color borderColor = defaultBorderColor;//<--not defined inside class because that would mean creating a new color for each button, which is expensive.
        private int x, y;
        public CellButton() {//<-- -Constructor
            this.setFocusable(false);//<-- made unfocusable because i didnt like hitting tab for 3 years
            this.setHorizontalAlignment(SwingConstants.CENTER);//<-- they used to be in their own loop but this is the same thing
            this.setVerticalAlignment(SwingConstants.CENTER);//Initialize our cell button properties other than font size (which is done after packing layout)
            this.setMargin(CellInset);//<-- defined outside of class so not multiplied times whatever
        }
        public void setBorderColor(Color borderColor, int borderWeight) {
            this.borderColor = borderColor;
            this.borderWeight = borderWeight;
        }
        public void setXY(int x, int y){
            this.x = x;//<-- this used to be a putClientProperty item
            this.y = y;
        }
        public int getXcoord(){return x;}
        public int getYcoord(){return y;}
        
        @Override
        protected void paintBorder(Graphics g) {//<-- override paintBorder so that I can change border color and thickness
            if(borderColor!=null){
                g.setColor(borderColor);
                int top;
                if(borderColor == defaultBorderColor){top = borderWeight;
                }else top = borderWeight*(1+(this.getSize().height/23));
                for(int i=0;i<top;i++)g.drawRect(i, i, getWidth() - i*2 - 1, getHeight() - i*2 -1);
            }
        }
    }
    //--------------------------------------------Scaleable Icon-----------------ScaleableIcon();-----------Scaleable Icon----------------
    private class ScalableIcon implements Icon {//currently only used in GameOver function
        private ImageIcon originalIcon;//<-- getting original icon for sizing purposes
        public ScalableIcon(ImageIcon originalIcon) {//<-- Constructor
            this.originalIcon = originalIcon;
        }//Stuff for resizing Icon.
        public int getIconWidth() {return originalIcon.getIconWidth();}
        public int getIconHeight() {return originalIcon.getIconHeight();}
        public void paintIcon(Component c, Graphics g, int x, int y) {
            int width = c.getWidth();
            int height = c.getHeight();
            Graphics2D g2d = (Graphics2D) g.create();//<-- i hate these because i have to look for stuff like
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);//<-- RenderingHints
            AffineTransform transform = new AffineTransform();//<-- or the stretch but with matricies that i couldnt remember the name of
            transform.scale((double) width / originalIcon.getIconWidth(), (double) height / originalIcon.getIconHeight());// but im sure
            g2d.drawImage(originalIcon.getImage(), transform, null);// if i get used to it it could be powerful
            g2d.dispose();//<--                                      BUT. it turned my low effort 25 pixel explosion made in paint
        }//                                                        into a thing that doesnt look like it used to be square so thats cool.
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------GRID CONSTRUCTOR----------------------GRID CONSTRUCTOR----------------------------GRID CONSTRUCTOR------------------------------
    public Grid(int w, int h, int bombNum, int lives) {//INIT
        Fieldx = w;
        Fieldy = h;                                      //THE
        this.lives = lives;
        bombCount = bombNum;                               //GRID
        BombsFound = 0;
        livesLeft = lives;
        answers = new Minefield(Fieldx, Fieldy, bombCount);
        Grid.this.setLayout(new GridLayout(Fieldy, Fieldx));
        Grid.this.setOpaque(false);
        for(int i = 0; i < Fieldx; i++){
            for(int j = 0; j < Fieldy; j++){
                Grid.this.add(new CellButton());//<-- add the buttons to the grid.
            }
        }
        for(int i = 0; i < Fieldx; i++){
            for(int j = 0; j < Fieldy; j++){
                getButtonAt(i,j).setXY(i,j);//order of button add irrelevant because (i,j)=(i,j)
            }
        }
        if(DarkMode)for(int i = 0; i < Fieldx; i++)for(int j = 0; j < Fieldy; j++)getButtonAt(i,j).setBackground(BLACK);
    }
    //--------------------getButtonAt(int x, int y) Became necessary after getting rid of 2d array to reference cells by location-------------------------
    private CellButton getButtonAt(int x, int y) {
        return (CellButton) Grid.this.getComponent(y * Fieldx + x);
    }
    //-----------------------------------function for adding mouse Listener to cells in Grid-------------------------------------------------
    void addCellListener(MouseListener mouseListener){//----------addCellListener()--------------------------------------------------
        for (int i = 0; i < Fieldx; i++) {
            for (int j = 0; j < Fieldy; j++) {
                getButtonAt(i,j).addMouseListener(mouseListener);
            }//The short time where your click wont immediately update the visuals yet after board becomes visible on large board sizes
        }//                               ^ isnt due to this function its just because its java (tm).
    }//----------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void paintComponent(Graphics g) {//<--Look what you have to do to make the background GRASS without making the components also GRASS??
            g.setColor(GRASS);
            g.fillRect(0, 0, getWidth(), getHeight());
    }
    //--------------------------------------------Misc Public Display Functions--------------------------------------------------------------------
    //---------Misc Public Display Functions-----------------------------------------Misc Public Display Functions---------------------------
    String getTime(){return answers.getTime();}//<-- i passed this through here to keep surface area small for brain, but compiler might know this is dum.
    int getBombsFound(){return BombsFound;}//<-- get
    int getLivesLeft(){return livesLeft;}//<-- that
    int[] getGameOverIndex(){//     <--    text
        int[] messageIndexes = new int[2];
        messageIndexes[0]=GameOverMessageIndex;
        messageIndexes[1]=wonValue;
        return messageIndexes;
    }
    void toggleQuestionMarks(){//<-- toggles the ability to mark cells with a ? on and off (i find it annoying so it starts turned off)
        this.cancelQuestionMarks = !cancelQuestionMarks;
        if(cancelQuestionMarks == true){
            for (int x = 0; x < Fieldx; x++) {
                for (int y = 0; y < Fieldy; y++) {
                    if(answers.isQuestionable(x, y)){
                        answers.clearSuspicion(x,y);
                        getButtonAt(x,y).setForeground((DarkMode)?DarkModeTextColor:LightModeTextColor);
                        getButtonAt(x,y).setText("");
                    }
                }
            }
        }
    }
    boolean isDarkMode(){return DarkMode;}//<-- this is a function

    void toggleDarkMode(){//<-- and so is this
        this.DarkMode = !DarkMode;
        for (int x = 0; x < Fieldx; x++) {
            for (int y = 0; y < Fieldy; y++) {
                if(!answers.exploded(x, y)&&(!answers.checked(x, y)||answers.adjCount(x, y)>0)){//these are the 2 conditions in which i set background
                    //                                                                            ^so i check to prevent overwriting it
                    if(DarkMode){getButtonAt(x,y).setBackground(BLACK);
                    }else{
                        getButtonAt(x,y).setBackground(null);
                        getButtonAt(x,y).setIcon(DefaultButtonIcon);
                    }
                    if(((DarkMode)?(getButtonAt(x,y).getForeground() == LightModeTextColor):(getButtonAt(x,y).getForeground() == DarkModeTextColor))){
                        getButtonAt(x,y).setForeground((DarkMode)?DarkModeTextColor:LightModeTextColor);
                    }//^this if is to make sure it doesnt change the color of the game over ! marker when it happens on a chord because it will replace a number. 
                }// it doesnt get caught by enclosing condition so this if basically says, only change text color if default color
            }
        }
        Grid.this.repaint();
    }
    void ResetBoard(){//<-- for when you want new but not like, new new. Just like, sorta new. Refreshed.
        BombsFound = 0;
        livesLeft = lives;
        GameOverMessageIndex = 3;
        wonValue = 3;
        answers = new Minefield(Fieldx, Fieldy, bombCount);//<-- get a new minefield, thus resetting the timer and everything else
        for (int i = 0; i < Fieldx; i++) {
            for (int j = 0; j < Fieldy; j++) {// and then reset appearances.
                getButtonAt(i,j).setForeground((DarkMode)?DarkModeTextColor:LightModeTextColor);
                getButtonAt(i,j).setBorderColor(defaultBorderColor, 1);
                getButtonAt(i,j).setText("");
                if(DarkMode){
                    getButtonAt(i,j).setBackground(BLACK);
                    getButtonAt(i,j).setIcon(null);
                }else {
                    getButtonAt(i,j).setBackground(null);
                    getButtonAt(i,j).setIcon(DefaultButtonIcon);
                }
            }
        }
        Grid.this.repaint();//repaint so that it actually repaints at a time that makes sense rather than just like... later.
    }
    int[] doZoom(int rotation, int mouseX1, int mouseY1){//it makes the cells bigger. The main window is in a scroll pane. does not set font
        Dimension currentCellSize = Grid.this.getComponent(0).getSize();
        Dimension newCellSize = new Dimension(currentCellSize.width + rotation, currentCellSize.height + rotation);
        for (int i = 0; i < Fieldx; i++) {//apply the new sizes
            for (int j = 0; j < Fieldy; j++) {
                getButtonAt(i,j).setPreferredSize(newCellSize);
            }
        }//Returns old and new grid sizes. I need it to recenter window on mouse. MUCH faster than using pack(); to update size before recenter
        int[] gridSizesOldNew= new int[4];
        gridSizesOldNew[0] = currentCellSize.width*Fieldx;
        gridSizesOldNew[1] = currentCellSize.height*Fieldy;
        gridSizesOldNew[2] = (newCellSize.width*Fieldx);
        gridSizesOldNew[3] = (newCellSize.height*Fieldy);
        return gridSizesOldNew;
    }
    void setCellFontSize(){//call this after pack to get correct component size.
        int cellHeight = Grid.this.getComponent(0).getHeight();
        int FontSize = cellHeight- 1;
        if(cellHeight>18)FontSize=18;
        Font newFont = new Font("Tahoma", 0, FontSize);
        for(int x = 0; x < Fieldx; x++)for(int y = 0; y < Fieldy; y++)getButtonAt(x,y).setFont(newFont);
    }
//----GAME-LOGIC-BELOW--------GAME-LOGIC-BELOW--------GAME-LOGIC-BELOW--------GAME-LOGIC-BELOW--------GAME-LOGIC-BELOW-------GAME-LOGIC-BELOW----------
//--------Click Handler--------------Click Handler--------------Click Handler--------------Click Handler--------------Click Handler------
    public void doClickType(JButton clickedButton, int clickType) {
        CellButton clickedCell = (CellButton)clickedButton;
        if(clickType==0){
            regularClick(clickedCell);
        }else if(clickType==1){
            markCell(clickedCell);
        }else if(clickType==2){
            playChord(clickedCell);
        }
    }
//-----------END OF PUBLIC FUNCTIONS------------------------END OF PUBLIC FUNCTIONS------------------------END OF PUBLIC FUNCTIONS-----------------------
//-----------MAIN CELL CLICKED FUNCTIONS-----------------MAIN CELL CLICKED FUNCTIONS----------MAIN CELL CLICKED FUNCTIONS--------------------------------
    private void regularClick(CellButton current){//---------regularClick()--------regularClick()--------regularClick()--------regularClick()----
        int xValue = current.getXcoord();
        int yValue = current.getYcoord();
        if(!answers.isGameOver()){
            if (answers.isFirstClick()) {
                answers.reset(xValue,yValue);//if possible, answers.adjCount(clickedX,clickedY) == 0 on the first click. also initializes field data.
                answers.doFirstClick();
            }
            if (!answers.exploded(xValue,yValue) && !answers.checked(xValue,yValue) && !answers.marked(xValue,yValue) && !answers.isQuestionable(xValue,yValue)) {
                if (answers.isBomb(xValue, yValue)) {//was it a bomb?                           ^was it a clickable cell?
                    answers.explode(xValue,yValue);//it was a bomb...
                    if(answers.cellsExploded()>=lives){//RIP
                        current.setText("!");
                        current.setBackground(RED);
                        current.setForeground(CYAN);
                        answers.setGameOver();
                        GameOver(false);//<-- starts game over process
                    }else if(answers.cellsExploded()<lives){//You lost one of your privates.
                        current.setText(Integer.toString(answers.cellsExploded()));
                        current.setBackground(RED);
                        current.setForeground(CYAN);
                        BombsFound = answers.cellsMarked()+answers.cellsExploded();
                    }
                    livesLeft = lives-answers.cellsExploded();
                } else if (answers.adjCount(xValue, yValue) != 0) {//*whew*.... close one.
                    current.setText(String.valueOf(answers.adjCount(xValue, yValue)));
                    current.setForeground((DarkMode)?DarkModeTextColor:LightModeTextColor);
                    answers.check(xValue, yValue);
                    setBorderBasedOnAdj(xValue, yValue);
                } else {                           //you clicked a 0
                    boolean[][] prechekd=new boolean[Fieldx][Fieldy];//saving current checks so we know which ones fillzeroes flipped
                    for(int j=0;j<Fieldx;j++){
                        for(int k=0;k<Fieldy;k++){
                            prechekd[j][k]=(answers.checked(j, k));
                        }
                    }
                    fillZeroes(xValue, yValue);//<-- adjCount=0. Fill.
                    markLonelyBombs(prechekd);//<-- mark the bombs with all neighbors 1 that were completely revealed by fillzeroes
                    BombsFound = answers.cellsMarked()+answers.cellsExploded();
                }
            }
            if ((answers.cellsChecked() == Fieldx * Fieldy - bombCount)&&(answers.cellsChecked()>0)&&!answers.isGameOver()) {//Are you done yet?
                answers.setGameOver();
                GameOver(true);//<-- starts game over process
            }
        }
    }
    //---------------------------------------------------------------markCell()--------------------------markCell()-------------
    private void markCell(CellButton current){//----------------------markCell()--------------------------markCell()-------------
        int xValue = current.getXcoord();
        int yValue = current.getYcoord();
        if (!answers.isGameOver() && !answers.isFirstClick()) {//marking
            if (!answers.exploded(xValue,yValue) && !answers.checked(xValue,yValue) && !answers.isFirstClick()){
                if(!answers.marked(xValue,yValue) && !answers.isQuestionable(xValue, yValue)) {
                    answers.mark(xValue,yValue);
                    current.setForeground(MARKCOLOR);
                    current.setText("X");
                } else if (answers.marked(xValue,yValue) && !cancelQuestionMarks) {
                    answers.unmark(xValue,yValue);
                    answers.question(xValue,yValue);
                    current.setForeground(QSTNMARKCOLOR);
                    current.setText("?");
                }else if (answers.isQuestionable(xValue,yValue) || ((answers.marked(xValue, yValue) && cancelQuestionMarks))){
                    answers.unmark(xValue,yValue);//<-- it already checks if it was unmarked so just stick it here as well to make it work regardless of ? settings
                    answers.clearSuspicion(xValue,yValue);
                    current.setForeground((DarkMode)?DarkModeTextColor:LightModeTextColor);
                    current.setText("");
                }
                BombsFound = answers.cellsMarked()+answers.cellsExploded();
            }
        }
    }//--------------------------playChord()----------playChord()--------------playChord()--------------playChord()--------------playChord()-------
    private void playChord(CellButton current){//Chord reveals all cells around a number, but you need to mark correctly or it hurts.
        int a = current.getXcoord();//     ^ did you know about this? I didnt either... I checked the minesweeper rules again...
        int b = current.getYcoord();
        int adjMarked = 0;
        if(!answers.isFirstClick() && !answers.isGameOver()){
            if(answers.checked(a, b) && answers.adjCount(a, b)!=0){
                for(int i=a-1;i<=a+1;i++){
                    for(int j=b-1;j<=b+1;j++){
                        if(i<0||j<0||i>=Fieldx||j>=Fieldy||(i==a && j==b)) continue;
                        if(answers.marked(i, j))adjMarked++;
                        if(!answers.exploded(i, j)&&!answers.marked(i, j)){
                            if(answers.isBomb(i, j)){
                                answers.explode(i, j);
                                getButtonAt(i,j).setBackground(RED);
                                getButtonAt(i,j).setForeground(CYAN);
                                getButtonAt(i,j).setText(Integer.toString(answers.cellsExploded()));
                            }else if(!answers.isQuestionable(i, j)){
                                if (answers.adjCount(i, j) != 0) {//adjCount>0
                                    getButtonAt(i,j).setText(String.valueOf(answers.adjCount(i, j)));
                                    getButtonAt(i,j).setForeground((DarkMode)?DarkModeTextColor:LightModeTextColor);
                                    answers.check(i, j);
                                    setBorderBasedOnAdj(i, j);
                                } else {                           //you hit a 0
                                    boolean[][] prechekd=new boolean[Fieldx][Fieldy];//saving current checks so we know which ones fillzeroes flipped
                                    for(int x=0;x<Fieldx;x++){
                                        for(int y=0;y<Fieldy;y++){
                                            prechekd[x][y]=(answers.checked(x, y));
                                        }
                                    }
                                    fillZeroes(i, j);//<-- adjCount=0. Fill.
                                    markLonelyBombs(prechekd);//<-- mark the bombs with all neighbors 1 that were completely revealed by fillzeroes
                                    BombsFound = answers.cellsMarked()+answers.cellsExploded();
                                }
                            }
                        }
                    }
                }
            }
            if(adjMarked>answers.adjCount(a, b)){//if you marked too many
                int penalty = adjMarked-answers.adjCount(a, b);
                for(int i=a-1;i<=a+1;i++){
                    for(int j=b-1;j<=b+1;j++){
                        if(i<0||j<0||i>=Fieldx||j>=Fieldy||(i==a && j==b)) continue;
                        if(penalty>0&&answers.marked(i, j)&&answers.isBomb(i, j)&&!answers.exploded(i, j)){
                                answers.unmark(i, j);
                                answers.explode(i, j);
                                getButtonAt(i,j).setBackground(RED);
                                getButtonAt(i,j).setForeground(CYAN);
                                getButtonAt(i,j).setText(Integer.toString(answers.cellsExploded()));
                                penalty--;
                        }
                    }
                }
            }
            if(answers.cellsExploded()>=lives){//RIP
                current.setText("!");
                current.setForeground(ChGO_RED);
                answers.setGameOver();
                GameOver(false);//<-- starts game over process
            }else if(answers.cellsExploded()<lives){//You lost some of your privates.
                BombsFound = answers.cellsMarked()+answers.cellsExploded();
            }
            livesLeft = lives-answers.cellsExploded();
            if ((answers.cellsChecked() == Fieldx * Fieldy - bombCount) && answers.cellsChecked()>0&&!answers.isGameOver()) {//Are you done yet?
                answers.setGameOver();
                GameOver(true);//<-- starts game over process
            }
        }
    }//--------------end of main cell clicked functions----Helper functions they referenced below---------------------------------------------------------
    //------------------------------------------fillZeroes()-------------------------------------------------
    private void fillZeroes(int xValue, int yValue) {//-----------fillZeroes()------------------------------------------------------------------
        Stack<Integer> stack = new Stack<>();//a stack must be used instead of recursion because large board sizes cause stack overflow.
        stack.push(yValue * Fieldx + xValue);//make single value out of x and y (in this order because thats the same as getButtonAt)
        while (!stack.isEmpty()) {
            int position = stack.pop();
            int y = position / Fieldx;//<-- integer division does floor. y*Fieldx/Fieldx
            int x = position % Fieldx;//<-- remainder=x
            if (!answers.checked(x, y)) {//since this will unfortunately branch and call multiple times per 0 cell, 
                answers.check(x, y);//     ^make sure we dont re-call our loop on already checked squares
                ((CellButton) Grid.this.getComponent(position)).setText(String.valueOf(answers.adjCount(x, y)));//direct from position because otherwise
                ((CellButton) Grid.this.getComponent(position)).setBackground(GRASS);                           //you do the multiplication twice unnecessarily
                ((CellButton) Grid.this.getComponent(position)).setBorderColor(null, 1);
                ((CellButton) Grid.this.getComponent(position)).setForeground(GRASS);
                for (int i = x - 1; i <= x + 1; i++) {//check neighbors
                    for (int j = y - 1; j <= y + 1; j++) {
                        if (i < 0 || j < 0 || i >= Fieldx || j >= Fieldy) {//exclude invalid cells
                            continue;
                        } else if (!answers.checked(i, j)) {
                            if (answers.adjCount(i, j) != 0) {//not 0, dont fill from it
                                answers.check(i, j);
                                setBorderBasedOnAdj(i, j);
                                getButtonAt(i,j).setText(String.valueOf(answers.adjCount(i, j)));
                                getButtonAt(i,j).setForeground((DarkMode)?DarkModeTextColor:LightModeTextColor);
                            } else {
                                stack.push(j * Fieldx + i);//is 0. queue it up!
                            }
                        }
                    }
                }
            }
        }
        Grid.this.repaint();//repaint so that it actually repaints at a time that makes sense rather than just like... later.
    }
    //-----------------------------------------------------markLonelyBombs()----------------------------------------------------------------
    private void markLonelyBombs(boolean[][] prechekd){//I didnt want to click on all the lone bombs on big boards in order to
        for (int j = 0; j < Fieldx; j++) {             //make the count of marked bombs actually useful
            for (int k = 0; k < Fieldy; k++) {
                if(answers.isBomb(j, k)){//<-- check for "is bomb"
                    boolean dqed = true;
                    int totalneighbors = 0;
                    int meetsConditions = 0;
                    for(int l=j-1;l<=j+1;l++){//check neighbors
                        for(int m=k-1;m<=k+1;m++){
                            if(!(l<0||m<0||l>=Fieldx||m>=Fieldy||(l==j && m==k))){//<-- it was valid cell
                                totalneighbors++;      //<-- get number of neighbors
                                if(answers.adjCount(l, m)==1 && answers.checked(l, m) && !prechekd[l][m])meetsConditions++; 
                            }//                                ^^^ condition would mean fillzeroes just revealed it so add 1
                        }
                    }
                    if(totalneighbors==meetsConditions)dqed=false;//fillzeroes revealed all neighbors and all were 1.
                    if(!dqed){//<-- if it was a lonely bomb
                        answers.mark(j,k); //If so, mark the lonely bomb.
                        getButtonAt(j,k).setForeground(MARKCOLOR);
                        getButtonAt(j,k).setText("X");
                    }
                }
            }
        };
    }//---------------------------------------setBorderBasedOnAdj()------------------------------------------------------------------------------
    private void setBorderBasedOnAdj(int x, int y){//it does setBorderBasedOnAdj
        if( answers.adjCount(x, y)<=1 ){ 
            getButtonAt(x,y).setBorderColor(defaultBorderColor, 1); 
        }else if(answers.adjCount(x, y)<=2 ){ 
            getButtonAt(x,y).setBorderColor(BORDERYELLOW, 2); 
        }else if(answers.adjCount(x, y)<=3 ){ 
            getButtonAt(x,y).setBorderColor(BORDERORANGE, 2); 
        }else if(answers.adjCount(x, y)<=5 ){ 
            getButtonAt(x,y).setBorderColor(BORDERORANGERED, 2); 
        }else if(answers.adjCount(x, y)<=8 ){ 
            getButtonAt(x,y).setBorderColor(BORDERRED, 2); 
        }
    }
    //---------------------------------------GameOver()-----------------------------------------------------------------------------------------
    private void GameOver(boolean won) {//reveals bombs on board then passes the work to UpdateLeaderboard
                //I was relying on the poor scaling to have my explode look good. uncomment if you have a better icon
        ScalableIcon EXPiconAutoScaled = new ScalableIcon(new ImageIcon(EXPicon)/*get rid of this ); too-->*/);//.getScaledInstance(getButtonAt(0,0).getWidth(), getButtonAt(0,0).getHeight(), Image.SCALE_SMOOTH)));
        ScalableIcon RVLiconAutoScaled = new ScalableIcon(new ImageIcon(RVLicon.getScaledInstance(getButtonAt(0,0).getWidth(), getButtonAt(0,0).getHeight(), Image.SCALE_SMOOTH)));
        for (int i = 0; i < Fieldx; i++) {
            for (int j = 0; j < Fieldy; j++) {
                if (answers.isBomb(i, j) && !answers.exploded(i, j)) {
                    if (won == false){
                        if(answers.marked(i,j)){
                            getButtonAt(i,j).setForeground(BLUE);
                            getButtonAt(i,j).setText("@");
                        }else{getButtonAt(i,j).setText("");}
                        getButtonAt(i,j).setIcon(EXPiconAutoScaled);
                        getButtonAt(i,j).revalidate();
                    }else{
                        if(!answers.marked(i,j)){
                            getButtonAt(i,j).setForeground(MAGENTA);
                            getButtonAt(i,j).setText("@");
                        }else{getButtonAt(i,j).setText("");}
                        getButtonAt(i,j).setIcon(RVLiconAutoScaled);
                        getButtonAt(i,j).revalidate();
                    }
                    getButtonAt(i,j).setIconTextGap(-((getButtonAt(0,0).getWidth()*2)/3));
                    answers.check(i,j);//check the exploded bomb so you cant mess it up by toggling dark mode
                }
            }
        }
        int MessageIndex = 0; //update leaderboard then update win or loss message based on highscore status
        if(Fieldx*Fieldy>bombCount&&bombCount>0)MessageIndex = updateLeaderboard(won);//<--
        GameOverMessageIndex = MessageIndex;
        wonValue=(won)?1:0;
    }//---------------------------------------------updateLeaderboard()-------------------------------------------------------------------------
    private int updateLeaderboard(boolean won){//Reads and writes scores from score file, returns index for win/loss message
        String scoresFileName;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            scoresFileName = scoresFileNameWindows;
        } else {
            scoresFileName = scoresFileNameOther;
        }
        String RemainingLives= String.valueOf(Math.max(0, lives-answers.cellsExploded()));
        String timeString = answers.getTime();
        boolean highscore = false;
        boolean newBoardSize = false;
        String answersString = String.valueOf(Fieldx) + ":" + String.valueOf(Fieldy) + ":" + String.valueOf(bombCount) + ":" + String.valueOf(lives);
        String thisScore = (answersString +"-"+ RemainingLives +"-"+ timeString);  //format:"x:y:bombCount:lives-RemainingLives-time"
        boolean fileFound = true;
        String[] word = null;
        try (Scanner in = new Scanner(new File(scoresFileName))) { //try to read
            StringBuilder tFile = new StringBuilder();
            while (in.hasNext()) {
                tFile.append(in.next()).append(" ");
            }
            word = tFile.toString().split("\\s+");
        } catch (IOException e) { // whoops. Could not.
            fileFound = false;
            try {
                Files.createDirectories(Path.of(scoresFileName).getParent()); //<-- Create the directory
                Files.writeString(Path.of(scoresFileName), thisScore + " ", StandardOpenOption.CREATE); //<-- Write the file
            } catch (IOException ex) {ex.printStackTrace();}
        }
        if(fileFound){
            if(1<=word.length && word[0].isEmpty()){
                try (FileWriter out1 = new FileWriter(scoresFileName)) { //file found but empty. Writing.
                    fileFound=false;
                    out1.write(thisScore + " ");
                } catch (IOException ex) {}
             }else{
                int c = 0;
                String[] currentSavedScore;
                while(c<word.length){
                    currentSavedScore=word[c].split("-");
                    if(currentSavedScore[0].equals(answersString)){
                        if(won && (Integer.parseInt(currentSavedScore[2])>Integer.parseInt(timeString))){
                            word[c]=thisScore;//                         ^did you beat the time?
                            highscore=true;
                        }else if(won && (Integer.parseInt(currentSavedScore[1])>Integer.parseInt(RemainingLives)) && (Integer.parseInt(currentSavedScore[2])==Integer.parseInt(timeString))){
                            word[c]=thisScore;//                         ^is it same time but more lives?
                            highscore=true;
                        }else if(won && (Integer.parseInt(currentSavedScore[1])<1)){//was the entry created by dying on a new board configuration?
                            word[c]=thisScore;
                            highscore=true;
                        }
                        break;
                    }
                    c++;
                }
                if(c==word.length){//none were a match. New Board Size
                    newBoardSize=true;
                    String[] xUy = new String[word.length+1];
                    int i=0;
                    while(i<word.length){
                        xUy[i]=word[i];
                        i++;
                    }
                    xUy[i]=thisScore;
                    word=xUy;
                }
            }
            if(highscore || newBoardSize){//save file
                try (FileWriter out2 = new FileWriter(scoresFileName)) {
                    for(int i=0; i<word.length; i++){
                        out2.write(word[i]+" ");
                    }
                }catch(IOException e){}
            }
        }
        return (highscore)?2:((newBoardSize || !fileFound)?1:0);
    }
}