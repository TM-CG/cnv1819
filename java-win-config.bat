@echo off
rem CNV Configuration of Java Enviroment for Project Hill@Climb
rem -----------------------------------------------------------

rem If you installed JDK 7 on a different folder change this
set JAVA_PATH=C:\Java\jdk1.7.0_80

set JAVA_HOME=%JAVA_PATH%
set JAVA_ROOT=%JAVA_PATH%
set JDK_HOME=%JAVA_PATH%
set JRE_HOME=%JAVA_PATH%\jre
set PATH=%JAVA_PATH%\bin\
set SDK_HOME=%JAVA_PATH%
set _JAVA_OPTIONS="-XX:-UseSplitVerifier"

set CLASSPATH=C:\Users\%USERNAME%\Documents\GitHub\cnv1819;C:\Users\%USERNAME%\Documents\GitHub\cnv1819\BIT\samples;.

rem Open WSL on hyper using the environment variables declared above
C:\Users\%USERNAME%\AppData\Local\hyper\hyper.exe %cd%