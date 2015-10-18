//package com.goobers.steganography;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import javax.imageio.ImageIO;
//import java.io.IOException;
//
///**
// * This class wraps around a picture using our custom Pixel class.
// *
// * @author Aaron Friesen
// * @version 2.0
// */
//public class Pic {
//    private Pixel[][] pixels;
//    private BufferedImage buff;
//    private String imageName;
//    private int count = 0;
//    private File file;
//
//    /**
//     * Constructor for Pic. Sets up the backing Pixel array.
//     *
//     * @param imageName The name of the image you want to load, as a String.
//     * Includes file type.
//     */
//    public Pic(String imageName) {
//        try {
//            file = new File(imageName);
//            buff = ImageIO.read(file);
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.err.println("Silly goose! That's not a valid file name.");
//            System.exit(0);
//        }
//        this.imageName = imageName;
//        pixels = new Pixel[buff.getHeight()][buff.getWidth()];
//
//        for (int row = 0; row < pixels.length; row++) {
//            for (int col = 0; col < pixels[row].length; col++) {
//                int p = buff.getRGB(col, row);
//                int a = (p >> 24) & 0xFF;
//                int r = (p >> 16) & 0xFF;
//                int g = (p >> 8) & 0xFF;
//                int b = p & 0xFF;
//                pixels[row][col] = new Pixel(r, g, b, a);
//            }
//        }
//    }
//
//    /**
//     * Gets the width of the picture you set up in the constructor.
//     *
//     * @return The width of the picture.
//     */
//    public int getWidth() {
//        return pixels[0].length;
//    }
//
//    /**
//     * Gets the height of the picture you set up in the constructor.
//     *
//     * @return The height of the picture.
//     */
//    public int getHeight() {
//        return pixels.length;
//    }
//
//    /**
//     * Returns the backing Pixel array.
//     *
//     * Note that any changes you make to the Pixel array are reflected
//     * in the picture!
//     *
//     * @return The pixel array for this Pic.
//     */
//    public Pixel[][] getPixels() {
//        return pixels;
//    }
//
//    /**
//     * Returns a deep copy of the image. Useful when you want to
//     * maintain a "clean" copy.
//     *
//     * @return The copy of the picture as a new Pic object.
//     */
//    public Pic deepCopy() {
//        return new Pic(this.imageName);
//    }
//
//
//
//    public void save(File file) throws IOException {
//        for (int row = 0; row < pixels.length; row++) {
//            for (int col = 0; col < pixels[row].length; col++) {
//                int convert = convert(pixels[row][col]);
//                buff.setRGB(col, row, convert);
//            }
//        }
//        ImageIO.write(buff, "png", file);
//    }
//
//    public File getFile() {
//        return file;
//    }
//
//    /**
//     * This is deep bitshifting magic. Also don't worry about this.
//     *
//     * @param p The pixel to convert back into int mode
//     * @return The converted ARGB int.
//     */
//    private int convert(Pixel p) {
//        return (p.getAlpha() << 24) | (p.getRed() << 16)
//             | (p.getGreen() << 8) | p.getBlue();
//    }
//}
