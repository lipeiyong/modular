package com.komlin.libcommon.util.image;

import android.graphics.Bitmap;

/**
 * @author lipeiyong
 * @date 2019/7/15 19:07
 */
public class ImagePixelUtils {

    public static final int ORIENTATION_DOWN = 0x01;
    public static final int ORIENTATION_UPWARD = 0x02;

    /**
     * @param bitmap
     * @return
     */
    public static Bitmap getTransparentBitmap(Bitmap bitmap, int progress) {
        if (bitmap == null) {
            return null;
        }
        int[] argb = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(argb, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        float pre = (100 - progress) / 100f;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int count = (int) (width * height * pre);

        for (int i = 0; i < argb.length; i++) {
            if (i <= count) {
                argb[i] = argb[i] & 0x00FFFFFF;
            }
        }

        bitmap = Bitmap.createBitmap(argb, bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        return bitmap;
    }

    /**
     * @param bitmap
     * @return
     */
    public static Bitmap getTransparentBitmap(Bitmap bitmap, int progress, int orientation) {
        if (bitmap == null) {
            return null;
        }
        int[] argb = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(argb, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float pre;
        int count;
        if (orientation == ORIENTATION_DOWN) {
            pre = progress / 100f;
        } else {
            pre = (100 - progress) / 100f;
        }
        count = (int) (width * height * pre);

        for (int i = 0; i < argb.length; i++) {
            if (orientation == ORIENTATION_DOWN) {
                if (i > count) {
                    argb[i] = argb[i] & 0x00FFFFFF;
                }
            }else{
                if (i <= count) {
                    argb[i] = argb[i] & 0x00FFFFFF;
                }
            }
        }

        bitmap = Bitmap.createBitmap(argb, bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        return bitmap;
    }

}
