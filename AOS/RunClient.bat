@ECHO ON

cd "C:\Users\ehaacls\OneDrive - The University of Texas at Dallas\CS 6380 ( Distributed Computing )\DistributedSystems_Assignment\DistributedSystems_Assignment1"
javac -cp ".\bin" -d ".\bin" .\src\*.java

FOR /L %%A IN (0,1,3) DO (
  ECHO %%A
  start "%%A" cmd.exe /k "java -cp ".\bin" InvokeMain %%A"
)
cmd /k