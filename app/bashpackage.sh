#!/bin/bash
[ -f "./minesweeper.cfg" ] && mv ./minesweeper.cfg ../
InstallDir="/usr/local/games"
jpackage --name minesweeper --description "Minesweeper for Linux" --input ../app --dest ../ --main-jar minesweeper.jar --icon PackageIcons/MineSweeperIcon.png --add-modules java.desktop,java.logging,java.xml  --install-dir $InstallDir
[ -f "../minesweeper.cfg" ] && mv ../minesweeper.cfg .