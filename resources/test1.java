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

    public static int fibonacci2(int n) {
        int[] result = new int[n];
        result[0] = 0;
        result[1] = 1;
        for (int i = 2; i <= n; i++) {
            result[i] = result[i - 1] + result[i - 2];
        }
        return result[n];
    }

    public static void main(String[] args) {
       int result1 = fibonacci(30); // 0xcb228
       int result2 = fibonacci2(30); // 0xcb228
    }

}
