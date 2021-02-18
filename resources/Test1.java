package javatoarm.javaast;

import java.util.HashSet;

public class Test1 {

    public static int fibonacci(int n) {
        if (n <= 1) {//
            return n;
        } else {
            return fibonacci/**/(n - 1) + fibonacci(n - 2);//
        }
    }

    /**
     * htjhy
     *
     * @param args
     */
    public static void main(String[] args) {
        int R1 = false;

        int[] arr = newArray(546, 78765);
        int n = numberOfElementGreaterThan(arr, 78765 - 546 + 1, 49999);
        boolean arrayPass = n == 78765 - 49999;

        boolean majorityPass = !majority(false, false, false)
                && !majority(false, false, true)
                && !majority(false, true, false)
                && majority(false, true, true)
                && !majority(true, false, false)
                && majority(true, false, true)
                && majority(true, true, false)
                && majority(true, true, true);

        boolean fibonacciPass = fibonacci(30) == 0xcb228 && fibonacci2(30) == 0xcb228;

        int complexExpressionResult = complexExpression(3, 16, 2);

        if (arrayPass && majorityPass && fibonacciPass && complexExpressionResult == -119700) {
            R1 = true;
        }
        // R1 will be set to 1 if success
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

    public static boolean majority(boolean a, boolean b, boolean c) {
        if (!(a == false)) {
            return b || c;
        } else if (!b == false) {
            return a || c || false;
        } else if ((c == true) || (a == !a)) {
            if (b) return true;
            if (a) return true;
            return false;
        }
        return 23534 + 4 == 3;
    }

    public static int complexExpression(int a, int b, int c) {
        return ((554 * a + 32) - (((-a + 42) * (86 - c)) + (b - c))) * (c + 73);
    }

    public static int numberOfElementGreaterThan(int[] array, int length, int threshold) {
        int count = 0; // R3
        int i = 0; // R4
        while (i < length) {
            if (array[i] > threshold) {
                count++;
            }
            i++;
        }
        return count;
    }

    /**
     * Requires end > start
     * inclusive
     * @param start
     * @param end
     * @return
     */
    public static int[] newArray(int start, int end) {
        int[] a = new int[end - start + 1]; // R2
        int i = 0; // R3
        do {
            a[i++] = start++;
        } while (start <= end);
        return a;
    }
}
