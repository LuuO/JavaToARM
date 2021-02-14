package javatoarm.java;

import java.util.HashSet;

public class Fibb2 {

    @Deprecated(since="1.1")
    public static int fibonacci(int n) {
        System.out.println("fibo start");
        if (n <= 1) {
            return n;
        } else {
            return fibonacci/**/(n - 1) + fibonacci(n - 2);//

        }
    }

}
