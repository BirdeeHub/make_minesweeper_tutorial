/*
    Programming is not so complex as you may think. Computers are stupid. They cannot learn english. Their language is much simpler than ours.
    Programs may one day be able to understand english. Currently we have programs that can generate it in response to input.
    But a computer will never understand english. Too stupid. I get zap, I turn switch on. I dont get zap, I turn switch off.
    When I say Computers Know, what I mean is, someone has created a set of instructions we can use to tell the computer what to do
    except, without pulling our hair out trying to make sense of 00010101 00111001 010110101011111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111 Stack Overflow Error

    Because humans want humans to understand the way humans should talk to a computer, it all comes down to just a few concepts. So:

    Commputers know:
    How to store a type of thing, that we can access by a variable name. You must tell it what type of thing.
    A certain number of things you can do on those things. The language you use dictates you what words and operators to use to tell it to do those things.
    You can store a true or false, called a boolean, which is basically a single byte with a couple bytes to identify it as a boolean
    A number of several sizes, i.e. a long is just like an integer (called an int going forward) that is able to store a bigger number than a basic integer can
        or with a decimal point (called a float)
    A single byte (0 or 1)
    How to refer to a specific piece of memory by an address(called a pointer)
    An array of things, which is a pointer to a list of things where you access each item by index starting at 0 (myarray[0]) and can get myarray.length
        (and if you choose an index above the max you crash the program. also you can have arrays of arrays)
    in some languages you must tell it to create a physical place to store things before you tell it what variable name to store the value in.

    You can tell it to read a thing it stored
    You can tell it to perform a thing on a thing you stored, including checking it against the value of another thing.
    You can tell it to do one set of things or another based on checking a comparison between things (the main way to do this is called an if statement)
    You can tell it to do a set of things until a comparison becomes false which is called a loop
        (declared with "for(variable; condition; thing to change on each loop){your stuff here}", or "while(comparison){your stuff here}")
    You can add a comment, like this one, or by using // for 1 line comments

    There is 1 fundamental way of both dictating and grouping what things should be stored and what things should be done on those things.
    It is called a function. A function has a name, and you can call it later. 
    It also returns a new thing to you so that you can do something with the result (as long as you give it a place to put it!)
    Things declared inside functions cannot be seen by things outside of them by default unless you return it. This is called scope. It sees out but not in.
    
    //This is how we declare a function. it returns an int, it is called addTwoIntegers, and it recieves 2 integers
    int addTwoIntegerUnlessAnswerIsThreeOtherwiseMultiply(int a, int b){
        int c;//<-- it then creates a new integer to store the value of a + b
        if(c != 3){//<-- if not equal to 3
            c = a + b;
        }else{//<-- in this case, this would be equivalent to if(c == 3){
            c = a * b;
        }
        return c;//<-- we then return that value
    }

    //and this is how we call it and store the result:
    int result;
    int firstNumber = 1;
    int secondNumber = 2;
    result = addTwoIntegersIfAnswerIsNotThreeOtherwiseMultiply(firstNumber, secondNumber);

    //which would make the value of result be 2.

    to know what operators you have and what they can do (like + for example can append strings or add numbers. 
    / is also worth looking up. It will not work like you think for integers 
    the assign operator is = and is equal to is == and you can use ++ next to an integer to add 1 to it. ! means not on its own also)
    google operators for your language after reading this. Know how these work.
    
    //this while loop will execute 4 times:
    int theAnswer=42;
    int i = 0;
    while(i<12)
        if(i==1){
            i=i+2;
        }else if(i==0){
            i++;
        }else{
            if(theAnswer==42){
                i=i*2;
            }else{
                i==i;
            }
        }
    }
    
    First with i == 0, then i == 1, then 3, then 6 and then when it becomes 12, it will fail the check in the while loop and not execute again.
    Try to read through it like the computer does. It's very good theAnswer == 42 otherwise it would loop forever!

    Theres also a weird thing that is nice sometimes that looks like "int x = (comparison)?numberone:numbertwo;""
    called a ternary that is basically just an if statement that returns the thing that was chosen (which I stored in x in the above example)

    in Java, execution begins at the main(String[] args) function, which takes an array as input, that is filled with whichever words (strings) 
    which may have been passed in from the command line. The main function of this program begins in this file.

    Languages also have other ways of grouping things. C has Structs, for example, which allow you to group together related variables.
    If a language is object oriented, that basically just means it has classes rather than structs.

    Java has classes. Classes allow you to group sets of data and ALSO functions to work on that data, and you create new instances of them to use them. 
    You can create multiple instances of a class just like you create a variable of a certain type, and each one will have its own separate set of data.
    To access the data from a particular one, you create a variable of your new class type, 
    and create a new instance using 'new' and a constructor
        (a constructor is just a function with the name of the class that automatically returns an instance of that class)
    you can then access functions and data inside of it from outside of the class (unless said item is declared as private) by using the dot operator '.'

    If you had a class named MyClass that took a String as input:
    String inputString = "some sort of data";
    MyClass instanceOfMyClass = new MyClass(inputString);

    And if it contained our function addTwoIntegersIfAnswerIsNotThreeOtherwiseMultiply, we could do this:
    String inputString = "some sort of data";
    MyClass instanceOfMyClass = new MyClass(inputString);
    int result;
    int firstNumber = 2;
    int secondNumber = 2;
    result = instanceOfMyClass.addTwoIntegersIfAnswerIsNotThreeOtherwiseMultiply(firstNumber, secondNumber);

    which would make the value of result be 4.

    if our weird add function was declared as static, we wouldnt need to create an instance of MyClass, we could use it directly.

    int firstNumber = 6;
    int secondNumber = -3;
    int result = MyClass.addTwoIntegersIfAnswerIsNotThreeOtherwiseMultiply(firstNumber, secondNumber);

    which would make the value of result be -18, (because (6 + (-3)) == 3 of course!)

    Classes can also extend other classes. This means that they have all the functions the class they extend has. 
    They can change them if they want with @Override
    Implementing is similar but its for something called an interface where you just inherit the developers problems. 
    You must write the functions of an interface you implement and you must write them all. Other functions are relying on you for it!

    An example is, if my class was to extend a JFrame, it would inherit all the functions and properties needed to 
    display as a window and call UI components from the javax.swing library inside of it. 
    All classes in this project named somethingWindow are examples of this.

    If you want to use a library, you must import it.

    There are things called event listeners and hooks and stuff that dynamically call based on user inputs, 
    for example, user clicked on an instance of the JButton class, so do this stuff.

    languages handle errors differently. java uses try{}catch(){}, try(withathing){thing to be tried}catch(exceptionname variable){} and throws exception

    java specific thing:
    Java runs from a java virtual machine thingy that runs your java code so that you can run it anywhere you can install Java.
    You have to first compile it into a .class file for the JVM to be able to read your .java file.

    Knowing the above, plus learning the syntax for doing each of those things, and knowing how to google for what functions in a 
    language would be best to use for a particular thing is honestly all you need to know to begin. 
    When I began writing this, I knew some basic command line scripting, 
    so I was familiar with the concept of commands and how to use both if{}else statements and loops.
    I also knew the above. With that knowledge, I decided to build my first program with a graphical interface.

    An overview of what each of my classes does is in the README in the app folder. 
    There is one major public class per file, each with the same name as the class they contain.
    Most basic customization can be done by changing properties at the tops of the files.
    I have done my best to make sure that if you change 1 thing, it does not confusingly change many other things.
    Poke around! If you break it further than you expected and dont know how to fix it, just uninstall and reinstall with the installer!

    This program was for me to learn to program.
    This program now exists to be minesweeper, but also a beginners coding playground.
    Dont like something? Find the thing, usually at the top of the file, change it, but also check out where it is used and how.
    Then run the compile script provided. 
    Then either run the jar, or, if you are in your game files right now, just run the program! (you wont see your error messages that way though) 
    This program exists for you to understand how information moves around within a program, and how to control what stuff happens when.

    Oh, by the way, if you make your own program with a GUI, 
    choose a library that isn't swing because swing is old, not thread-safe, and not supported by GraalVM
    But dont worry about what any of those things mean exactly just yet while looking through this program. 
    For now, assume doing it with a different library is basically the same just different function and class names. It wouldn't be a lie.

    1 more thing. Recommended reading order:
    Play the game a couple times. Then MineSweeper, then OpeningWindow, 
    then MainGameWindow but dont worry about zoom listener for now unless you want a headache, 
    then Grid up through the end of the constructor except dont worry too much about the scaleable icon stuff,
    then Minefield, then the rest of Grid except the zoom function and random toggle dark mode thing that I dont know why I still have but I do. 
    now read the zoom function on both MainGameWindow and Grid, and the scaleable icon stuff in Grid. 
    Or skip it and go straight to scores and go back to it later.
    Then ScoresFileIO, then ScoreEntry, then ScoresWindow. 
    instructionsWindow is really better viewed in the game.
 */
//This class contains the main function and launches the opening window (or the main game window with optional command line arguments).
//It also contains 2 static references so that I can easily access them from anywhere in the game code. 
//One is to my scores saving class. The other just tells the program if it has been compiled into a jar so that everything still works when you use an IDE to run it
package MySweep;//<-- this class is in the MySweep package, and thus, the classes will be in the /MySweep/ folder in the jar.
import javax.swing.UIManager;//<-- import the libraries used
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.EventQueue;
import java.io.FileInputStream;
import java.nio.file.Paths;
class MineSweeper {
    public static ScoresFileIO scoresFileIO = new ScoresFileIO();//<-- Scores file input output
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
    /**
     * @param args int width, int height, int BombCount, int lives
     */
    public static void main(String[] args) {
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
        int width, height, bombCount, lives;
        if(args.length == 4){
            try{
                width = (int)(Integer.parseInt(args[0]));
                height = (int)(Integer.parseInt(args[1]));
                bombCount = (int)(Integer.parseInt(args[2]));
                lives = (int)(Integer.parseInt(args[3]));
                if(width > 0 && height > 0 && bombCount > 0 && lives > 0){
                    //JFrames work best initiated this way, and these classes extend JFrame, so they are JFrames
                    EventQueue.invokeLater(new Runnable(){public void run(){new MainGameWindow(width, height, bombCount, lives).setVisible(true);}});
                }else{
                    System.out.println("integer arguments only: width, height, BombCount, lives (where all are > 0)");
                    EventQueue.invokeLater(new Runnable(){public void run(){new OpeningWindow().setVisible(true);}});
                }
            }catch(NumberFormatException e){
                System.out.println("integer arguments only: width, height, BombCount, lives (where all are > 0)");
                EventQueue.invokeLater(new Runnable(){//this is called a lambda or anonymous function
                    public void run(){//you cant reference them outside of the fact that that they are now a property of what you added them to.
                        new OpeningWindow().setVisible(true);//you can also write this like this, but i put them on 1 line if its 1 command to save space
                    }
                });
            }
        }else if(args.length == 5){
            if(args[0].equals("o")||args[0].equals("m")){
                try{
                    width = (int)(Integer.parseInt(args[1]));
                    height = (int)(Integer.parseInt(args[2]));
                    bombCount = (int)(Integer.parseInt(args[3]));
                    lives = (int)(Integer.parseInt(args[4]));
                    if(width > 0 && height > 0 && bombCount > 0 && lives > 0){
                        if(args[0].equals("m"))EventQueue.invokeLater(new Runnable(){public void run(){
                            new MainGameWindow(width, height, bombCount, lives).setVisible(true);}});
                        if(args[0].equals("o"))EventQueue.invokeLater(new Runnable(){public void run(){
                            new OpeningWindow(Integer.toString(width),Integer.toString(height), Integer.toString(bombCount), Integer.toString(lives)).setVisible(true);}});
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
