#!/bin/bash
javac ../src/*.java -d minesweeper_classes && jar --create -v --file=../jar/minesweeper.jar --main-class=MineSweeper -C minesweeper_classes .
cp ../src/*.java ../jar/src/
rm -r ./minesweeper_classes/
InstallDir="/usr/local/games"
jpackage --name minesweeper --description "Minesweeper for Debian" --input ../jar --dest . --main-jar minesweeper.jar --icon ./MineSweeperIcon.png --add-modules java.desktop,java.logging,java.xml --install-dir $InstallDir
rm ../jar/src/*.java