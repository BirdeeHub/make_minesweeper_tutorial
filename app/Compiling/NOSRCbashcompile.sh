#!/usr/bin/env bash
##once compiled, this jar will not contain scripts, Minesweeper.ico file, or source code.
[ -f ../src/MySweep/Icons/Minesweeper.ico ] && mv ../src/MySweep/Icons/Minesweeper.ico .
javac ../src/MySweep/*.java ../src/*.java -d ../minesweeper_jar_in && cd ../minesweeper_jar_in/ && \
jar --create -v --file=../minesweeper_tiny.jar --main-class=MySweep.MineSweeper  . ../src/MySweep/Icons ../src/MySweep/save  && \
cd .. && rm -r ./minesweeper_jar_in/ && rm ./*.class
[ -f ./Compiling/Minesweeper.ico ] && mv ./Compiling/Minesweeper.ico ./src/MySweep/Icons/Minesweeper.ico
