```

If you have come looking for answers from the tutorial experience, theyre in Grid class, in the same spot.

```

This folder contains the compile and package scripts along with:

Application source code is in src/MySweep folder (other than OverwriteJar.java which is in src/)

Icons are in src/MySweep/Icons folder.

The application binary runs the .jar in this file using the associated Java Runtime Environment.

all you *need* to run the game is the .jar, and a JRE, as icons are inside the .jar

This game comes from https://github.com/BirdeeHub/minesweeper

************************************************************************************

***MINESWEEPER***

I couldn't easily find a minesweeper game on the store that allowed you to actually set the size of the field
or the number of bombs or lives...

So I made one! 

Mostly I just wanted to learn some Java.

**Attention:** This version is kinda dumb. Its also kinda cool. It exists as a proof of concept and because I wanted to try.

Why is it so dumb? It saves by writing a new jar containing updated scores, and then overwriting itself on exit. 
This means I can't make a linux package because all locations linux could install it to with dpkg are not writeable, 
so linux needs to use the jar for this one.
It also means I need a native java executable in the windows package until I learn to mess with a system class loader, so the package installer is an extra 10 MB.

**Attention:** Is it better? No it's worse. Force quitting might mean you have to then open and close the game again to move the jar file from the temp directory over the main one. But its also significantly cooler in my opinion. Also I used swing so I can't easily use GraalVM to make a true native version so I settled on making it interesting.

**Attention:** Don't install this version unless you are curious.

*************************************************************************************************************************

**Bonus:**

You can learn too!

If you didnt download the version with a compiler, you will need a version of Java Development Kit to recompile if you wish to edit. 

```powershell
##go to
https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html

##or if you have Chocolatey package manager for windows:
choco install oracle17jdk
```

for linux, search for JDK in your package manager, e.g. sudo apt search jdk or: sudo apt search default-jdk

then install 17 if available, otherwise whatever you can find.

you will need 16+ for JPackage to be easy.

both openjdk and oracle jdk are fine.

You might need to add it to your path.

If you wish to make your own installer, you will still need a JDK regardless of if your game version includes compiler.

You will also need your own JDK if you wish to include modules not included in java.base or java.desktop or java.management (you probably wont though)

-----------------------------------------------------------------------------------------------------------------------------------------

After making sure you have a jdk, edit whatever you want in the src folder.

Then you can use the appropriate compile script for your OS to make a new jar.

Doing so will update the actual version of the program you installed. (assuming you used the windows package installer)

I.e. you can run it using the same icon in your start menu when you are done editing.

It has plenty of comments, and most easy customizations can be changed at the top of the files. (including the directory it saves scores to.)

There is also a package script to make your own installable version using JPackage! (windows only for this version)

**I only have the .jar**

The entire repo app folder is actually included in the jar, so if all you have is that and a JDK, you can run jar xvf path/to/minesweeper.jar

It will need to be in a folder named app in order for the scripts that package your own installer to work.

You can remove the META-INF/ directory and MyClass/*.class and OverwriteJar.class files that appear in the main directory you extracted to if you wish because they are generated when you compile. It won't break anything if you don't they just wont ever be used again. 

in bash, completing all these 3 steps would look like: mkdir app && cd app && jar xvf ../minesweeper.jar && rm -r META-INF MySweep OverwriteJar.class

_____________________________________________________________________________________________________________________________________________

**Compiling**

If you have a mac, only bashcompile.sh will work for you.

Compiling is easy. Go to Compiling folder. Run the script.

**Packaging (requires Windows for this version)**

Packaging is also easy if you have jpackage and its dependency. For windows this is WIX, for debian linux, dpkg

It is not necessary to do this to update your game, it just makes a version you can send to others.

After you compile, go to Packaging folder and run the package script!

You are done! the installer program is 1 level up from where you are. Just like in the git repo.

____________________________________________________________________________________________________________________________________

**Quick code walkthrough:**

______________________________________________________________________________________________________________________________________________

Main game window is in charge of action listeners and the menu bar and scrolling. And being the window. It contains an instance of the Grid class

Grid is a panel containing the grid of buttons for the board.

Grid changes the displays of buttons based on the data in Minefield, and as such contains the main game logic.

Display of buttons also includes doing the actual zoom action. (it grows, then the main game window's scroll pane compensates, and then scrolls to recenter mouse)

Grid also contains 2 private classes defined at top of file, for display purposes.

One of these is to allow for scaling icons for end of game explosions and reveals. Or whatever icons you wish to add.

the other is called CellButton. It was created to allow painting of custom border colors and weights for the buttons on the board, and then it absorbed its non-expensive client properties, such as where on the board it was located, or things that (I think) dont cause heap allocations.

--

Minefield contains the timer, and all the things you need to keep track of for game state and contains handy functions for updating and referencing the data.

On each reset, Grid creates a new Minefield instance (which was designed to be as cheap as possible), and sets all the buttons back to their default appearances. The board is not yet initialized. Reset(clickedX,ClickedY) is passed in on first click to fill the cells to ensure safe first click.

--

ScoresFileIO contains functions directly pertaining to reading and writing to the scores file. 

ScoresFileIO turns the scores file into ScoreEntry instances, and can update or delete them.

scores save to %userprofile%\AppData\Roaming\minesweeperScores for windows and ~/.minesweeper/ for others.

ScoresWindow displays ScoreEntry instances correctly and allows for deletion, and relaunching a new opening window with a pre populated score.

--

MineSweeper.java contains the ever-important main function that launches everything, as well as logic for command line launching straight to game window.

MineSweeper.java also contains the hook to copy OverwriteJar.class to a temp directory and run it upon exiting.

OverwriteJar.java extracts the contents of the original jar to memory, replaces the scores file with the scores file in the temp directory, and then overwrites the old jar with the new contents. If successful, it then cleans up the extra scores file in the temp directory and then deletes itself.

The rest of the source files are just the other windows.

_______________________________________________________________________________________________________________________________________________

Enjoy the game! Maybe change what you don't like and learn along the way! New colors is the easiest place to start. You can do it!

************************************************************************************************************************************