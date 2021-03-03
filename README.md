# JavaToARM

Welcome!

This project is a compiler that converts Java programs to ARMv7 instructions.

Requires Java 15 and Gradle

### How to Use:

Step 1. clone this repository

Step 2. cd to the project directory

Step 3. Build
```
gradle build
```

Step 4. Run the program to compile your .java file
```
java -cp build/classes/java/main/ javatoarm.JavaToARM "path/to/the_file_to_compile.java" [Memory Size (in MB) (Optional)]
```

Suggested ARMv7 Simulator: <https://cpulator.01xz.net/?sys=arm>

### Samples:

Some sample test files included under ``resources/``:
* ``Test1.java`` 
* ``String.java`` from Java Standard Library

### Features:

####Front-end:

* Support most Java syntax (Everything in ``String.java``)

####Back-end:

* Supported data types: ``int``, ``boolean``, and arrays
* Support most procedure pro
* Mini malloc
* 
* 
