IF EXIST "..\Minesweeper.cfg" (move ..\Minesweeper.cfg ..\..\)
jpackage --name Minesweeper --description "Minesweeper for Windows" --input ..\..\app --dest ..\..\ --main-jar minesweeper.jar --icon ..\src\Icons\Minesweeper.ico --add-modules java.desktop --win-dir-chooser --win-menu --win-shortcut --win-shortcut-prompt --win-per-user-install --app-version 1.0 --vendor Birdee
IF EXIST "..\..\Minesweeper.cfg" (move ..\..\Minesweeper.cfg ..\)
