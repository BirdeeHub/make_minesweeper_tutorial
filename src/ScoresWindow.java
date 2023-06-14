import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.Insets;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.border.EmptyBorder;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.GridLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.KeyboardFocusManager;

public class ScoresWindow extends JFrame {
    private final String thisBoard;
    private JToggleButton clickableToggle = new JToggleButton("click?");
    private boolean clickable;
    private JFrame ParentFrame;
    private boolean FileIssue = false;//<-- used to stop it from becoming a button when you use the toggle button if file issue
    private final Dimension defaultwindowsize = new Dimension(280, 500);
    private JPanel BoardPanel;//these ones are globally initialized to create buttons with small load time
    private JLabel[] BoardLabel;
    private JButton[] BoardButton;
    //----------Constructor---------------------------------
    public ScoresWindow(int Fieldx, int  Fieldy, int bombCount, int lives, JFrame ParentFrame) {
        thisBoard = Fieldx+":"+Fieldy+":"+bombCount + ":" + lives;//this is how it knows what score to highlight
        clickableToggle.setSelected(!(ParentFrame instanceof MainGameWindow));//get default state of clickable
        this.clickable = clickableToggle.isSelected();
        this.ParentFrame=ParentFrame;//we need this to close it later if new board is chosen
        initComponents();
    }
    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); //Initialize our nested gridbaglayout panels
        getContentPane().setPreferredSize(new Dimension(defaultwindowsize));
        JPanel containerGridBag = new JPanel(new GridBagLayout());
        GridBagConstraints containerConstraints = new GridBagConstraints();
        JPanel HeadingPanel = new JPanel(new GridBagLayout());
        GridBagConstraints HeadingConstraints = new GridBagConstraints();
        JPanel ScoresPanel = new JPanel(new GridBagLayout());
        GridBagConstraints ScoresConstraints = new GridBagConstraints();
        JScrollPane scrollPane = new JScrollPane(containerGridBag);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        getContentPane().add(scrollPane);//add scroll pane to frame

        containerConstraints.fill = GridBagConstraints.BOTH;//add our other panels into the main panel which is inside a scroll pane
        containerConstraints.gridx = 0;
        containerConstraints.gridy = 0;
        containerConstraints.gridwidth = 1;
        containerConstraints.gridheight = 1;
        containerConstraints.weighty = 0.0;
        containerGridBag.add(HeadingPanel, containerConstraints);
        containerConstraints.gridy = 1;
        containerConstraints.gridwidth = GridBagConstraints.REMAINDER;
        containerConstraints.gridheight = GridBagConstraints.REMAINDER;
        containerConstraints.weightx = 1.0;
        containerConstraints.weighty = 1.0;
        containerGridBag.add(ScoresPanel, containerConstraints);

        JButton Back = new JButton("Back");              //initialize HeadingPanel items
        Back.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt) {
                ScoresWindow.this.dispose();
            }
        });
        clickableToggle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clickable = clickableToggle.isSelected();
                getContentPane().setPreferredSize(ScoresWindow.this.getContentPane().getSize());
                if(!FileIssue){
                    BoardPanel.removeAll();//remove
                    if(clickable){
                        for(int i=0;i<BoardButton.length;i++)BoardPanel.add(BoardButton[i]);
                    }else for(int i=0;i<BoardLabel.length;i++)BoardPanel.add(BoardLabel[i]);
                    ScoresWindow.this.pack();
                    ScoresWindow.this.setVisible(true);
                    ScoresWindow.this.getContentPane().revalidate();
                }
            }
        });
        ActionListener BoardButtonListener = new ActionListener(){
            public void actionPerformed(ActionEvent evt) {
                if(clickableToggle.isSelected()==true)BoardButtonPressedAction(((JButton) evt.getSource()), ParentFrame);
            }
        };
        KeyAdapter keyAdapter = new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                // Check if the Enter key is pressed
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    // get focused component source
                    Component CurrComp = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                    if(CurrComp == clickableToggle){//this one is a JToggleButton
                        clickableToggle.doClick();
                    }else ((JButton)CurrComp).doClick();
                }
            }
        };
        clickableToggle.addKeyListener(keyAdapter);
        Back.addKeyListener(keyAdapter);
        JLabel TitleLabel = new JLabel();
        TitleLabel.setFont(new Font("Tahoma", 0, 36));
        TitleLabel.setText("High Scores!");
        TitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        HeadingConstraints.gridx = 0;
        HeadingConstraints.gridy = 0;
        HeadingConstraints.gridwidth = 1;
        HeadingConstraints.gridheight = 2;
        HeadingConstraints.weighty = 1.0;
        HeadingConstraints.fill = GridBagConstraints.BOTH;                //layout HeadingPanel
        HeadingPanel.add(Back, HeadingConstraints);
        HeadingConstraints.weighty = 0.0;
        HeadingConstraints.gridy = 2;
        HeadingConstraints.gridheight = 1;
        HeadingPanel.add(clickableToggle, HeadingConstraints);
        HeadingConstraints.gridy = 0;
        HeadingConstraints.gridx = 1;
        HeadingConstraints.gridheight = 3;
        HeadingConstraints.gridwidth = GridBagConstraints.REMAINDER;
        HeadingConstraints.weightx = 1.0;
        HeadingPanel.add(TitleLabel, HeadingConstraints);

        JLabel ColumnHeadingLabel1 = new JLabel("<html><u>Board:</u></html>");         //initialize row 1 of ScoresPanel
        JLabel ColumnHeadingSpacer = new JLabel(" ");
        JLabel ColumnHeadingLabel2 = new JLabel("<html><u>Lives Left:</u></html>");    //scores panel lives heading
        JLabel ColumnHeadingSpacer2 = new JLabel(" ");
        JLabel ColumnHeadingLabel3 = new JLabel("<html><u>time:</u></html>");          //scores panel time heading
        ColumnHeadingLabel1.setBorder(new EmptyBorder(5, 10, 0, 0));
        ColumnHeadingSpacer.setBorder(new EmptyBorder(5, 10, 0, 10));
        ColumnHeadingLabel2.setBorder(new EmptyBorder(5, 0, 0, 0));
        ColumnHeadingSpacer2.setBorder(new EmptyBorder(5, 5, 0, 5));
        ColumnHeadingLabel3.setBorder(new EmptyBorder(5, 0, 0, 10));
        ColumnHeadingLabel1.setVerticalAlignment(SwingConstants.NORTH);
        ColumnHeadingSpacer.setVerticalAlignment(SwingConstants.NORTH);
        ColumnHeadingLabel2.setVerticalAlignment(SwingConstants.NORTH);
        ColumnHeadingSpacer2.setVerticalAlignment(SwingConstants.NORTH);
        ColumnHeadingLabel3.setVerticalAlignment(SwingConstants.NORTH);

        ScoresConstraints.fill = GridBagConstraints.BOTH;
        ScoresConstraints.gridx = 0;
        ScoresConstraints.gridy = 0;
        ScoresConstraints.gridwidth = 1;
        ScoresConstraints.gridheight = 1;
        ScoresPanel.add(ColumnHeadingLabel1, ScoresConstraints);
        ScoresConstraints.gridx = 1;
        ScoresPanel.add(ColumnHeadingSpacer, ScoresConstraints); //layout row 1 of ScoresPanel
        ScoresConstraints.gridx = 2;
        ScoresPanel.add(ColumnHeadingLabel2, ScoresConstraints);
        ScoresConstraints.gridx = 3;
        ScoresPanel.add(ColumnHeadingSpacer2, ScoresConstraints);
        ScoresConstraints.gridx = 4;
        ScoresPanel.add(ColumnHeadingLabel3, ScoresConstraints);

        BoardPanel = new JPanel();                           //set up main scores display properties
        BoardPanel.setLayout(new GridLayout(0, 1)); 
        JLabel BoardSpacer = new JLabel(" ");
        BoardSpacer.setBorder(new EmptyBorder(10, 10, 10, 10));
        JPanel LivesPanel = new JPanel();
        LivesPanel.setLayout(new GridLayout(0, 1));
        JLabel BoardSpacer2 = new JLabel(" ");
        BoardSpacer2.setBorder(new EmptyBorder(10, 5, 10, 5));
        JPanel TimePanel = new JPanel();
        TimePanel.setLayout(new GridLayout(0, 1));
        leaderboardText(BoardPanel, LivesPanel, TimePanel, BoardButtonListener, keyAdapter);//set text for main scores display
        JLabel BoardSpacer3 = new JLabel(" ");

        ScoresConstraints.fill = GridBagConstraints.BOTH;
        ScoresConstraints.gridx = 0;
        ScoresConstraints.gridy = 1;
        ScoresConstraints.gridwidth = 1;
        ScoresConstraints.gridheight = 1;
        ScoresPanel.add(BoardPanel, ScoresConstraints);
        ScoresConstraints.gridx = 1;
        ScoresPanel.add(BoardSpacer, ScoresConstraints);             //layout main scores display
        ScoresConstraints.gridx = 2;
        ScoresPanel.add(LivesPanel, ScoresConstraints);
        ScoresConstraints.gridx = 3;
        ScoresPanel.add(BoardSpacer2, ScoresConstraints);
        ScoresConstraints.gridx = 4;
        ScoresPanel.add(TimePanel, ScoresConstraints);
        ScoresConstraints.gridy = 2;
        ScoresConstraints.gridx = GridBagConstraints.REMAINDER;
        ScoresConstraints.weighty = 1.0;
        ScoresPanel.add(BoardSpacer3, ScoresConstraints);

        pack();
        getContentPane().setVisible(true);
        //Component[] components = BoardPanel.getComponents();
        //if(components[1] instanceof JLabel)System.out.println("Label height: " + ((JLabel)components[1]).getHeight());
        //if(components[1] instanceof JButton)System.out.println("Button height: " + ((JButton)components[1]).getHeight());
    }
    //----------------------------------------------leaderboardText()-----------Reads files, creates components based on contents-----------------
    private void leaderboardText(JPanel BoardPanel, JPanel LivesPanel, JPanel TimePanel, ActionListener BoardButtonListener, KeyAdapter keyAdapter){ //sets JLabel text from file input
        String os = System.getProperty("os.name").toLowerCase();
        String scoresFileName;
        if (os.contains("win")) {
            scoresFileName = System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Roaming" + File.separator + "minesweeperScores" + File.separator + "Leaderboard.txt";
        } else {
            scoresFileName = System.getProperty("user.home") + File.separator + ".minesweeper" + File.separator + "Leaderboard.txt";
        }
        String SHL="<u>"; //StartHighLight variable for easily changing tags
        String EHL="</u>";//EndHighLight
        String Shtml="<html>";
        String Ehtml="</html>";
        try(Scanner in = new Scanner(new File(scoresFileName))) {
            String tFile = "";
            while(in.hasNext()){
                tFile += in.next()+" ";
            }
            String[] word = tFile.split("\\s+");
            if(word.length==1 && word[0].isEmpty()){
                FileIssue=true;
                BoardButton = new JButton[1];
                BoardLabel = new JLabel[1];
                JLabel[] LivesLabel = new JLabel[1];
                JLabel[] TimeLabel = new JLabel[1];
                BoardLabel[0] = new JLabel("File");
                BoardButton[0] = new JButton("File");
                LivesLabel[0] = new JLabel("is");
                TimeLabel[0] = new JLabel("empty.");
                BoardPanel.add(BoardLabel[0]);
                LivesPanel.add(LivesLabel[0]);
                TimePanel.add(TimeLabel[0]);
                return;
            }
            String[] current;//initialize the stuff we read the values to
            String[] BoardText = new String[word.length];
            String[] LivesText = new String[word.length];
            String[] TimeText = new String[word.length];
            BoardButton = new JButton[word.length];
            for(int c=0; c<word.length; c++){//initialize board buttons
                BoardButton[c] = new JButton();
                BoardButton[c].putClientProperty("BoardTarget", "");
            }//Board String will be its own property so that we can change how it displays as we wish
            for(int c=0; c<word.length; c++){
                current=word[c].split("-"); //now, current[x] for x=(0,1,2)=(board,lives,time)
                if(current.length>1)try{
                    if(Integer.parseInt(current[1])==0)current[1]="DIED AT";//lives=0 RIP in pieces
                }catch(NumberFormatException e){}
                String[] BoardDisplayStrings = current[0].split(":");//create the format for only the display of the values
                String finalBoardDisplayString;
                if(BoardDisplayStrings.length>3){
                    finalBoardDisplayString = BoardDisplayStrings[0]+"x"+BoardDisplayStrings[1]+" B:"+BoardDisplayStrings[2]+" L:"+BoardDisplayStrings[3];
                }else{finalBoardDisplayString = current[0];}
                if(current.length >= 3){
                    if(current[0].equals(thisBoard)){//This is us! add to start
                        if(word.length>1 && c!=0){
                            for(int add=word.length-1; add>0; add--){//move all values up 1 so we dont overwrite when we place at start
                                BoardButton[add].putClientProperty("BoardTarget",BoardButton[add-1].getClientProperty("BoardTarget"));
                                BoardButton[add].setText(BoardButton[add-1].getText());
                                BoardText[add]=BoardText[add-1];
                                LivesText[add]=LivesText[add-1];
                                TimeText[add]=TimeText[add-1];
                            }
                        }//place our board at start
                        BoardButton[0].putClientProperty("BoardTarget", current[0]);
                        BoardButton[0].setText(Shtml+SHL+finalBoardDisplayString+EHL+Ehtml);
                        BoardText[0] = Shtml+SHL+finalBoardDisplayString+EHL+Ehtml;
                        LivesText[0] = Shtml+SHL+current[1]+EHL+Ehtml;
                        TimeText[0] = Shtml+SHL+current[2]+EHL+Ehtml;
                    } else{//add scores that arent us to end
                        BoardButton[c].putClientProperty("BoardTarget", current[0]);
                        BoardButton[c].setText(Shtml+finalBoardDisplayString+Ehtml);
                        BoardText[c] = Shtml+finalBoardDisplayString+Ehtml;
                        LivesText[c] = Shtml+current[1]+Ehtml;
                        TimeText[c] = Shtml+current[2]+Ehtml;
                    }
                } else {
                    BoardButton[c].setText("entry");
                    BoardText[c] = "entry";
                    LivesText[c] = "length";
                    TimeText[c] = "invalid";
                }
            }
            BoardLabel = new JLabel[word.length];//initialize all the labels and button 
            JLabel[] LivesLabel = new JLabel[word.length];//properties that we didnt need to add pre-read.
            JLabel[] TimeLabel = new JLabel[word.length];
            for(int i=0; i<word.length; i++){
                BoardButton[i].setMargin(new Insets(-1, 0, -1, 0));
                BoardButton[i].setBorderPainted(false);
                BoardButton[i].addActionListener(BoardButtonListener);
                BoardButton[i].addKeyListener(keyAdapter);
                BoardLabel[i] = new JLabel(BoardText[i]);
                BoardLabel[i].setHorizontalAlignment(SwingConstants.CENTER);
                LivesLabel[i] = new JLabel(LivesText[i]);
                LivesLabel[i].setBorder(new EmptyBorder(0, 0, 0, 0));
                TimeLabel[i] = new JLabel(TimeText[i]);
                TimeLabel[i].setBorder(new EmptyBorder(0, 0, 0, 10));
                if(clickable){
                    BoardPanel.add(BoardButton[i]);
                }else{
                    BoardPanel.add(BoardLabel[i]);
                }
                LivesPanel.add(LivesLabel[i]);
                TimePanel.add(TimeLabel[i]);
            }
        }catch(FileNotFoundException e){
            FileIssue=true;
            BoardLabel = new JLabel[1];
            BoardButton = new JButton[1];
            JLabel[] LivesLabel = new JLabel[1];
            JLabel[] TimeLabel = new JLabel[1];
            BoardLabel[0] = new JLabel("File");
            BoardButton[0] = new JButton("File");
            LivesLabel[0] = new JLabel("not");
            TimeLabel[0] = new JLabel("found.");
            BoardPanel.add(BoardLabel[0]);
            LivesPanel.add(LivesLabel[0]);
            TimePanel.add(TimeLabel[0]);
        }
    }
    private void BoardButtonPressedAction(JButton BoardPressed, JFrame ParentFrame){
        String fullboardtext=((String)BoardPressed.getClientProperty("BoardTarget"));
        String[] splitboardtext=fullboardtext.split(":");
        if(splitboardtext.length>3){//dont worry, we check the validity of these inputs later. but we need at least 4
            ParentFrame.dispose();
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new OpeningWindow(splitboardtext[0],splitboardtext[1],splitboardtext[2],splitboardtext[3]).setVisible(true);
                }
            });
            ScoresWindow.this.dispose();
        }
    }
}
