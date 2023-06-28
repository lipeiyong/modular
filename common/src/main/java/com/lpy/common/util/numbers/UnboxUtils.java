package com.lpy.common.util.numbers;

/**
 * @author lipeiyong
 * @date on 2018/7/16 下午1:25
 */
public class UnboxUtils {

    public static int safeUnBox(Integer integer) {
        return safeUnBox(integer, 0);
    }

    public static int safeUnBox(Integer integer, int defaultValue) {
        return integer == null ? defaultValue : integer;
    }

    public static boolean safeUnBox(Boolean bool) {
        return safeUnBox(bool, false);
    }

    public static boolean safeUnBox(Boolean bool, boolean defaultValue) {
        return bool == null ? defaultValue : bool;
    }
}
