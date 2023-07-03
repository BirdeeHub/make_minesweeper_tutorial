:these will be packaged with the application. If you edit the files in src, and you use this script, it will update the installed game.
:if you mess up your game too much from editing, simply uninstall and reinstall from the installer. Your scores are saved elsewhere.
javac ..\src\MySweep\*.java ..\src\*.java -d ..\minesweeper_jar_in && cd ..\minesweeper_jar_in && jar -cvfe ..\minesweeper.jar MySweep.MineSweeper . ..\src ..\Compiling ..\Packaging ..\README.md && cd ..
del /s /q .\minesweeper_jar_in\*
rmdir /s /q .\minesweeper_jar_in\
del /s /q .\*.class