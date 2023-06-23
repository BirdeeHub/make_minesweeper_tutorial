#!/bin/bash
##these will be packaged with the application. If you edit the files in src, and you use this script, it will update the installed game.
##the binary jpackage uses in bin folder more or less just gives it an icon and the necessary pointers to run the included jar with the included jre.
##if you mess up your game too much from editing, simply uninstall and reinstall from the installer. Your scores are saved elsewhere.
javac ./src/*.java -d minesweeper_classes && cp ./src/ButtonIcons/* ./minesweeper_classes/ && \
jar --create -v --file=minesweeper.jar --main-class=MineSweeper -C minesweeper_classes . && \
rm -r ./minesweeper_classes/