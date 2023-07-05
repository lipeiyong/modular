package com.lpy.socket;

import java.util.Random;

/**
 * 二进制退避技术（Binary Exponential Back off）
 *
 * @author lipeiyong
 * @since 2023/07/05 16:17
 */
public class BinaryExponentialBackHelper {

    private static final int OFFSET = 3;
    private static final int MAX_K = 10;
    private static final int MAX_TIMES = 16;
    private int k;

    public int next() {
        k++;
        if (k > MAX_TIMES) {
            return k = MAX_TIMES;
        }
        int min = min(k, MAX_K);
        int pow = pow(min);
        Random random = new Random();
        int result = random.nextInt(pow);
        return OFFSET + result;
    }

    public void clear() {
        k = 0;
    }

    private int pow(int k) {
        int result = 1;
        for (int i = 0; i < k; i++) {
            result *= 2;
        }
        return result;
    }

    private int min(int a, int b) {
        return a > b ? b : a;
    }
}
