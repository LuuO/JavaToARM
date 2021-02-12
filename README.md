# JavaToARM
Welcome!

This project is a compiler that converts Java programs to ARMv7 instructions.

Requires Gradle and Java 15

**How to Use**

Step 1. cd to the project directory

Step 2. Build
```
gradle build
```

Step 3. Compile your .java file
```
java -cp build/classes/java/main/ javatoarm.JavaToARM "path/to/the_file_to_compile.java"
```

A suggested ARMv7 Simulator: https://cpulator.01xz.net/?sys=arm
