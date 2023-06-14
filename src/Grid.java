import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Icon;
import java.awt.Font;
import java.awt.GridLayout;
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
//This class controls the behavior of the game board. Contains cell display and action logic, and uses Minefield to keep track of the game state
public class Grid extends JPanel {
    //--------------Initialize-----------------------------
    private final int Fieldx, Fieldy, bombCount, lives;
    private final Color BLACK = new Color(0);
    private final Color GREEN = new Color(31, 133, 28);
    private final Color MAGENTA = new Color(255,0,255);
    private final Color RED = new Color(255,0,0);
    private final Color ORANGE = new Color(255, 160, 0);
    private final Color ORANGERED = new Color(255,95,0);
    private final Color YELLOW = new Color(255, 255, 0);
    private final Color BLUE = new Color(0,0,255);
    private final Color CYAN = new Color(0,255,200);
    private final Color PURPLE = new Color(109, 50, 153);
    private final Color defaultBorderColor = new Color(126, 126, 126);
    private final Insets CellInset = new Insets(-20, -20, -20, -20);
    private final Icon defaultButtonIcon = (new JButton()).getIcon();//<-- it should just do this at compile time right? I'd hope?
    private final String scoresFileNameWindows = System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Roaming" + File.separator + "minesweeperScores" + File.separator + "Leaderboard.txt";
    private final String scoresFileNameOther = System.getProperty("user.home") + File.separator + ".minesweeper" + File.separator + "Leaderboard.txt";
    private final String highScoreMessage = "Record Time!";
    private final String newBoardSizeAndWonMessage = "New Board Cleared!";
    private final String wonAndNotHighScoreMessage = "Cleared!";
    private final String diedButNewBoardMessage = "1st Board Death";
    private final String diedAndNotNewBoardMessage = "Exploded...";
    private boolean cancelQuestionMarks = true;//<-- boolean for toggling ? marks on bombs
    private String GameOverMessage = "";
    private int BombsFound = 0;
    private int livesLeft = 0;
    private Minefield answers;//<-- this one is the data class for game logic
    private CellButton[][] Cell;//<-- JButton with border painting. Defined below.
    private class CellButton extends JButton {//-----------------------------------CellButton();----------------------------------------------
        private int borderWeight = 1;         //^created to allow the setting of color and thickness of cell borders, now it knows where it is.
        private Color borderColor = defaultBorderColor;//<--not defined inside class because that would mean creating a new color for each button, which is expensive.
        private int x, y;
        public CellButton(int x, int y) {//<-- -Constructor
            this.x=x;
            this.y=y;
            this.setMargin(CellInset);        //Initialize our cell button properties other than font size (which is done after packing layout)
            this.setFocusable(false);//<-- made unfocusable because i didnt like hitting tab for 3 years
            this.setHorizontalAlignment(SwingConstants.CENTER);//<-- they used to be in their own loop but this is the same thing
            this.setVerticalAlignment(SwingConstants.CENTER);
        }
        public void setBorderColor(Color borderColor, int borderWeight) {
            this.borderColor = borderColor;
            this.borderWeight = borderWeight;
        }
        public int getXcoord(){return x;}
        public int getYcoord(){return y;}
        @Override
        protected void paintBorder(Graphics g) {//<-- override paintBorder so that I can change border color and thickness
            if(borderColor!=null){
                g.setColor(borderColor);
                int scale = 1+(this.getSize().height/23);
                if(borderColor == defaultBorderColor){scale = 1;}
                int top = borderWeight*scale;
                for(int i=0;i<top;i++)g.drawRect(i, i, getWidth() - i*2 - 1, getHeight() - i*2 -1);
            }
        }
    }//---------------------GRID CONSTRUCTOR----------------------GRID CONSTRUCTOR----------------------------GRID CONSTRUCTOR------------------------------
    public Grid(int w, int h, int bombNum, int lives) {
        Fieldx = w;
        Fieldy = h;
        this.lives = lives;
        bombCount = bombNum;
        BombsFound = 0;
        livesLeft = lives;
        answers = new Minefield(Fieldx, Fieldy, bombCount);
        Cell = new CellButton[Fieldx][Fieldy];
        Grid.this.setLayout(new GridLayout(Fieldy, Fieldx));
        Grid.this.setOpaque(false);
        for (int i = 0; i < Fieldx; i++) {//this is the heaviest part of the entire program. Pretty sure if i wanted it to be 
            for (int j = 0; j < Fieldy; j++) {//faster i shoulda written it in GO or Rust haha! luckily, nobody wants to click on
                Cell[i][j] = new CellButton(i, j);//<-- 90000 cells anyway. It still loads up if you feel inspired. Give it a minute.
            }
        }
        for (int j = Fieldy - 1; j >= 0; j--) {//add cells to panel in correct order.
            for (int i = 0; i < Fieldx; i++) {
                Grid.this.add(Cell[i][j]);
            }
        }
    }//-----------------------------------function for adding mouse Listener to cells in Grid-------------------------------------------------
    void addCellListener(MouseListener mouseListener){//----------addCellListener()--------------------------------------------------
        for (int i = 0; i < Fieldx; i++) {
            for (int j = 0; j < Fieldy; j++) {
                Cell[i][j].addMouseListener(mouseListener);
            }
        }
    }//---Look what you have to do to make the background green without making the components also green??------------------------------------
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(GREEN);
        g.fillRect(0, 0, getWidth(), getHeight());
    }//---------Misc Public Display Functions-----------------------------------------Misc Public Display Functions---------------------------
    String getTime(){return answers.getTime();}
    String getBombsFound(){return Integer.toString(BombsFound);}
    String getLivesLeft(){return Integer.toString(livesLeft);}
    String getGameOverMessage(){return GameOverMessage;}
    void toggleQuestionMarks(){//<-- toggles the ability to mark cells with a ? on and off (i find it annoying so it starts turned off)
        this.cancelQuestionMarks = !cancelQuestionMarks;
        if(cancelQuestionMarks == true){
            for (int x = 0; x < Fieldx; x++) {//change ? to unmarked cells
                for (int y = 0; y < Fieldy; y++) {
                    if(answers.isQuestionable(x, y)){
                        answers.clearSuspicion(x,y);
                        Cell[x][y].setForeground(BLACK);
                        Cell[x][y].setText("");
                    }
                }
            }
        }
    }
    void ResetBoard(){
        answers = new Minefield(Fieldx, Fieldy, bombCount);//<-- get a new minefield, thus resetting the timer and everything else
        for(int i=0;i<Fieldx;i++){//resetting Cell Properties is way faster than creating new ones
            for(int j=0;j<Fieldy;j++){
                Cell[i][j].setForeground(BLACK);
                Cell[i][j].setBorderColor(defaultBorderColor, 1);
                Cell[i][j].setBackground(null);
                Cell[i][j].setIcon(defaultButtonIcon);
                Cell[i][j].setText("");
            }
        }
        Grid.this.repaint();
        BombsFound = 0;
        livesLeft = lives;
        GameOverMessage = "";
    }
    void doZoom(int rotation){//it makes the cells bigger. The main window is in a scroll pane
        Dimension currentCellSize = Cell[0][0].getSize();
        Dimension newCellSize = new Dimension(currentCellSize.width - rotation, currentCellSize.height - rotation);
        int newFontSize = newCellSize.height-1;
        if(newCellSize.height>18)newFontSize=18;
        Font newFont = new Font("Tahoma", 0, newFontSize);
        for (int i = 0; i < Fieldx; i++) {//apply the new sizes
            for (int j = 0; j < Fieldy; j++) {
                Cell[i][j].setFont(newFont);
                Cell[i][j].setPreferredSize(newCellSize);
            }
        }
    }
    void setCellFontSize(){//call this after pack to get correct component size.
        int cellHeight = Cell[0][0].getHeight();
        int FontSize = cellHeight- 1;
        if(cellHeight>18)FontSize=18;
        Font newFont = new Font("Tahoma", 0, FontSize);
        for (int i = 0; i < Fieldx; i++) { 
            for (int j = 0; j < Fieldy; j++) {
                Cell[i][j].setFont(newFont);
            }
        }
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
        int xValue = (int) current.getXcoord();
        int yValue = (int) current.getYcoord();
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
            if ((answers.cellsChecked() == Fieldx * Fieldy - bombCount)) {//Are you done yet?
                answers.setGameOver();
                GameOver(true);//<-- starts game over process
            }
        }
    }//---------------------------------------------------------------markCell()--------------------------markCell()-------------
    private void markCell(CellButton current){//----------------------markCell()--------------------------markCell()-------------
        int xValue = (int) current.getXcoord();
        int yValue = (int) current.getYcoord();
        if (!answers.isGameOver() && !answers.isFirstClick()) {//marking
            if (!answers.exploded(xValue,yValue) && !answers.checked(xValue,yValue) && !answers.isFirstClick()){
                if(!answers.marked(xValue,yValue) && !answers.isQuestionable(xValue, yValue)) {
                    answers.mark(xValue,yValue);
                    current.setForeground(RED);
                    current.setText("X");
                } else if (answers.marked(xValue,yValue) && !cancelQuestionMarks) {
                    answers.unmark(xValue,yValue);
                    answers.question(xValue,yValue);
                    current.setForeground(PURPLE);
                    current.setText("?");
                }else if (answers.isQuestionable(xValue,yValue) || ((answers.marked(xValue, yValue) && cancelQuestionMarks))){
                    answers.unmark(xValue,yValue);//<-- it already checks if it was unmarked so just stick it here as well to make it work regardless of ? settings
                    answers.clearSuspicion(xValue,yValue);
                    current.setForeground(BLACK);
                    current.setText("");
                }
                BombsFound = answers.cellsMarked()+answers.cellsExploded();
            }
        }
    }//--------------------------playChord()----------playChord()--------------playChord()--------------playChord()--------------playChord()-------
    private void playChord(CellButton current){//Chord reveals all cells around a bomb, but you need to mark correctly or it hurts.
        int a = (int) current.getXcoord();//     ^ did you know about this? I didnt either... I checked the minesweeper rules again...
        int b = (int) current.getYcoord();
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
                                Cell[i][j].setBackground(RED);
                                Cell[i][j].setForeground(CYAN);
                                Cell[i][j].setText(Integer.toString(answers.cellsExploded()));
                            }else if(!answers.isQuestionable(i, j)){
                                if (answers.adjCount(i, j) != 0) {//adjCount>0
                                    Cell[i][j].setText(String.valueOf(answers.adjCount(i, j)));
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
                                Cell[i][j].setBackground(RED);
                                Cell[i][j].setForeground(CYAN);
                                Cell[i][j].setText(Integer.toString(answers.cellsExploded()));
                                penalty--;
                        }
                    }
                }
            }
            if(answers.cellsExploded()>=lives){//RIP
                current.setText("!");
                current.setForeground(RED);
                answers.setGameOver();
                GameOver(false);//<-- starts game over process
            }else if(answers.cellsExploded()<lives){//You lost some of your privates.
                BombsFound = answers.cellsMarked()+answers.cellsExploded();
            }
            livesLeft = lives-answers.cellsExploded();
            if ((answers.cellsChecked() == Fieldx * Fieldy - bombCount)) {//Are you done yet?
                answers.setGameOver();
                GameOver(true);//<-- starts game over process
            }
        }
    }//--------------end of main cell clicked functions----Helper functions they referenced below---------------------------------------------------------
    private void fillZeroes(int xValue, int yValue) {//-----------fillZeroes()------------------------------------------------------------------
        Stack<Integer> stack = new Stack<>();//a stack must be used instead of recursion because large board sizes cause stack overflow.
        stack.push(xValue * Fieldy + yValue);//make single value out of x and y
        while (!stack.isEmpty()) {
            int position = stack.pop();
            int x = position / Fieldy;//<-- integer division does floor. x*Fieldy/Fieldy
            int y = position % Fieldy;//<-- remainder=y
            if (!answers.checked(x, y)) {//since this will unfortunately branch and call multiple times per 0 cell, 
                answers.check(x, y);//     ^make sure we dont use check too many times
                Cell[x][y].setText(String.valueOf(answers.adjCount(x, y)));
                Cell[x][y].setBackground(GREEN);
                Cell[x][y].setBorderColor(null, 1);
                Cell[x][y].setForeground(GREEN);
                for (int i = x - 1; i <= x + 1; i++) {//check neighbors
                    for (int j = y - 1; j <= y + 1; j++) {
                        if (i < 0 || j < 0 || i >= Fieldx || j >= Fieldy) {//exclude invalid cells
                            continue;
                        } else if (!answers.checked(i, j)) {
                            if (answers.adjCount(i, j) != 0) {//not 0, dont fill from it
                                answers.check(i, j);
                                setBorderBasedOnAdj(i, j);
                                Cell[i][j].setText(String.valueOf(answers.adjCount(i, j)));
                                Cell[i][j].setForeground(BLACK);
                            } else {
                                stack.push(i * Fieldy + j);//is 0. queue it up!
                            }
                        }
                    }
                }
            }
        }
        Grid.this.repaint();
    }//-----------------------------------------------------markLonelyBombs()----------------------------------------------------------------
    private void markLonelyBombs(boolean[][] prechekd){//I didnt want to click on all the lone bombs on big boards in order to
        for(int j=0;j<Fieldx;j++){                     //^make the count of marked bombs actually useful
            for(int k=0;k<Fieldy;k++){                 //^so this exists to fill ones revealed by FillZeroes
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
                        Cell[j][k].setForeground(RED);
                        Cell[j][k].setText("X");
                    }
                }
            }
        }
    }//---------------------------------------setBorderBasedOnAdj()------------------------------------------------------------------------------
    private void setBorderBasedOnAdj(int x, int y){//it does setBorderBasedOnAdj
        if( answers.adjCount(x, y)<=1 ){ 
            Cell[x][y].setBorderColor(defaultBorderColor, 1); 
        }else if(answers.adjCount(x, y)<=2 ){ 
            Cell[x][y].setBorderColor(YELLOW, 2); 
        }else if(answers.adjCount(x, y)<=3 ){ 
            Cell[x][y].setBorderColor(ORANGE, 2); 
        }else if(answers.adjCount(x, y)<=5 ){ 
            Cell[x][y].setBorderColor(ORANGERED, 2); 
        }else if(answers.adjCount(x, y)<=8 ){ 
            Cell[x][y].setBorderColor(RED, 2); 
        }
    }//---------------------------------------GameOver()-----------------------------------------------------------------------------------------
    private void GameOver(boolean won) {//reveals bombs on board then passes the work to UpdateLeaderboard
        for (int i = 0; i < Fieldx; i++) {
            for (int j = 0; j < Fieldy; j++) {
                if (answers.isBomb(i, j) && !answers.exploded(i, j)) {
                    if (won == false){
                        Cell[i][j].setForeground((answers.marked(i,j))?MAGENTA:ORANGERED);
                    }else Cell[i][j].setForeground((answers.marked(i,j))?BLUE:MAGENTA);
                    Cell[i][j].setText((won)?"@":"*");
                }
            }
        }
        int MessageIndex = 0; //update leaderboard then update win or loss message based on highscore status
        if(Fieldx*Fieldy!=bombCount/*unnecessary filtering*/)MessageIndex = updateLeaderboard(won);//<--
        if(won){ GameOverMessage = (MessageIndex==2)?highScoreMessage:((MessageIndex==1)?newBoardSizeAndWonMessage:wonAndNotHighScoreMessage);
        }else GameOverMessage = (MessageIndex==1)?diedButNewBoardMessage:diedAndNotNewBoardMessage;
    }//---------------------------------------------updateLeaderboard()-------------------------------------------------------------------------
    private int updateLeaderboard(boolean won){//Reads and writes scores from score file, returns index for win/loss message
        String os = System.getProperty("os.name").toLowerCase();
        String scoresFileName;
        if (os.contains("win")) {
            scoresFileName = scoresFileNameWindows;
        } else {
            scoresFileName = scoresFileNameOther;
        }
        String RemainingLives= String.valueOf(lives-answers.cellsExploded());
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