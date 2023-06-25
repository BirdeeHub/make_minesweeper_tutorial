#!/bin/bash
[ -f "../minesweeper.cfg" ] && mv ../minesweeper.cfg ../../
InstallDir="/usr/local/games"
jlink --compress=2 --add-modules java.desktop,java.compiler,jdk.jartool --output ../../jre
jpackage --name minesweeper --description "Minesweeper for Linux with compiler and jar tool included" --input ../../app --dest ../../ --main-jar minesweeper.jar --icon ../src/Icons/MineSweeperIcon.png --runtime-image ../../jre --app-version 2.0 --install-dir $InstallDir
[ -f "../../minesweeper.cfg" ] && mv ../../minesweeper.cfg ../
[ -d "../../jre" ] && echo "requesting sudo to easily remove ../../jre now that packaging is done as some of it is write protected and requires many prompts. No need to run the entire script with sudo because then that would make the .deb also write protected unnecessarily" && \
sudo rm -r ../../jre