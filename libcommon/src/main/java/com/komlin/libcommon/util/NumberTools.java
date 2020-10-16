package com.komlin.libcommon.util;

/**
 * @author lipeiyong
 * @date 17-6-9
 */
public class NumberTools {

    public static int tryParseInt(String string) {
        return tryParseInt(string, 0);
    }

    public static int tryParseInt(String string, int defaultValue) {
        try {
            return Integer.parseInt(string);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    public static float tryParseFloat(String string) {
        return tryParseFloat(string, 0);
    }

    public static float tryParseFloat(String string, float defaultValue) {
        try {
            return Float.parseFloat(string);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    /**
     * try find int in string
     *
     * @param str          src string
     * @param defaultValue default value
     * @return int value in string or default value if not find any number
     */
    public static int tryFindInt(String str, int defaultValue) {
        return tryFindInt(str, 0, defaultValue);
    }

    /**
     * try find int in string
     *
     * @param str          src string
     * @param start        start index to find
     * @param defaultValue default value
     * @return int value in string or default value if not find any number
     */
    public static int tryFindInt(String str, int start, int defaultValue) {
        try {
            return Integer.parseInt(tryFindIntString(str, start));
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    /**
     * try find float in string
     *
     * @param str          src string
     * @param defaultValue default value
     * @return float value in string or default value if not find any number
     */
    public static float tryFindFloat(String str, float defaultValue) {
        try {
            return Float.parseFloat(tryFindFloatString(str, 0));
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    /**
     * try float int in string
     *
     * @param str          src string
     * @param start        start index to find
     * @param defaultValue default value
     * @return float value in string or default value if not find any number
     */
    public static float tryFindFloat(String str, int start, float defaultValue) {
        try {
            return Float.parseFloat(tryFindFloatString(str, start));
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    /**
     * Keep one decimal places
     *
     * @param f src
     * @return float value with two decimal places
     */
    public static float splitDecimal1(float f) {
        return ((int) (f * 10)) / 10.0f;
    }

    /**
     * Keep two decimal places
     *
     * @param f src
     * @return float value with two decimal places
     */
    public static float splitDecimal2(float f) {
        return ((int) (f * 100)) / 100.0f;
    }

    /**
     * Keep one decimal places
     *
     * @param f src
     * @return double value with two decimal places
     */
    public static double splitDecimal1(double f) {
        return ((int) (f * 10)) / 10.0;
    }

    /**
     * Keep two decimal places
     *
     * @param f src
     * @return double value with two decimal places
     */
    public static double splitDecimal2(double f) {
        double t1 = f*100;
        int t2 = (int) t1;
        double t3 = t2/100;
        return ((int) (f * 100)) / 100.0;
    }

    /**
     * try get int value int string
     * @param str   string to search
     * @param start start index
     * @return Only numbers string
     * @throws RuntimeException ("Not find any number")
     */
    public static String tryFindIntString(String str, int start) {
        char[] src = str.toCharArray();
        char[] chars = new char[src.length];
        int count = 0;
        char c;
        for (int i = start; i < src.length; i++) {
            c = src[i];
            if ('0' <= c && c <= '9') {
                chars[count] = c;
                count++;
            } else if (c == '-') {
                if (count == 0) {
                    chars[0] = '-';
                    count++;
                } else if (count != 1 || chars[0] != '-') {
                    break;
                }
            } else {
                if (count > 0) {
                    break;
                }
            }
        }
        boolean unLegitimate = (count == 1 && chars[0] == '-') || count == 0;
        if (unLegitimate) {
            throw new RuntimeException("Not find number in [" + str + "] ,start at [" + start + "]");
        } else {
            return new String(chars, 0, count);
        }
    }

    /**
     * try get float value int string
     * @param str   string to search
     * @param start start index
     * @return Only numbers string
     * @throws RuntimeException ("Not find any number")
     */
    public static String tryFindFloatString(String str, int start) {
        char[] src = str.toCharArray();
        char[] chars = new char[src.length];
        int count = 0;
        char c;
        for (int i = start; i < src.length; i++) {
            c = src[i];
            boolean isFloatChar = ('0' <= c && c <= '9') || c == '.';
            if (isFloatChar) {
                chars[count] = c;
                count++;
            } else if (c == '-') {
                if (count == 0) {
                    chars[0] = '-';
                    count++;
                } else if (count != 1 || chars[0] != '-') {
                    break;
                }
            } else {
                if (count > 0) {
                    break;
                }
            }
        }
        boolean unLegitimate = (count == 1 && chars[0] == '-') || count == 0;
        if (unLegitimate) {
            throw new RuntimeException("Not find number in [" + str + "] ,start at [" + start + "]");
        } else {
            return new String(chars, 0, count);
        }
    }
}
