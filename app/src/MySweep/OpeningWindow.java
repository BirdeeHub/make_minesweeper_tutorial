package MySweep;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.EventQueue;
import java.awt.Color;
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
    //misc
    private final Color PURPLE = new Color(58, 0, 82);
    private final Color LIGHTPRPL = new Color(215, 196, 255);
    private static final Icon DefaultButtonIcon = (new JButton()).getIcon();//<-- final means that after you assign this variable, it cannot be changed. you can have static AND final if you want. 
    //-----------------------------------------Constructors----------------------------------------------------------
    //notice a constructor just looks like a function, but it has the same name as the class
    public OpeningWindow(String initialx, String initialy, String initialbombno, String initiallives) {//<-- called when you have 4 strings as arguments
        WidthField = new JTextField(initialx);
        HeightField = new JTextField(initialy);//<-- when you use the JTextField constructor with a string as an argument,
        BombNumber = new JTextField(initialbombno);             //it prepopulates the field with that string
        LivesNumber = new JTextField(initiallives);
        initComponents();//<-- sets up our window components
    }
    //A Constructor also has no return type (because it always is used with new to return an instance of this class.)
    public OpeningWindow() {//<-- called when you use no arguments
        WidthField = new JTextField();
        HeightField = new JTextField();//<-- initialize them without pre populated text instead
        BombNumber = new JTextField();
        LivesNumber = new JTextField();
        initComponents();
    }//--------------------------------and then our functions!---------------------------------------------------------------------------

    //-------------------------------------------------Start Action Performed---called by action listener on start button-------------------
    private void StartActionPerformed() {//this function runs MainGameWindow, performs error checking and displays errors
        //this next part is like in MineSweeper class
        try{
            int width =(int)(Integer.parseInt(WidthField.getText()));//(int) makes sure it is read as an integer. This is called a cast.
            int height =(int)(Integer.parseInt(HeightField.getText()));//Integer.parseInt(String) converts strings to integers
            int bombCount = (int)(Integer.parseInt(BombNumber.getText()));//if the parses fail, the catch clause will be triggered
            int lives = (int)(Integer.parseInt(LivesNumber.getText()));//this is our check on user inputs
            if(width*height<=bombCount||bombCount<0||lives<1||width<1||height<1){//<-- and here, we check if they are valid numbers for the game
                if(lives<1){
                    LifeFieldLabel.setText("no life");//<-- and tell the user what they did wrong
                }
                if(width<1){
                    WidthFieldLabel.setText("invalid width");//  by setting the text of a label we already had
                } 
                if(height<1){//<-- but we want to give them the correct message
                    HeightFieldLabel.setText("invalid height");
                }
                if(width*height<=bombCount){//<-- so only set the relevant one
                    BombFieldLabel.setText("Space<Bombs");
                }
                if(bombCount<0){
                    BombFieldLabel.setText("Bombs<0");
                }
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

        this.setIconImage(MineSweeper.MineIcon);//<-- see how easy it is to reference our public static variable from MineSweeper?
        //^ here we set that little icon in the top left corner
        //"this.something" allows us to refer to 'something' belonging to 'this' instance of the class
        //in this case, 'something' is a function called setIconImage() belonging to the class named OpeningWindow, which is a JFrame

        //--------------------------------------add action Listeners to components
        Start.addActionListener(new ActionListener() {//<-- our start button was clicked?
            public void actionPerformed(ActionEvent evt) {
                StartActionPerformed();//<-- run the start function!
            }
        });
        ScoreBoard.addActionListener(new ActionListener() {//<-- action listeners are also interfaces with various functions you can assign
            public void actionPerformed(ActionEvent evt) {//<-- we have assigned the actionPerformed function to do a thing
                EventQueue.invokeLater(new Runnable() {//<-- and that thing is to implement the runnable interface
                    public void run() {//<-- this run() will launch the scores window. It is part of the Runnable class, but we have to define it.
                        try{
                            int width =(int)(Integer.parseInt(WidthField.getText()));//get input info so we can highlight the current one.
                            int height =(int)(Integer.parseInt(HeightField.getText()));
                            int bombCount = (int)(Integer.parseInt(BombNumber.getText()));
                            int lives = (int)(Integer.parseInt(LivesNumber.getText()));
                            new ScoresWindow(width,height,bombCount,lives,OpeningWindow.this).setVisible(true);//<-- and then run our scores window
                        }catch(NumberFormatException e){
                            new ScoresWindow(OpeningWindow.this).setVisible(true);//<-- if bad input, use the other constructor
                        }
                    }
                });
            }
        });
        HelpWindow.addActionListener(new ActionListener() {//and this one runs our Help window!
            public void actionPerformed(ActionEvent evt) {//these are anonymous interface classes. They are defined within the () of a function call.
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        new InstructionsWindow().setVisible(true);
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
            }    //the tab focusable component property is on by default unless you turn it off in Swing.
        };       //so we just need to process the enter button action.
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

        setDarkMode();//<-- call this here too so we dont have to type it again.

        //You can actually use this syntax when you use 'new' to modify any class rather than just interfaces.
        JPanel backgroundPanel = new JPanel(){//<-- new ClassConstructor(...){Your Stuff Here};
            @Override//<-- use @Override to modify the original function of the class (works for extends as well!)
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor((MineSweeper.isDarkMode())?PURPLE:LIGHTPRPL);//<-- this one makes our background a prettier color.
                g.fillRect(0, 0, getWidth(), getHeight());
            }//^unfortunately, I had to do this, because when using component.setBackground(color) the color gets inherited by child components if their background is null.
        };

        //--------------------------now to add our stuff to our content pane----------------

        //Layout Managing:
        //layout managers allow you to tell the program where to put the stuff without specifying actual pixels.
        //this greatly simplifies making stuff be the right size in the right places on different screens and window sizes
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setPreferredSize(new Dimension(300, 200));
        getContentPane().add(backgroundPanel);//<-- it's generally best practice to have the contents in their own panel, so you can alter things about it easily, like we did above.
        backgroundPanel.setLayout(new GridBagLayout());//<-- set the layout manager of the panel
        GridBagConstraints containerConstraints = new GridBagConstraints();//<-- you modify this, and add a thing to the pane with it
                //when you do that, it imbues that thing with its position and size attributes. You then modify it and do it again with a new thing
                //after you add the thing, you can change the constraints object without affecting how you set the previous thing
        //Thats how this layout manager works. There are others. Like grid. Which is a regular grid. This is a grid bag. Bags are more flexible than grids

        containerConstraints.gridx =2;//set some values for x and y and whatever of the constraints
        containerConstraints.gridy =0;
        containerConstraints.gridwidth =3;
        containerConstraints.gridheight =1;
        containerConstraints.fill = GridBagConstraints.BOTH;
        backgroundPanel.add(TitleLabel, containerConstraints);//<-- then add your component to the panel, but with containerConstraints as a 2nd argument

        containerConstraints.gridx =4;//<-- you can then change the values you want to, and then repeat the process, 
        containerConstraints.gridy =1;// and TitleLabel will keep the previous setting as its position.
        containerConstraints.gridwidth =1;
        containerConstraints.gridheight =1;
        containerConstraints.fill = GridBagConstraints.BOTH;
        backgroundPanel.add(AuthorLabel, containerConstraints);//<-- and this will recieve the new values, along with whatever wasnt changed from before.

        containerConstraints.gridx =2;
        containerConstraints.gridy =2;
        containerConstraints.gridwidth =1;
        containerConstraints.gridheight =1;
        containerConstraints.fill = GridBagConstraints.BOTH;
        backgroundPanel.add(WidthFieldLabel, containerConstraints);

        containerConstraints.gridx =4;
        containerConstraints.gridy =2;
        containerConstraints.gridwidth =1;
        containerConstraints.gridheight =1;
        containerConstraints.fill = GridBagConstraints.BOTH;
        backgroundPanel.add(HeightFieldLabel, containerConstraints);

        containerConstraints.gridx =2;
        containerConstraints.gridy =3;
        containerConstraints.gridwidth =1;
        containerConstraints.gridheight =1;
        containerConstraints.fill = GridBagConstraints.BOTH;
        backgroundPanel.add(WidthField, containerConstraints);

        containerConstraints.gridx =4;
        containerConstraints.gridy =3;
        containerConstraints.gridwidth =1;
        containerConstraints.gridheight =1;
        containerConstraints.fill = GridBagConstraints.BOTH;
        backgroundPanel.add(HeightField, containerConstraints);

        containerConstraints.gridx =2;
        containerConstraints.gridy =4;
        containerConstraints.gridwidth =1;
        containerConstraints.gridheight =1;
        containerConstraints.fill = GridBagConstraints.BOTH;
        backgroundPanel.add(BombFieldLabel, containerConstraints);

        containerConstraints.gridx =4;
        containerConstraints.gridy =4;
        containerConstraints.gridwidth =1;
        containerConstraints.gridheight =1;
        containerConstraints.fill = GridBagConstraints.BOTH;
        backgroundPanel.add(LifeFieldLabel, containerConstraints);

        containerConstraints.gridx =2;
        containerConstraints.gridy =5;
        containerConstraints.gridwidth =1;
        containerConstraints.gridheight =1;
        containerConstraints.fill = GridBagConstraints.BOTH;
        backgroundPanel.add(BombNumber, containerConstraints);

        containerConstraints.gridx =4;
        containerConstraints.gridy =5;
        containerConstraints.gridwidth =1;
        containerConstraints.gridheight =1;
        containerConstraints.fill = GridBagConstraints.BOTH;
        backgroundPanel.add(LivesNumber, containerConstraints);

        containerConstraints.gridx =2;
        containerConstraints.gridy =6;
        containerConstraints.gridwidth =1;
        containerConstraints.gridheight =1;
        containerConstraints.fill = GridBagConstraints.BOTH;
        backgroundPanel.add(ScoreBoard, containerConstraints);

        containerConstraints.gridx =2;
        containerConstraints.gridy =7;
        containerConstraints.gridwidth =1;
        containerConstraints.gridheight =1;
        containerConstraints.fill = GridBagConstraints.BOTH;
        backgroundPanel.add(HelpWindow, containerConstraints);

        containerConstraints.gridx =4;
        containerConstraints.gridy =6;
        containerConstraints.gridwidth =1;
        containerConstraints.gridheight =2;
        containerConstraints.fill = GridBagConstraints.BOTH;
        backgroundPanel.add(Start, containerConstraints);

        pack();//<-- this pack(); causes it to evaluate sizes and paint the contents of the pane
        getContentPane().setVisible(true);//<-- then this displays the pane
    }
    void toggleDarkMode(){//<-- MineSweeper.toggleDarkMode() calls this function
        setDarkMode();//<-- and it calls this
        repaint();
    }
    private void setDarkMode(){//sets colors appropriately based on DarkMode
        if(MineSweeper.isDarkMode()){
            Start.setForeground(Color.WHITE);
            ScoreBoard.setForeground(Color.WHITE);
            HelpWindow.setForeground(Color.WHITE);
            Start.setBackground(Color.BLACK);
            ScoreBoard.setBackground(Color.BLACK);
            HelpWindow.setBackground(Color.BLACK);
            LifeFieldLabel.setForeground(Color.WHITE);
            WidthFieldLabel.setForeground(Color.WHITE);
            HeightFieldLabel.setForeground(Color.WHITE);
            BombFieldLabel.setForeground(Color.WHITE);
            TitleLabel.setForeground(Color.GREEN);
            AuthorLabel.setForeground(Color.GREEN);
        }else{
            Start.setBackground(null);
            Start.setIcon(DefaultButtonIcon);
            Start.setForeground(Color.BLACK);
            ScoreBoard.setBackground(null);
            ScoreBoard.setIcon(DefaultButtonIcon);
            ScoreBoard.setForeground(Color.BLACK);
            HelpWindow.setBackground(null);
            HelpWindow.setIcon(DefaultButtonIcon);
            HelpWindow.setForeground(Color.BLACK);
            LifeFieldLabel.setForeground(Color.BLACK);
            WidthFieldLabel.setForeground(Color.BLACK);
            HeightFieldLabel.setForeground(Color.BLACK);
            BombFieldLabel.setForeground(Color.BLACK);
            TitleLabel.setForeground(Color.BLACK);
            AuthorLabel.setForeground(Color.BLACK);
        }
    }
    //Made it to the end?

    //Now time for main game window!
}
