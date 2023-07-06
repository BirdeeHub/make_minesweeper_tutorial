//This class contains the main function and launches the opening window (or the main game window with optional command line arguments).
//It also contains 2 static references so that I can easily access them from anywhere in the game code. 
//One is to my scores saving class. The other just tells the program if it has been compiled into a jar so that everything still works when you use an IDE to run it

package MySweep;//<-- this class is in the MySweep package, and thus, the files will be in the MySweep folder. It means more stuff but thats what matters right now.

import javax.swing.UIManager;//<-- import the functions used from their libraries.
import javax.swing.UnsupportedLookAndFeelException;//smart editors can do this for you
import java.awt.EventQueue;
import java.io.FileInputStream;//these libraries, i.e. java.awt are also packages in java
import java.nio.file.Paths;
class MineSweeper {//<-- the start of our first class
    /** --a fancy comment-- @param aaaaaarg!!!
     * These are our possible command line arguments (all > 0)
     * @param args [String <o or m>], int width, int height, int BombCount, int lives
     */
    public static void main(String[] args) {// <-- MAIN FUNCTION!! EXECUTION STARTS HERE! return type is void. (it doesnt return anything)
        //^public and static are defined at the end of this file for you to read then.

        try {
            // Set cross-platform Java L&F (also called "Metal") (I found out that if you dont do this some swing library stuff breaks on mac)
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());//<-- I copy pasted this straight from the oracle documentation
        }                                                       //and thats just fine. Now I can set button backgrounds on mac.
        catch (UnsupportedLookAndFeelException e) {
        }
        catch (ClassNotFoundException e) {
        }
        catch (InstantiationException e) {
        }
        catch (IllegalAccessException e) {
        }

        //In this section, dont worry too much about the event queue stuff for now, but it runs an example of an interface.
        //just know its a function that we use to run our windows.
        //I barely knew what an interface was but google told me how to run a JFrame. I know what it does now but I didn't originally
        //I first had to google "How to create a window in java" to even find out there was a class called a JFrame
        //Rest assured, I will probably be googling "What is the best library for creating windows in python" (or GO) at some point.

        int width, height, bombCount, lives;//<-- this is a variable declaration for 4 integers. There is nothing in them yet.
        if(args.length == 4){//<-- processing command line arguments. If we got 4 arguments from the command line, do:
            try{//<-- try means handle errors if they happen. Language extensions will tell you when you need these

                //try to parse the arguments
                width = (int)(Integer.parseInt(args[0]));//<-- checking and saving each index in our args array (which is an array of strings)
                height = (int)(Integer.parseInt(args[1]));//<-- each index in it corresponds to a string. This is the second item.
                bombCount = (int)(Integer.parseInt(args[2]));//<-- Integer.parseInt(String) converts strings to integers
                lives = (int)(Integer.parseInt(args[3]));//<-- (int) makes sure it is read as an integer. this is called a "cast"

                if(width > 0 && height > 0 && bombCount > 0 && lives > 0){//<-- if you put in 4 positive numbers
                                                                            //then launch main game window with those numbers as input

                    EventQueue.invokeLater(new Runnable(){//<-- invoke later just well... it runs it later. not much later though.
                        public void run(){//(JFrames are initiated this way, and the window classes extend JFrame)
                            //(notice the "new" though? it is used to call a constructor for a class)
                            new MainGameWindow(width, height, bombCount, lives).setVisible(true);//<-- launch main game window with the arguments,
                        }                                                                                   //(now in integer form)
                    });

                }else{//                                                                      <--- else if not correct arguments, 
                    System.out.println("integer arguments only: width, height, BombCount, lives (where all are > 0)");//<-- print an error message
                                                                            //then launch opening window
                    EventQueue.invokeLater(new Runnable(){//<-- this is called an anonymous class/interface/whatever
                        public void run(){    //you cant reference them outside of the fact that that they are now a property of what you added them to.
                            new OpeningWindow().setVisible(true);//<-- launch opening window
                        }
                    });//<-- see how it is defined inside the () of the function?

                }
            }catch(NumberFormatException e){//<-- if integer.parseInt threw an error (the string wasnt a number i.e. 1), then do:
                System.out.println("integer arguments only: width, height, BombCount, lives (where all are > 0)");//<-- print an error message

                EventQueue.invokeLater(new Runnable(){public void run(){new OpeningWindow().setVisible(true);}});//<-- launch opening window
                //    ^you can also write short 1 command interface definitions like this. ^ this is the same thing as the last opening window call
            }

        //-----------------basically the same thing but 5 arguments--(and no comment clutter)-------------------------
        }else if(args.length == 5){
            if(args[0].equals("o")||args[0].equals("m")){//<-- aString.equals(anotherString) is how you compare strings.
                try{
                    width = (int)(Integer.parseInt(args[1]));//<-- same as before
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
                                new OpeningWindow(args[1],args[2], args[3], args[4]).setVisible(true);//<-- Opening window can take strings
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
    
    //the following are public variables and functions. This means if you can reference an instance of this class, you can reference these functions.
    //they are also static, which means you dont need to use an instance. 
    //i.e. if(MineSweeper.isJarJile())doSomething(); rather than if(instanceOfMineSweeper.isJarFile())doSomething();
    //dont worry too hard about it untill you see private ones on opening window. Then this will make sense.

    //a public and static variable:
    public static ScoresFileIO scoresFileIO = new ScoresFileIO();//<-- Scores file input output
                  //^ it returns a reference to this instance of ScoresFileIO
                  //it is used so that all classes in the game can easily access an instance of ScoresFileIO

    //a public and static function:
    public static boolean isJarFile() {//<-- apparently .jar files have a magic number that shows if it is a jar file.
                    //^you can also see that it returns a boolean, i.e. true or false.
        
        //we try to open the file we are running from to read it 
        //(you need to do this to read 4 bytes from a file. Dont worry. Just pay attention to "public static boolean" right now.)
        try (FileInputStream fileInputStream = new FileInputStream(Paths.get(System.getProperty("java.class.path")).toFile())) {//<-- start file input stream from where we are running from
            byte[] magicNumber = new byte[4];
            int bytesRead = fileInputStream.read(magicNumber);
            return bytesRead == 4 &&//<-- whitespace doesnt matter in code.
                    (magicNumber[0] == 0x50 && magicNumber[1] == 0x4B && magicNumber[2] == 0x03 && magicNumber[3] == 0x04)//wow what a long comparison
                    || (magicNumber[0] == (byte) 0x80 && magicNumber[1] == 0x75 && magicNumber[2] == 0x03 && magicNumber[3] == 0x04);//<-- it ends here
        } catch (Exception e) {
            return false;
        }        //dont worry about what a magic number is. 
    }      //It is read from a file, and the function returns true when it is there. If the number is there, its a .jar file
}
//Got this well enough? OpeningWindow Time!