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


public class OpeningWindow extends JFrame {
    public OpeningWindow(String initialx, String initialy, String initialbombno, String initiallives) {//called by scores window
        JTextField WidthField = new JTextField(initialx);
        JTextField HeightField = new JTextField(initialy);
        JTextField BombNumber = new JTextField(initialbombno);
        JTextField LivesNumber = new JTextField(initiallives);
        initComponents(WidthField,HeightField,BombNumber,LivesNumber);
    }
    public OpeningWindow() {//called by the rest
        JTextField WidthField = new JTextField();
        JTextField HeightField = new JTextField();
        JTextField BombNumber = new JTextField();
        JTextField LivesNumber = new JTextField();
        initComponents(WidthField,HeightField,BombNumber,LivesNumber);
    }
    private void initComponents(JTextField WidthField, JTextField HeightField, JTextField BombNumber, JTextField LivesNumber) {
        //init components and listeners
        JButton Start = new JButton();
        JButton ScoreBoard = new JButton();
        JButton HelpWindow = new JButton();
        JLabel LifeFieldLabel = new JLabel();
        JLabel WidthFieldLabel = new JLabel();
        JLabel HeightFieldLabel = new JLabel();
        JLabel BombFieldLabel = new JLabel();
        JLabel TitleLabel = new JLabel();
        JLabel AuthorLabel = new JLabel();

        //add properties to pass to start listener
        Start.setText("Start!");
        Start.putClientProperty("WidthField", WidthField);
        Start.putClientProperty("HeightField", HeightField);
        Start.putClientProperty("BombNumber", BombNumber);
        Start.putClientProperty("LivesNumber", LivesNumber);
        Start.putClientProperty("TitleLabel", TitleLabel);
        Start.putClientProperty("LifeFieldLabel", LifeFieldLabel);
        Start.putClientProperty("HeightFieldLabel", HeightFieldLabel);
        Start.putClientProperty("WidthFieldLabel", WidthFieldLabel);
        Start.putClientProperty("BombFieldLabel", BombFieldLabel);

        //add action Listeners
        Start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                JButton Start=(JButton)evt.getSource();
                StartActionPerformed(Start);
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
                    StartActionPerformed(Start);
                }else((JButton)CurrComp).doClick();
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

    private void StartActionPerformed(JButton Start) {//runs grid, performs error checking and displays errors
        JTextField WidthField=(JTextField)(Start).getClientProperty("WidthField");
        JTextField HeightField=(JTextField)(Start).getClientProperty("HeightField");
        JTextField BombNumber=(JTextField)(Start).getClientProperty("BombNumber");
        JTextField LivesNumber=(JTextField)(Start).getClientProperty("LivesNumber");
        JLabel TitleLabel=(JLabel)(Start).getClientProperty("TitleLabel");
        JLabel LifeFieldLabel=(JLabel)(Start).getClientProperty("LifeFieldLabel");
        JLabel HeightFieldLabel=(JLabel)(Start).getClientProperty("HeightFieldLabel");
        JLabel WidthFieldLabel=(JLabel)(Start).getClientProperty("WidthFieldLabel");
        JLabel BombFieldLabel=(JLabel)(Start).getClientProperty("BombFieldLabel");
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
    } 
}
