package com.goobers.steganography;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class Pic {
    private Pixel[][] pixels;
    private Bitmap buff;
    private String imageName;
    private int count = 0;
    private File file;


    public Pic(String imageName) {
        try {
            file = new File(imageName);
            buff = BitmapFactory.decodeFile(file.getPath());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Silly goose! That's not a valid file name.");
            System.exit(0);
        }
        this.imageName = imageName;
        pixels = new Pixel[buff.getHeight()][buff.getWidth()];

        for (int row = 0; row < pixels.length; row++) {
            for (int col = 0; col < pixels[row].length; col++) {
                int p = buff.getPixel(col, row);
                int a = (p >> 24) & 0xFF;
                int r = (p >> 16) & 0xFF;
                int g = (p >> 8) & 0xFF;
                int b = p & 0xFF;
                pixels[row][col] = new Pixel(r, g, b, a);
            }
        }
    }


    public int getWidth() {
        return pixels[0].length;
    }


    public int getHeight() {
        return pixels.length;
    }


    public Pixel[][] getPixels() {
        return pixels;
    }

    public Pic deepCopy() {
        return new Pic(this.imageName);
    }



    public void save(File file) throws IOException {
        for (int row = 0; row < pixels.length; row++) {
            for (int col = 0; col < pixels[row].length; col++) {
                int convert = convert(pixels[row][col]);
                buff.setPixel(col, row, convert);
            }
        }
        FileOutputStream out = new FileOutputStream(file);
        buff.compress(Bitmap.CompressFormat.PNG, 100, out);
        out.close();
    }

    public File getFile() {
        return file;
    }


    private int convert(Pixel p) {
        return (p.getAlpha() << 24) | (p.getRed() << 16)
             | (p.getGreen() << 8) | p.getBlue();
    }
}
