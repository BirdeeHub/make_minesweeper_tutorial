IF EXIST "..\Minesweeper_3.0.cfg" (move ..\Minesweeper.cfg ..\..\)
IF EXIST "..\Minesweeper_2.0.cfg" (move ..\Minesweeper.cfg ..\..\)
jlink --compress=2 --add-modules java.desktop,java.management --output ..\..\jre
jpackage --name Minesweeper_3.0 --description "Minesweeper for Windows with Jar-ception" --input ..\..\app --dest ..\..\ --main-jar minesweeper.jar --icon ..\src\MySweep\Icons\Minesweeper.ico --runtime-image ..\..\jre --win-dir-chooser --win-menu --win-shortcut --win-shortcut-prompt --win-per-user-install --app-version 1.0 --vendor Birdee
del /s /q ..\..\jre\
rmdir /s /q ..\..\jre\
IF EXIST "..\..\Minesweeper_3.0.cfg" (move ..\..\Minesweeper.cfg ..\)
IF EXIST "..\..\Minesweeper_2.0.cfg" (move ..\..\Minesweeper.cfg ..\)
