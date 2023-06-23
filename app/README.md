This folder contains the application source code in packaged installs.

This game comes from https://github.com/BirdeeHub/minesweeper

************************************************************************************

***MINESWEEPER***

Author: Robin DeBoer

There was no minesweeper game on the store that allowed you to actually set the size of the field
or the number of bombs or lives. 
So I made one! 

Mostly I just wanted to learn Java.

**Bonus:**

You can learn too!

You will need a version of Java Development Kit to recompile if you wish to edit. You might need to add it to your path.

Once you properly install a JDK, edit whatever you want in the src folder.

Then you can use the appropriate compile script for your OS to make a new jar.

Doing so will update the actual version of the program you installed. (the binary is just a pointer to the jar and included JRE)

I.e. you can run it using the same icon in your start menu when you are done editing.

It has plenty of comments, and most easy customizations can be changed at the top of the files. (including the directory it saves scores to.)

There is also a package script to make your own installable version using JPackage!

**Quick code walkthrough:**

Main game window is in charge of action listeners and the menu bar and scrolling. 

Grid changes the displays of buttons based on the data in Minefield, and as such contains the main game logic.

Grid also contains 2 private classes defined at top of file, for display purposes.

Minefield contains the timer, and all the things you need to keep track of for game state and contains handy functions for updating and referencing the data. 

On each reset, Grid creates a new minefield, and sets all the buttons back to their default appearances.

Grid also contains scores saving logic at end of file, but the file paths are defined at the top of the file.

scores save to %userprofile%\AppData\Roaming\minesweeperScores for windows

and ~/.minesweeper/ for others.
