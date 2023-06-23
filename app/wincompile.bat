:these will be packaged with the application. If you edit the files in src, and you use this script, it will update the installed game.
:the exe jpackage uses more or less just gives it an icon and the necessary pointers to run the included jar with the included jre.
:if you mess up your game too much from editing, simply uninstall and reinstall from the installer. Your scores are saved elsewhere.
javac .\src\*.java -d minesweeper_classes && xcopy /E /I .\src\ButtonIcons\ minesweeper_classes\ && jar -cvfe minesweeper.jar MineSweeper -C minesweeper_classes .
del /s /q .\minesweeper_classes\*
rmdir /s /q .\minesweeper_classes\