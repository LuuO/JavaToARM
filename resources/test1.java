package javatoarm.java;

import java.util.HashSet;

public class FIBB {

    public static int fibonacci(int n) {
        // System.out.println("fibo start");
        if (n <= 1) {
            return n;
        }
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

}
