IF EXIST "..\Minesweeper.cfg" (move ..\Minesweeper.cfg ..\..\)
jlink --compress=2 --add-modules java.desktop,java.compiler,jdk.jartool --output ..\..\jre
jpackage --name Minesweeper --description "Minesweeper for Windows with compiler and jar tool included" --input ..\..\app --dest ..\..\ --main-jar minesweeper.jar --icon ..\src\Icons\Minesweeper.ico --runtime-image ..\..\jre --win-dir-chooser --win-menu --win-shortcut --win-shortcut-prompt --win-per-user-install --app-version 2.0 --vendor Birdee
del /s /q ..\..\jre\
rmdir /s /q ..\..\jre\
IF EXIST "..\..\Minesweeper.cfg" (move ..\..\Minesweeper.cfg ..\)
