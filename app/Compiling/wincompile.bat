:these will be packaged with the application. If you edit the files in src, and you use this script, it will update the installed game.
:if you mess up your game too much from editing, simply uninstall and reinstall from the installer. Your scores are saved elsewhere.
IF EXIST "..\runtime\bin\javac.exe" (IF EXIST "..\runtime\bin\jar.exe" (
..\..\runtime\javac.exe ..\src\MySweep\*.java -d ..\minesweeper_jar_in && xcopy /E /I ..\src\Icons\ ..\minesweeper_jar_in\ && ..\..\runtime\jar.exe -cvfe ..\minesweeper.jar MySweep.MineSweeper -C ..\minesweeper_jar_in .
del /s /q ..\minesweeper_jar_in\*
rmdir /s /q ..\minesweeper_jar_in\
)) else (
javac ..\src\MySweep\*.java -d ..\minesweeper_jar_in && xcopy /E /I ..\src\Icons\ ..\minesweeper_jar_in\ && jar -cvfe ..\minesweeper.jar MySweep.MineSweeper -C ..\minesweeper_jar_in .
del /s /q ..\minesweeper_jar_in\*
rmdir /s /q ..\minesweeper_jar_in\
)