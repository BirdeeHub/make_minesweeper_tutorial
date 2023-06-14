javac ..\src\*.java -d ..\minesweeper_classes && jar -cvfe ..\jar\minesweeper.jar MineSweeper -C ..\minesweeper_classes .
xcopy ..\src\*.java ..\jar\src\
jpackage --name Minesweeper --description "Minesweeper for Windows" --input ..\jar --dest ..\WindowsInstaller\ --main-jar minesweeper.jar --icon .\MineSweeperIcon.ico --add-modules java.desktop,java.logging,java.xml --win-dir-chooser --win-menu --win-shortcut --win-shortcut-prompt --win-per-user-install --app-version 1.0 --vendor Birdee
del ..\jar\src\*.java