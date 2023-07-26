:once compiled, this jar will not contain scripts, Minesweeper.ico file, or source code.
IF EXIST "..\src\MySweep\Icons\Minesweeper.ico" (move ..\src\MySweep\Icons\Minesweeper.ico .)
IF EXIST "..\runtime\bin\javac.exe" (IF EXIST "..\runtime\bin\jar.exe" (
..\..\runtime\javac.exe ..\src\MySweep\*.java ..\src\*.java -d ..\minesweeper_jar_in && cd ..\minesweeper_jar_in && ..\..\runtime\jar.exe -cvfe ..\..\JarWithNOSRCIncluded\minesweeper.jar MySweep.MineSweeper . ..\src\MySweep\Icons ..\src\MySweep\save && cd ..
del /s /q .\minesweeper_jar_in\*
rmdir /s /q .\minesweeper_jar_in\
)) else (
javac ..\src\MySweep\*.java ..\src\*.java -d ..\minesweeper_jar_in && cd ..\minesweeper_jar_in && jar -cvfe ..\..\JarWithNOSRCIncluded\minesweeper.jar MySweep.MineSweeper . ..\src\MySweep\Icons ..\src\MySweep\save && cd ..
del /s /q .\minesweeper_jar_in\*
rmdir /s /q .\minesweeper_jar_in\
)
IF EXIST ".\Compiling\Minesweeper.ico" (move .\Compiling\Minesweeper.ico .\src\MySweep\Icons\Minesweeper.ico)