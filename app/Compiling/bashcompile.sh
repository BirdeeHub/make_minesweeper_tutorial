#!/bin/bash
##these will be packaged with the application. If you edit the files in src, and you use this script, it will update the installed game.
##if you mess up your game too much from editing, simply uninstall and reinstall from the installer. Your scores are saved elsewhere.
if [ -f ../runtime/bin/javac ] && [ -f ../runtime/bin/jar ]
then
../../runtime/bin/javac ../src/MySweep/*.java -d ../minesweeper_jar_in && cp ../src/Icons/* ../minesweeper_jar_in/ && \
../../runtime/bin/jar --create -v --file=../minesweeper.jar --main-class=MySweep.MineSweeper -C ../minesweeper_jar_in . && \
rm -r ../minesweeper_jar_in/
else
javac ../src/MySweep/*.java -d ../minesweeper_jar_in && cp ../src/Icons/* ../minesweeper_jar_in/ && \
jar --create -v --file=../minesweeper.jar --main-class=MySweep.MineSweeper -C ../minesweeper_jar_in . && \
rm -r ../minesweeper_jar_in/
fi