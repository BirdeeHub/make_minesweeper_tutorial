IF EXIST "..\Minesweeper.cfg" (move ..\Minesweeper.cfg ..\..\)
jlink --compress=2 --add-modules java.desktop,java.management,java.compiler,jdk.compiler,jdk.jartool --output ..\..\jre
jpackage --name Minesweeper_2.0 --description "Minesweeper for Windows with compiler and jar tool included" --input ..\..\app --dest ..\..\ --main-jar minesweeper.jar --icon ..\src\MySweep\Icons\Minesweeper.ico --runtime-image ..\..\jre --win-dir-chooser --win-menu --win-shortcut --win-shortcut-prompt --win-per-user-install --app-version 1.0 --vendor Birdee
del /s /q ..\..\jre\
rmdir /s /q ..\..\jre\
IF EXIST "..\..\Minesweeper.cfg" (move ..\..\Minesweeper.cfg ..\)
