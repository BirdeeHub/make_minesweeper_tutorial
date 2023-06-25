This folder contains the compile and package scripts in packaged installs. 

Application source code is in src/MySweep folder. 

Icons are in src/Icons folder.

minesweeper_linux_dist.zip creation script, and associated install scripts in Packaging/LinuxInstall.

The application binary runs the .jar in this file using the associated Java Runtime Environment.

all you *need* to run the game is the .jar, and a JRE, as icons are inside the .jar

This game comes from https://github.com/BirdeeHub/minesweeper

************************************************************************************

***MINESWEEPER***

Author: Robin DeBoer

I couldn't easily find a minesweeper game on the store that allowed you to actually set the size of the field
or the number of bombs or lives...

So I made one! 

Mostly I just wanted to learn some Java.

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

You will also need your own JDK if you wish to include modules not included in java.base or java.desktop (you probably wont though, unless maybe you want to mess with how saving works)

-----------------------------------------------------------------------------------------------------------------------------------------

After making sure you have a jdk, edit whatever you want in the src folder.

Then you can use the appropriate compile script for your OS to make a new jar.

Doing so will update the actual version of the program you installed.

I.e. you can run it using the same icon in your start menu when you are done editing.

It has plenty of comments, and most easy customizations can be changed at the top of the files. (including the directory it saves scores to.)

There is also a package script to make your own installable version using JPackage!

_____________________________________________________________________________________________________________________________________________

**Compiling**

If you have a mac, only bashcompile.sh will work for you.

Compiling is easy. Go to Compiling folder. Run the script.

**Packaging (requires jdk)**

Packaging is also easy if you have jpackage and its dependency.

It is not necessary to do this to update your game, it just makes a version you can send to others.

After you compile, go to Packaging folder and run the package script!

If you are on windows, you are done! the installer program is 1 level up from where you are. Just like in the git repo.

**on Linux? just 1 more step.**

cd to Packaging/LinuxInstall, and without moving the .deb file from where the package script put it, 

run the zipLinuxMinesweeper.sh script.

then cd ../../.. and use ls and both the package and a distributable zip will be there for you. Again, just like in the git repo.

____________________________________________________________________________________________________________________________________


**Quick code walkthrough:**

______________________________________________________________________________________________________________________________________________

Main game window is in charge of action listeners and the menu bar and scrolling. And being the window.

Grid is a panel containing the grid of buttons

Grid changes the displays of buttons based on the data in Minefield, and as such contains the main game logic.

Display of buttons also includes doing the actual zoom action. (it grows, then the main game window's scroll pane compensates, and then scrolls to recenter mouse)

Grid also contains 2 private classes defined at top of file, for display purposes.

One of these is to allow for scaling icons for end of game explosions and reveals. Or whatever icons you wish to add.

the other is called CellButton. It was created to allow painting of custom border colors and weights for the buttons on the board, and then it absorbed its non-expensive client properties, such as where on the board it was located, or things that (I think) dont cause heap allocations.

Grid also contains score saving logic at end of file because it is called by gameover function, but the file paths are defined at the top of the file.

--

Minefield contains the timer, and all the things you need to keep track of for game state and contains handy functions for updating and referencing the data.

On each reset, Grid creates a new Minefield instance (which was designed to be as cheap as possible), and sets all the buttons back to their default appearances. The board is not yet initialized. Reset(clickedX,ClickedY) is passed in on first click to fill the cells to ensure safe first click.



scores save to %userprofile%\AppData\Roaming\minesweeperScores for windows and ~/.minesweeper/ for others.

--

MineSweeper.java contains the ever-important main function that launches everything, as well as logic for command line launching straight to game window. 

The rest of the source files are just the other windows.

--

**Random note** I play with scaling to make my terrible explosions look good. Go to Game Over Function near end of grid and do what it says if you want to use a real explosion icon

_______________________________________________________________________________________________________________________________________________

Enjoy the game! Maybe change what you don't like and learn along the way! New colors is the easiest place to start. You can do it!

************************************************************************************************************************************