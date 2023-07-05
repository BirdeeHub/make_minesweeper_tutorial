//This class contains the main function and launches the opening window (or the main game window with optional command line arguments).
//It also contains 2 static references so that I can easily access them from anywhere in the game code. 
//One is to my scores saving class. The other just tells the program if it has been compiled into a jar so that everything still works when you use an IDE to run it
package MySweep;//<-- this class is in the MySweep package, and thus, the files will be in the MySweep folder. It means more stuff but thats what matters right now.

import javax.swing.UIManager;//<-- import the functions used from their libraries.
import javax.swing.UnsupportedLookAndFeelException;//smart editors can do this for you
import java.awt.EventQueue;
import java.io.FileInputStream;
import java.nio.file.Paths;
class MineSweeper {
    //these are public variables and functions. This means if you can reference an instance of this class, you can reference these functions.
    //they are also static, which means you dont need to use an instance. i.e. if(MineSweeper.isJarJile())doSomething();
    //dont worry too hard about it untill you see private ones. Then this will make sense.
    //a public and static variable:
    public static ScoresFileIO scoresFileIO = new ScoresFileIO();//<-- Scores file input output
    //a public and static function:
    public static boolean isJarFile() {//<-- apparently .jar files have a magic number that shows if it is a jar file.
        try (FileInputStream fileInputStream = new FileInputStream(Paths.get(System.getProperty("java.class.path")).toFile())) {
            byte[] magicNumber = new byte[4];
            int bytesRead = fileInputStream.read(magicNumber);
            return bytesRead == 4 &&
                    (magicNumber[0] == 0x50 && magicNumber[1] == 0x4B && magicNumber[2] == 0x03 && magicNumber[3] == 0x04)
                    || (magicNumber[0] == (byte) 0x80 && magicNumber[1] == 0x75 && magicNumber[2] == 0x03 && magicNumber[3] == 0x04);
        } catch (Exception e) {
            return false;
        }
    }
    /** --a fancy comment-- @param aaaaaarg!!!
     * @param args int width, int height, int BombCount, int lives
     */
    public static void main(String[] args) {// MAIN FUNCTION!! EXECUTION STARTS HERE!
        try {
            // Set cross-platform Java L&F (also called "Metal")
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());//<-- If you dont do this some swing library stuff breaks on mac
        }
        catch (UnsupportedLookAndFeelException e) {
        }
        catch (ClassNotFoundException e) {
        }
        catch (InstantiationException e) {
        }
        catch (IllegalAccessException e) {
        }
        //In this section, dont worry too much about the event queue stuff for now, but it is an example of an interface.
        int width, height, bombCount, lives;//<-- this is a variable declaration.
        if(args.length == 4){//<-- processing command line arguments. If we got 4 arguments from the command line, do:
            try{//<-- try means handle errors if they happen. Language extensions will tell you when you need these
                width = (int)(Integer.parseInt(args[0]));//<-- (int) makes sure it is read as an integer. this is called a "cast"
                height = (int)(Integer.parseInt(args[1]));//<-- Integer.parseInt(String) converts strings to integers
                bombCount = (int)(Integer.parseInt(args[2]));
                lives = (int)(Integer.parseInt(args[3]));
                if(width > 0 && height > 0 && bombCount > 0 && lives > 0){//<-- if you put in 4 positive numbers
                    EventQueue.invokeLater(new Runnable(){//invoke later just well... it runs it later. not much later though.
                        public void run(){//JFrames work best initiated this way, and the window classes extend JFrame
                            new MainGameWindow(width, height, bombCount, lives).setVisible(true);//<-- launch main game window with the arguments
                        }
                    });
                }else{//                                 <--- else if not correct arguments, 
                    System.out.println("integer arguments only: width, height, BombCount, lives (where all are > 0)");
                    EventQueue.invokeLater(new Runnable(){//<-- this is called an anonymous class/interface/whatever
                        public void run(){    //you cant reference them outside of the fact that that they are now a property of what you added them to.
                            new OpeningWindow().setVisible(true);//<-- launch opening window
                        }
                    });//<-- see how it is defined inside the () of the function?
                }
            }catch(NumberFormatException e){//<-- if integer.parseInt threw an error (the string wasnt a number i.e. 1), then do:
                System.out.println("integer arguments only: width, height, BombCount, lives (where all are > 0)");
                EventQueue.invokeLater(new Runnable(){public void run(){new OpeningWindow().setVisible(true);}});
            }//    ^you can also write short 1 command interface definitions like this. ^ this is the same thing as the last opening window call

        //-----------------basically the same thing but 5 arguments--(and no comment clutter)-------------------------
        }else if(args.length == 5){
            if(args[0].equals("o")||args[0].equals("m")){
                try{
                    width = (int)(Integer.parseInt(args[1]));
                    height = (int)(Integer.parseInt(args[2]));
                    bombCount = (int)(Integer.parseInt(args[3]));
                    lives = (int)(Integer.parseInt(args[4]));
                    if(width > 0 && height > 0 && bombCount > 0 && lives > 0){
                        if(args[0].equals("m")){
                            EventQueue.invokeLater(new Runnable(){public void run(){
                                new MainGameWindow(width, height, bombCount, lives).setVisible(true);
                            }});
                        }
                        if(args[0].equals("o")){
                            EventQueue.invokeLater(new Runnable(){public void run(){
                                new OpeningWindow(Integer.toString(width),Integer.toString(height), Integer.toString(bombCount), Integer.toString(lives)).setVisible(true);
                            }});
                        }
                    }else{
                        System.out.println("<m or o>, width, height, BombCount, lives");
                        EventQueue.invokeLater(new Runnable(){public void run(){new OpeningWindow().setVisible(true);}});
                    }
                }catch(NumberFormatException e){
                    System.out.println("<m or o>, width, height, BombCount, lives");
                    EventQueue.invokeLater(new Runnable(){public void run(){new OpeningWindow().setVisible(true);}});
                }
            }else{
                System.out.println("<m or o>, width, height, BombCount, lives");
                EventQueue.invokeLater(new Runnable(){public void run(){new OpeningWindow().setVisible(true);}});
            }
        }else{
            EventQueue.invokeLater(new Runnable(){public void run(){new OpeningWindow().setVisible(true);}});
        }
    }
}
