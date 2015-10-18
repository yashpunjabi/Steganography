package com.goobers.steganography;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;

public class PNGConverter {

    public static File convert(File file, String fileDir) {
        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
        File temp = new File(fileDir, "conversion.png");
        try {
            FileOutputStream out = new FileOutputStream(temp);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }
}
