@ECHO ON

cd "C:\Users\ehaacls\OneDrive - The University of Texas at Dallas\CS 6378 ( Advanced Operating Systems )\Projects\Ricart Agrawala Mutex"
javac -cp ".\bin" -d ".\bin" .\src\*.java

FOR /L %%A IN (0,1,2) DO (
  ECHO %%A
  start "%%A" cmd.exe /k "java -cp ".\bin" InvokeMain %%A"
)
cmd /k