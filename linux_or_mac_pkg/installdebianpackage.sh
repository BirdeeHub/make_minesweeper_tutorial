#!/bin/bash
InstallDir="/usr/local/games"
LaunchDir="/usr/local/bin"
SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
sudo dpkg -i $SCRIPT_DIR/minesweeper_1.0-1_amd64.deb
sudo echo "#\!/bin/bash" > $LaunchDir/minesweeper && \
sudo echo "bash -c $InstallDir/minesweeper/bin/minesweeper >/dev/null 2>&1 < /dev/null &" > $LaunchDir/minesweeper && \
sudo chmod +x $LaunchDir/minesweeper