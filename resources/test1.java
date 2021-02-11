package javatoarm.java;

import java.util.HashSet;

public class FIBB {

    public static int fibonacci(int n) {
        // System.out.println("fibo start");
        if (n <= 1) {
            return n;
        } else {
            return fibonacci(n - 1) + fibonacci(n - 2);
        }
    }

    public static void main(String[] args) {
       int result = fibonacci(30); // 0xcb228
    }

}
