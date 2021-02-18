# JavaToARM
Welcome!

This project is a compiler that converts Java programs to ARMv7 instructions.

Requires Java 15 and Gradle

### How to Use:

Step 1. cd to the project directory

Step 2. Build
```
gradle build
```

Step 3. Run the program to compile your .java file
```
java -cp build/classes/java/main/ javatoarm.JavaToARM "path/to/the_file_to_compile.java" [Memory Size (in MB)]
```

A suggested ARMv7 Simulator: https://cpulator.01xz.net/?sys=arm

### Samples:

Some sample test files included under ``resources/``:
* ``Test1.java`` 
* ``String.java`` from Java Standard Library

### Features:
* Supported data types: ``int``, ``boolean``, and arrays
* Mini malloc
* 
* 
