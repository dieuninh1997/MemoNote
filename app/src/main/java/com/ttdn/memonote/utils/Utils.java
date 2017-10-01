package com.ttdn.memonote.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by ttdn1 on 10/1/2017.
 */

public class Utils {
    public static Bitmap getResizedBitmap(String filepath, int reqWidth, int reqHeight) {
        Bitmap bitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filepath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(filepath, options);
        return bitmap;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
}
