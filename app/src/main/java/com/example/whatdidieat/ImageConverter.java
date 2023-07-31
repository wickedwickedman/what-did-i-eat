package com.example.whatdidieat;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class ImageConverter {

    // Bitmap 용량 축소
    public Bitmap resize(Context context, Bitmap bitmap) {
        int quality = 3;
        double coef = 0.75;
//        if (bitmap.getWidth() > 2048 && bitmap.getHeight():q > 2048) {
//            quality = 1;
//        } else if (bitmap.getWidth() > 1024 && bitmap.getHeight() > 1024) {
//            quality = 2;
//        } else {
//            quality = 3;
//        }
        Configuration config = context.getResources().getConfiguration();
        Log.d("tag1", Integer.toString(config.smallestScreenWidthDp));
        if (config.smallestScreenWidthDp >= 800)
            bitmap = Bitmap.createScaledBitmap(bitmap, 400 * quality, (int) (400 * coef * quality), true);
        else if (config.smallestScreenWidthDp >= 600)
            bitmap = Bitmap.createScaledBitmap(bitmap, 300 * quality, (int) (300 * coef * quality), true);
        else if (config.smallestScreenWidthDp >= 400)
            bitmap = Bitmap.createScaledBitmap(bitmap, 200 * quality, (int) (200 * coef * quality), true);
        else if (config.smallestScreenWidthDp >= 360)
            bitmap = Bitmap.createScaledBitmap(bitmap, 180 * quality, (int) (180 * coef * quality), true);
        else
            bitmap = Bitmap.createScaledBitmap(bitmap, 160 * quality, (int) (160 * coef * quality), true);
        Log.d("tag1", "After : w " + bitmap.getWidth() + " h " + bitmap.getHeight());
        Log.d("tag1", ""+bitmap.getAllocationByteCount());
        return bitmap;
    }

    public byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] byteArray = stream.toByteArray();
        String temp = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return temp;
    }

    public Bitmap stringToBitmap(String image) {
        byte[] byteArray = Base64.decode(image, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        return bitmap;
    }

    public Bitmap byteArrayToBitmap(byte[] b) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        return bitmap;
    }

    public String byteArrayToString(byte[] b) {
        String s = new String(b);
        return s;
    }
}
