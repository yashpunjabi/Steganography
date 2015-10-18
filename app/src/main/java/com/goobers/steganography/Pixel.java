package com.goobers.steganography;

public class Pixel {
    private int red, green, blue, alpha;


    public Pixel(int red, int green, int blue, int alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }


    public int getRed() {
        return red;
    }


    public int getGreen() {
        return green;
    }


    public int getBlue() {
        return blue;
    }


    public int getAlpha() {
        return alpha;
    }


    public void setRed(int red) {
        if (isValid(red)) {
            this.red = red;
        }
    }


    public void setGreen(int green) {
        if (isValid(green)) {
            this.green = green;
        }
    }


    public void setBlue(int blue) {
        if (isValid(blue)) {
            this.blue = blue;
        }
    }


    public void setAlpha(int alpha) {
        if (isValid(alpha)) {
            this.alpha = alpha;
        }
    }

    private boolean isValid(int num) {
        return num < 256 && num > -1;
    }
}