#!/bin/bash
javac ../src/*.java -d ../minesweeper_classes && jar --create -v --file=../jar/minesweeper.jar --main-class=MineSweeper -C ../minesweeper_classes .