package MySweep;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.EventQueue;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;


public class OpeningWindow extends JFrame {//<-- its a JFrame
    //start off by initializing our variables that will be accessible anywhere within the class 
    //but not outside because they are private!
    //with a public variable, we can say "instanceOfAClass.thePublicVariable" 
    //but with private ones we cannot.
    private JTextField WidthField;//<-- user text (strings) goes into these.
    private JTextField HeightField;//<-- we create the actual instances to go in these in the constructor
    private JTextField BombNumber;
    private JTextField LivesNumber;
    private JButton Start = new JButton("Start!");//<-- the start button!
    private JButton ScoreBoard = new JButton();//<-- user can press these
    private JButton HelpWindow = new JButton();
    private JLabel LifeFieldLabel = new JLabel();//<-- these JLabels will display stuff
    private JLabel WidthFieldLabel = new JLabel();
    private JLabel HeightFieldLabel = new JLabel();
    private JLabel BombFieldLabel = new JLabel();
    private JLabel TitleLabel = new JLabel();
    private JLabel AuthorLabel = new JLabel();
    //-----------------------------------------Constructors----------------------------------------------------------
    //notice a constructor just looks like a function, but it has the same name as the class
    public OpeningWindow(String initialx, String initialy, String initialbombno, String initiallives) {//called by scores window
        WidthField = new JTextField(initialx);
        HeightField = new JTextField(initialy);//<-- these just allow us to pre populate the fields while we create the buttons to go in the variables.
        BombNumber = new JTextField(initialbombno);
        LivesNumber = new JTextField(initiallives);
        initComponents();//<-- sets up our window components
    }
    //it also has no return type (because it always returns an instance of this class.)
    public OpeningWindow() {//called by the rest
        WidthField = new JTextField();
        HeightField = new JTextField();//<-- initialize them without pre populated text instead
        BombNumber = new JTextField();
        LivesNumber = new JTextField();
        initComponents();
    }//--------------------------------and then our functions!---------------------------------------------------------------------------

    //-------------------------------------------------Start Action Performed---called by action listener on start button-------------------
    private void StartActionPerformed() {//this function runs MainGameWindow, performs error checking and displays errors
        //this next part is kinda like in MineSweeper class
        try{
            int width =(int)(Integer.parseInt(WidthField.getText()));//(int) makes sure it is read as an integer. This is called a cast.
            int height =(int)(Integer.parseInt(HeightField.getText()));//Integer.parseInt(String) converts strings to integers
            int bombCount = (int)(Integer.parseInt(BombNumber.getText()));//if the parses fail, the catch clause will be triggered
            int lives = (int)(Integer.parseInt(LivesNumber.getText()));//this is our first check on user inputs
            if(width*height<=bombCount||bombCount<0||lives<1||width<1||height<1){//<-- and here, we check if they are valid numbers for the game
                if(lives<1)LifeFieldLabel.setText("no life");
                if(width<1)WidthFieldLabel.setText("invalid width");//<-- and tell the user what they did wrong 
                if(height<1)HeightFieldLabel.setText("invalid height");//  by setting the text of a label we already had
                if(width*height<=bombCount)BombFieldLabel.setText("Space<Bombs");
                if(bombCount<0)BombFieldLabel.setText("Bombs<0");//notice the if statement at the start of each.
                return;//<-- return early, ending execution of the function, because input was bad.
            }
            EventQueue.invokeLater(new Runnable() {//<-- if you got here, you passed the checks.
                public void run() {
                    new MainGameWindow(width,height,bombCount,lives).setVisible(true);//<-- start the game!
                }
            });
            OpeningWindow.this.dispose();//<-- we dispose windows unless we want to close the whole program.
        }catch(NumberFormatException e){
            TitleLabel.setText("Invalid field(s)");//<-- if an error, tell the user their input was too stringy
        }
    } 
    
    //Yeah but where is the stuff located and what does it look like though? I thought this was a window? Or, a JFrame, or whatever?
    //---------------------------------initComponents()-----called by constructor-----------------------------------------------------------------
    private void initComponents() {//<-- a private function that doesnt return anything. It does stuff though.
        //--------------------------------------add action Listeners to components
        Start.addActionListener(new ActionListener() {//<-- our start button was clicked?
            public void actionPerformed(ActionEvent evt) {
                StartActionPerformed();//<-- run the start function!
            }
        });
        ScoreBoard.addActionListener(new ActionListener() {//<-- action listeners are also interfaces with various functions you can assign
            public void actionPerformed(ActionEvent evt) {//<-- we have assigned the actionPerformed function to do a thing
                EventQueue.invokeLater(new Runnable() {//<-- and that thing is to implement the runnable interface
                    public void run() {//this run() will launch the scores window
                        try{
                            int width =(int)(Integer.parseInt(WidthField.getText()));//get input info so we can highlight the current one.
                            int height =(int)(Integer.parseInt(HeightField.getText()));
                            int bombCount = (int)(Integer.parseInt(BombNumber.getText()));
                            int lives = (int)(Integer.parseInt(LivesNumber.getText()));
                            new ScoresWindow(width,height,bombCount,lives,OpeningWindow.this).setVisible(true);//<-- and then run our scores window
                        }catch(NumberFormatException e){new ScoresWindow(0,0,0,0,OpeningWindow.this).setVisible(true);}
                    }
                });
            }
        });
        HelpWindow.addActionListener(new ActionListener() {//and this one runs our Help window!
            public void actionPerformed(ActionEvent evt) {//these are anonymous interface classes. They are defined within the () of a function call.
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        new instructionsWindow().setVisible(true);
                    }
                });//<-- see?
            }
        });//<-- 2 of them!
        KeyAdapter keyAdapter = new KeyAdapter() {//this one is not defined as an anonymous class. It is called keyAdapter and it is a KeyAdapter.
            public void keyPressed(KeyEvent evt) {//It is a listener though. and an interface.
                // Check if the Enter key is pressed
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    // get focused component source
                    Component CurrComp = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                    if(!(CurrComp instanceof JButton)){//<-- instanceof tells us if a variable is an instance of a certain class (it returns null if nothing is assigned to the variable)
                                      //^this comparison makes it so that enter on a field starts the game too
                        StartActionPerformed();//<-- and here is where we say to start the start if it was a text field
                    }else {               //else,
                        ((JButton)CurrComp).doClick();//<-- doClick() does exactly what it sounds like
                    }
                }//It gives us enter key functionality!
            }    //the tab focusable component property is on by default unless you turn it off in this library.
        };       //so we just need to process the enter action.
        Start.addKeyListener(keyAdapter);
        ScoreBoard.addKeyListener(keyAdapter);//<-- if you dont add listeners directly (anonymously) 
        WidthField.addKeyListener(keyAdapter);//<-you have to add them to the components you want later like this
        HeightField.addKeyListener(keyAdapter);
        BombNumber.addKeyListener(keyAdapter);
        LivesNumber.addKeyListener(keyAdapter);
        HelpWindow.addKeyListener(keyAdapter);

        //set font and initial text
        ScoreBoard.setFont(new Font("Tahoma", 0, 12));
        ScoreBoard.setText("HiSc");
        HelpWindow.setFont(new Font("Tahoma", 0, 12));
        HelpWindow.setText("Help");
        LifeFieldLabel.setFont(new Font("Tahoma", 0, 14));
        LifeFieldLabel.setText("#ofLives:");
        LifeFieldLabel.setHorizontalAlignment(SwingConstants.CENTER);
        WidthFieldLabel.setFont(new Font("Tahoma", 0, 14));
        WidthFieldLabel.setText("Width(in Tiles):");
        WidthFieldLabel.setHorizontalAlignment(SwingConstants.CENTER);
        HeightFieldLabel.setFont(new Font("Tahoma", 0, 14));
        HeightFieldLabel.setText("Height(in Tiles):");
        HeightFieldLabel.setHorizontalAlignment(SwingConstants.CENTER);
        BombFieldLabel.setFont(new Font("Tahoma", 0, 14));
        BombFieldLabel.setText("#ofBombs");
        BombFieldLabel.setHorizontalAlignment(SwingConstants.CENTER);
        TitleLabel.setFont(new Font("Tahoma", 0, 36));
        TitleLabel.setText("Mine Sweeper");
        AuthorLabel.setFont(new Font("Tahoma", 0, 12));
        AuthorLabel.setText("-Birdee");
        AuthorLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        //--------------------------now to add our stuff to our content pane----------------

        //Layout Managing:
        //layout managers allow you to tell the program where to put the stuff without specifying actual pixels.
        //this greatly simplifies making stuff be the right size in the right places on different screens and window sizes
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setPreferredSize(new Dimension(300, 200));
        getContentPane().setLayout(new GridBagLayout());//<-- add the layout manager
        GridBagConstraints containerConstraints = new GridBagConstraints();//<-- you modify this, and add a thing to the pane with it
                //when you do that, it imbues that thing with its position and size attributes. You then modify it and do it again with a new thing
                //after you add the thing, you can change the constraints object without affecting how you set the previous thing
        //Thats how this layout manager works. There are others. Like grid. Which is a regular grid. This is a grid bag. Bags are more flexible than grids

        containerConstraints.gridx =2;
        containerConstraints.gridy =0;
        containerConstraints.gridwidth =3;
        containerConstraints.gridheight =1;
        containerConstraints.fill = GridBagConstraints.BOTH;
        getContentPane().add(TitleLabel, containerConstraints);

        containerConstraints.gridx =4;
        containerConstraints.gridy =1;
        containerConstraints.gridwidth =1;
        containerConstraints.gridheight =1;
        containerConstraints.fill = GridBagConstraints.BOTH;
        getContentPane().add(AuthorLabel, containerConstraints);

        containerConstraints.gridx =2;
        containerConstraints.gridy =2;
        containerConstraints.gridwidth =1;
        containerConstraints.gridheight =1;
        containerConstraints.fill = GridBagConstraints.BOTH;
        getContentPane().add(WidthFieldLabel, containerConstraints);

        containerConstraints.gridx =4;
        containerConstraints.gridy =2;
        containerConstraints.gridwidth =1;
        containerConstraints.gridheight =1;
        containerConstraints.fill = GridBagConstraints.BOTH;
        getContentPane().add(HeightFieldLabel, containerConstraints);

        containerConstraints.gridx =2;
        containerConstraints.gridy =3;
        containerConstraints.gridwidth =1;
        containerConstraints.gridheight =1;
        containerConstraints.fill = GridBagConstraints.BOTH;
        getContentPane().add(WidthField, containerConstraints);

        containerConstraints.gridx =4;
        containerConstraints.gridy =3;
        containerConstraints.gridwidth =1;
        containerConstraints.gridheight =1;
        containerConstraints.fill = GridBagConstraints.BOTH;
        getContentPane().add(HeightField, containerConstraints);

        containerConstraints.gridx =2;
        containerConstraints.gridy =4;
        containerConstraints.gridwidth =1;
        containerConstraints.gridheight =1;
        containerConstraints.fill = GridBagConstraints.BOTH;
        getContentPane().add(BombFieldLabel, containerConstraints);

        containerConstraints.gridx =4;
        containerConstraints.gridy =4;
        containerConstraints.gridwidth =1;
        containerConstraints.gridheight =1;
        containerConstraints.fill = GridBagConstraints.BOTH;
        getContentPane().add(LifeFieldLabel, containerConstraints);

        containerConstraints.gridx =2;
        containerConstraints.gridy =5;
        containerConstraints.gridwidth =1;
        containerConstraints.gridheight =1;
        containerConstraints.fill = GridBagConstraints.BOTH;
        getContentPane().add(BombNumber, containerConstraints);

        containerConstraints.gridx =4;
        containerConstraints.gridy =5;
        containerConstraints.gridwidth =1;
        containerConstraints.gridheight =1;
        containerConstraints.fill = GridBagConstraints.BOTH;
        getContentPane().add(LivesNumber, containerConstraints);

        containerConstraints.gridx =2;
        containerConstraints.gridy =6;
        containerConstraints.gridwidth =1;
        containerConstraints.gridheight =1;
        containerConstraints.fill = GridBagConstraints.BOTH;
        getContentPane().add(ScoreBoard, containerConstraints);

        containerConstraints.gridx =2;
        containerConstraints.gridy =7;
        containerConstraints.gridwidth =1;
        containerConstraints.gridheight =1;
        containerConstraints.fill = GridBagConstraints.BOTH;
        getContentPane().add(HelpWindow, containerConstraints);

        containerConstraints.gridx =4;
        containerConstraints.gridy =6;
        containerConstraints.gridwidth =1;
        containerConstraints.gridheight =2;
        containerConstraints.fill = GridBagConstraints.BOTH;
        getContentPane().add(Start, containerConstraints);

        pack();//<-- this pack(); causes it to evaluate sizes and paint the contents of the pane
        getContentPane().setVisible(true);//<-- then this displays the pane
    }
    //made it to the end? time for main game window!
}
