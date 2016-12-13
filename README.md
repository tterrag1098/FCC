# FCC

## Building

Clone this project and run `gradlew build` (or `./gradlew.sh build`) to build the JAR file.

The result file will be created in build/libs/. Use the `-all` jar if you do not have jasmin downloaded.

## Running

Run this jar like any other, using `java -jar FCC-1.0.0-all.jar` with the arguments specified by the book
(i.e. `java -jar FCC-1.0.0-all.jar compile -ix myprogram.c`). The program will then compile the program and
prompt you to run it. Simply entering `y` will execute the compiled code!
