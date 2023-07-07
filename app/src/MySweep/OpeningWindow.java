package MySweep;
import javax.swing.ImageIcon;
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


public class OpeningWindow extends JFrame {
    private JTextField WidthField;
    private JTextField HeightField;
    private JTextField BombNumber;
    private JTextField LivesNumber;
    private JButton Start = new JButton("Start!");
    private JButton ScoreBoard = new JButton();
    private JButton HelpWindow = new JButton();
    private JLabel LifeFieldLabel = new JLabel();
    private JLabel WidthFieldLabel = new JLabel();
    private JLabel HeightFieldLabel = new JLabel();
    private JLabel BombFieldLabel = new JLabel();
    private JLabel TitleLabel = new JLabel();
    private JLabel AuthorLabel = new JLabel();
    //-----------------------------------------constructors----------------------------------------------------------
    public OpeningWindow(String initialx, String initialy, String initialbombno, String initiallives) {//called by scores window
        WidthField = new JTextField(initialx);
        HeightField = new JTextField(initialy);
        BombNumber = new JTextField(initialbombno);
        LivesNumber = new JTextField(initiallives);
        initComponents();
    }
    public OpeningWindow() {//called by the rest
        WidthField = new JTextField();
        HeightField = new JTextField();
        BombNumber = new JTextField();
        LivesNumber = new JTextField();
        initComponents();
    }//----------------------------------------------------------Start Action-------------------------------------------------
    private void StartActionPerformed() {//runs MainGameWindow, performs error checking and displays errors
        try{
            int width =(int)(Integer.parseInt(WidthField.getText()));
            int height =(int)(Integer.parseInt(HeightField.getText()));
            int bombCount = (int)(Integer.parseInt(BombNumber.getText()));
            int lives = (int)(Integer.parseInt(LivesNumber.getText()));
            if(width*height<=bombCount||bombCount<0||lives<1||width<1||height<1){
                if(lives<1)LifeFieldLabel.setText("no life");
                if(width<1)WidthFieldLabel.setText("invalid width");
                if(height<1)HeightFieldLabel.setText("invalid height");
                if(width*height<=bombCount)BombFieldLabel.setText("Space<Bombs");
                if(bombCount<0)BombFieldLabel.setText("Bombs<0");
                return;
            }
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new MainGameWindow(width,height,bombCount,lives).setVisible(true);
                }
            });
            OpeningWindow.this.dispose();
        }catch(NumberFormatException e){TitleLabel.setText("Invalid field(s)");}
    } //----------------------------------------------------------initComponents-----------------------------------------------
    private void initComponents() {
        //add action Listeners
        Start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                StartActionPerformed();
            }
        });
        ScoreBoard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        try{
                            int width =(int)(Integer.parseInt(WidthField.getText()));
                            int height =(int)(Integer.parseInt(HeightField.getText()));
                            int bombCount = (int)(Integer.parseInt(BombNumber.getText()));
                            int lives = (int)(Integer.parseInt(LivesNumber.getText()));
                            new ScoresWindow(width,height,bombCount,lives,OpeningWindow.this).setVisible(true);
                        }catch(NumberFormatException e){new ScoresWindow(0,0,0,0,OpeningWindow.this).setVisible(true);}
                    }
                });
            }
        });
        HelpWindow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        new instructionsWindow().setVisible(true);
                    }
                });
            }
        });
        KeyAdapter keyAdapter = new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                // Check if the Enter key is pressed
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    // get focused component source
                    Component CurrComp = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                    if(!(CurrComp instanceof JButton)){
                        StartActionPerformed();
                    }else ((JButton)CurrComp).doClick();
                }
            }
        };
        Start.addKeyListener(keyAdapter);
        ScoreBoard.addKeyListener(keyAdapter);
        WidthField.addKeyListener(keyAdapter);
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

        //Layout Managing:
        setIconImage(MineSweeper.MineIcon);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setPreferredSize(new Dimension(300, 200));
        getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints containerConstraints = new GridBagConstraints();

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

        pack();
        getContentPane().setVisible(true);
    }
}
