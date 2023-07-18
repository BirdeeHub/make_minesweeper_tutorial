/*
    This class contains the main function and launches the opening window (or the main game window with optional command line arguments).
    It also contains some references we will use later in the game.

    In this file, dont worry too much about the EventQueue stuff, but it runs an example of an interface.
    I barely knew what an interface was but google told me how to run a JFrame. I know what it does now but I didn't originally.
    I first had to google "How to create a window in java" to even find out there was a class called a JFrame
    Rest assured, I will probably be googling "What is the best library for creating windows in python" (or GO) at some point.

    While some things in this particular file are hard to understand, it is where the program begins. So it is where we start.
    If you dont understand all the details in this file, that is fine. 

    The parts that are important to you right now, I have pointed out.

    EventQueue.invokeLater is one of these hard things. It just runs our windows and thats what matters.
    How some of the things at the end of this file actually work completely are also hard to understand.

    By the end of this file, you should see how an if statement works, 
    you should see some examples of variable definitions, remembering that they have a type
    you should see an example of an array, and referencing the things in it.
    you should see an example of how to use the 'new' keyword to create an instance of a class
    you should see that try{}catch(){} means that if an error occurs, it executes the stuff in the brackets of the catch{}
    you should learn about public and private variables
    you should learn about static variables.
*/

package MySweep;//<-- this class is in the MySweep package, and thus, the files will be in the MySweep folder. It means more stuff but thats what matters right now.

import javax.swing.ImageIcon;
import javax.swing.UIManager;//<-- import the functions used from their libraries.
import java.awt.EventQueue;//(smart editors can do this for you)
import java.awt.Image;
import java.io.FileInputStream;//these libraries, i.e. java.awt are also packages in java
import java.nio.file.Paths;
import java.awt.Frame;

class MineSweeper {//<-- the start of our first class
    /** --a fancy comment-- @param aaaaaarg!!!
     * These are our possible command line arguments (all > 0)
     * @param args [String <o or m>], int width, int height, int BombCount, int lives
     */
    public static void main(String[] args) {// <-- MAIN FUNCTION!! EXECUTION STARTS HERE! return type is void. (it doesnt return anything)
        //^public and static are defined at the end of this file for you to read then.

        //The args array is an array of strings called arguments, that may have been entered into the command line when running the game.

        //you can ignore this thing though....
        try {//(I found out that if you dont do this thing some Swing library stuff breaks on mac)
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());//<-- Set cross-platform Java L&F (also called "Metal") 
        }catch (Exception e) {e.printStackTrace();}//^I copy pasted this straight from the oracle documentation
                                                    //and thats just fine. Now I can set button backgrounds on mac.

        //Focus on how we check the input, using if statements and try.

        int width, height, bombCount, lives;//<-- this is a variable declaration for 4 integers. There is nothing in them yet.
        //^these variables are not global and do not need to be declared public or private.
        // They are contained within the scope of the main function only.

        if(args.length == 4){//<-- processing command line arguments. If we got 4 arguments from the command line, do:

            try{//<-- try means handle errors if they happen. (Language extensions can tell you when you need these)

                //try to parse the arguments (if these fail, it will trigger the catch.)
                width = (int)(Integer.parseInt(args[0]));//<-- checking and saving each item in our args array as an int
                height = (int)(Integer.parseInt(args[1]));//<-- each index corresponds to a string. This is the second index.
                bombCount = (int)(Integer.parseInt(args[2]));//<-- Integer.parseInt(String) converts strings to integers
                lives = (int)(Integer.parseInt(args[3]));//<-- (int) makes sure it is read as an integer. this is called a "cast"

                if(width > 0 && height > 0 && bombCount > 0 && lives > 0){//<-- if you put in 4 positive numbers
                                                                            //then launch main game window with those numbers as input

                    EventQueue.invokeLater(new Runnable(){//<-- invoke later just well... it runs it later. not much later though. (JFrames are initiated this way, and the window classes extend JFrame)
                        public void run(){//notice the "new" though? it is used to call a constructor for a class
                            new MainGameWindow(width, height, bombCount, lives).setVisible(true);//<-- launch main game window with the arguments,
                        }                                                                                   //(now in integer form)
                    });

                }else{//                                                                      <--- else if not correct arguments, 
                    System.out.println("integer arguments only: width, height, BombCount, lives (where all are > 0)");//<-- print an error message
                                                                            //then launch opening window
                    EventQueue.invokeLater(new Runnable(){//<-- this is called an anonymous class/interface/whatever
                        public void run(){    //you cant reference them outside of the fact that that they are input into a function.
                            new OpeningWindow().setVisible(true);//<-- launch opening window
                        }
                    });//<-- see how it is defined inside the () of the function?

                }
            }catch(NumberFormatException e){//<-- if Integer.parseInt() threw an error (the string wasnt a number i.e. 1), then do:
                System.out.println("integer arguments only: width, height, BombCount, lives (where all are > 0)");//<-- print an error message

                EventQueue.invokeLater(new Runnable(){public void run(){new OpeningWindow().setVisible(true);}});//<-- launch opening window
                //    ^you can also write short 1 command interface definitions like this. ^ this is the same thing as the last opening window call
            }

        //-----------------basically the same thing but 5 arguments--(and fewer comments)-------------------------
        }else if(args.length == 5){
            if(args[0].equals("o")||args[0].equals("m")){//<-- aString.equals(anotherString) is how you compare strings.
                try{
                    width = (int)(Integer.parseInt(args[1]));//<-- same as before
                    height = (int)(Integer.parseInt(args[2]));
                    bombCount = (int)(Integer.parseInt(args[3]));
                    lives = (int)(Integer.parseInt(args[4]));
                    if(width > 0 && height > 0 && bombCount > 0 && lives > 0){
                        if(args[0].equals("m")){//<-- check if the first arg was an "m"
                            EventQueue.invokeLater(new Runnable(){public void run(){
                                new MainGameWindow(width, height, bombCount, lives).setVisible(true);
                            }});
                        }
                        if(args[0].equals("o")){//<-- check if the first arg was an "o"
                            EventQueue.invokeLater(new Runnable(){public void run(){
                                new OpeningWindow(args[1],args[2], args[3], args[4]).setVisible(true);//<-- Opening window can take strings
                            }});       //reference items in an array with name[index] where index starts at 0
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

    //the following are public variables and functions (except for one private variable!). 
    //This means if you can reference an instance of this class, you can reference these variables and functions.
    //i.e. you could actually do instanceOfThisClass.publicFunctionFromThisClass(); and have it find and run the function

    //they are also static, which means you dont need to use an instance of a class. They are associated with the class itself.

    //i.e. if(MineSweeper.isJarJile()) doSomething(); 
    //rather than if(instanceOfMineSweeper.isJarFile()) doSomething();

    // the . in instanceOfMineSweeper.isJarFile() would mean it uses the .isJarFile() function from that instance of the MineSweeper class.
    // the . in MineSweeper.isJarJile() means the same thing, but it is a static function, so we dont need an instance. We call it on the class itself.

    //public and private change their scope, (or where they can be seen. Can you see it outside of the class?)
    //static (and others you will meet later) define how they can be referenced.

    //some of these following examples are fairly complex. Dont worry if they are hard right now.
    //But they are examples of public and static variables and functions.


    //public and static variables:
    //you can put in a value just like any variable
    //public static ScoreEntry scoreEntryInstance = new ScoreEntry();
    //^scope  ^modifier(s) ^Type    ^variableName     =  ^'new' followed by a Constructor (of the same type)

    //the following DarkMode variable is the only private global variable in this class. 
    //It can be seen anywhere within the class but not outside. 
    //It still needs to be static so that it can be used by static functions.
    private static boolean DarkMode = true;//<-- we only set this in toggleDarkMode so it is private so we dont forget and mess that up somewhere

    public static boolean isDarkMode(){return DarkMode;}//<-- this is how other classes get darkmode status using MineSweeper.isDarkMode()

    public static void toggleDarkMode() {//<-- the toggle button in InstructionsWindow calls this.
        Frame[] frames = Frame.getFrames();//<-- this is how you get all classes that extend Frame in a program. JFrames extend Frame
        DarkMode = !DarkMode;//<-- toggle our DarkModeVariable
        for (Frame frame : frames) {//<-- a fancy for loop. "for each frame in frames array"
            if(frame instanceof MainGameWindow){//<-- check if it is a specific type so you can cast it as the correct window
                ((MainGameWindow)frame).toggleDarkMode();//<-- cast as correct window, and run the function from that instance of the class.
            }       //right now, the only frame we have defined toggleDarkMode() for is MainGameWindow.
        }
    }

    //these next 2 are images we will use for end of game display, as well as the icon in the top left of the window.

    //we can reference them from anywhere with MineSweeper.MineIcon or MineSweeper.ExplosionIcon
    //for now, notice that they have "public static" before them, which we just explained, 
    //and also a type. In this case, that type is Image, which is a class in Java

    public static Image MineIcon = new ImageIcon(MineSweeper.class.getResource(((isJarFile())?"/src/MySweep/":"") + "Icons/MineSweeperIcon.png")).getImage();
    //recognize how to define variables, and put a new instance of a class in them

    public static Image ExplosionIcon = new ImageIcon(MineSweeper.class.getResource(((isJarFile())?"/src/MySweep/":"") + "Icons/GameOverExplosion.png")).getImage();
    //if you are wondering why type is Image, but the constructor is for an ImageIcon, you are paying excellent attention.
    //I have .getImage() at the end, so the thing being put in the variable is an Image returned by that function.

    //MineSweeper.class.getResource("File/Path") just gets files from the path we ran the program from (we need to check if we are in a jar because running from a jar file would change the path to the icon.)

    public static boolean isJarFile() {//<-- you can see that it returns a boolean, i.e. true or false.
        
        //you need to do this stuff to read 4 bytes from a file. Dont worry. Just pay attention to "public static boolean" right now.
        //apparently .jar files have a magic number that shows if it is a jar file.
        try (FileInputStream fileInputStream = new FileInputStream(Paths.get(System.getProperty("java.class.path")).toFile())) {//<-- start file input stream from where we are running from
            byte[] magicNumber = new byte[4];
            int bytesRead = fileInputStream.read(magicNumber);
            return bytesRead == 4 &&//<-- whitespace doesnt matter in code. These lines are 1 comparison (notice the && and ||), and the result is returned.
                    (magicNumber[0] == 0x50 && magicNumber[1] == 0x4B && magicNumber[2] == 0x03 && magicNumber[3] == 0x04)//wow what a long comparison
                    || (magicNumber[0] == (byte) 0x80 && magicNumber[1] == 0x75 && magicNumber[2] == 0x03 && magicNumber[3] == 0x04);//<-- it ends here
        } catch (Exception e) {
            return false;//<-- if we couldnt read the file at all, just return false
        }        //dont worry about what a magic number is. 
    }      //It is read from a file, and the function returns true when it is there. If the number is there, its a .jar file
}

//Understand public, private, static and type well enough for now? OpeningWindow Time!