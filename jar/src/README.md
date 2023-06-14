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

Don't like something? the code is here. edit the code in src folder, and use the compile script for your os.

Doing so will update the actual version of the program you installed. 

I.e. you can run it using the same icon in your start menu when you are done editing.

It has plenty of comments, and most easy customizations can be changed at the top of the files. (including the directory it saves scores to.)

*Quick walkthrough:*

Main game window is in charge of action listeners and the menu bar and scrolling. 

Grid changes the displays of buttons based on the data in Minefield, and as such contains the main game logic.

Minefield contains the timer, and all the things you need to keep track of for game state and contains handy functions for updating and referencing the data. 

On each reset, Grid creates a new minefield, and sets all the buttons back to their default appearances.

**HOW TO Install:** 

**(windows)**

************************************************************************************

go to WindowsInstaller directory, and click the exe in that folder.

This will launch an installer.

game installs to %userprofile%\AppData\Local\Minesweeper

This readme is in that folder though so if you are reading this you know that.

scores save to %userprofile%\AppData\Roaming\minesweeperScores

(Also, it will give you a windows defender warning because I didnt pay for it to be a signed installer.
If that worries you, you probably havent downloaded it yet and should look at the rest of the git repo. 
the source code is there and so are commands to compile it yourself)

**MAC AND LINUX USERS:**

************************************************************************************

**If you didnt clone the repo and are on debian linux:**

move to a writeable directory and run the following command (requires wget):
```bash
wget -O minesweeper_linux_dist.zip https://github.com/BirdeeHub/minesweeper/raw/main/linux_or_mac_pkg/linux_dist.zip && \
unzip minesweeper_linux_dist.zip -d minesweeper_linux_dist && \
sudo ./minesweeper_linux_dist/installdebianpackage.sh && \
[ ! -d ~/.minesweeper/ ] && mkdir ~/.minesweeper; \
mv ./minesweeper_linux_dist/uninstalldebianpackage.sh ~/.minesweeper/minesweeper_uninstall.sh && \
rm ./minesweeper_linux_dist.zip
##the last 3 of these lines are optional though
```

Game will be saved to /usr/local/games 
and scores will be saved to the ~/.minesweeper folder the above script created, 
and a copy of package and the install script will 
appear in a folder in the current directory named minesweeper_linux_dist

and then to uninstall if installed this way:

```bash
sudo ~/.minesweeper/minesweeper_uninstall.sh && rm ~/.minesweeper/minesweeper_uninstall.sh
```

