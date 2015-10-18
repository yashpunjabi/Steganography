package com.goobers.steganography;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;


public class Encoder {

    public static File encode(File base, File secret, File encoded) {
        Pic image = new Pic(base.getPath());
        try {


            byte[] byteArray = new byte[(int) secret.length()];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(secret));
                buf.read(byteArray, 0, byteArray.length);
                buf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Pixel[][] pix = image.getPixels();
            int numBitsPossible = ((pix.length * pix[0].length) * 3);
            StringBuilder bitString = new StringBuilder();
            for (byte element: byteArray) {
                bitString.append(String.format("%8s", Integer.toBinaryString(element & 0xFF)).replace(' ', '0'));
            }

            ArrayList<Pixel> pixels = new ArrayList<Pixel>();
            if (numBitsPossible < (bitString.length() + 32)) {
                return EndEncoder.encode(base, secret, encoded);
            }
            for (int i = 0; i < pix.length; i++) {
                for (int j = 0; j < pix[0].length; j++) {
                    pix[i][j].setRed(pix[i][j].getRed() | 0x1);
                    pix[i][j].setBlue(pix[i][j].getBlue() | 0x1);
                    pix[i][j].setGreen(pix[i][j].getGreen() | 0x1);
                    pixels.add(pix[i][j]);
                }
            }

            byte[] length = ByteBuffer.allocate(4).putInt(byteArray.length).array();
            StringBuilder numString = new StringBuilder();
            for (byte element: length) {
                numString.append(String.format("%8s", Integer.toBinaryString(element & 0xFF)).replace(' ', '0'));
            }
            int pixelCount = 0;
            for (int i = 0; i < numString.length(); i++) {
                if (i % 3 == 0) {
                    if (numString.charAt(i) == '0') {
                        pixels.get(pixelCount).setRed(pixels.get(pixelCount).getRed() & 0xFE);
                    }
                } else if (i % 3 == 1) {
                    if (numString.charAt(i) == '0') {
                        pixels.get(pixelCount).setBlue(pixels.get(pixelCount).getBlue() & 0xFE);
                    }
                } else {
                    if (numString.charAt(i) == '0') {
                        pixels.get(pixelCount).setGreen(pixels.get(pixelCount).getGreen() & 0xFE);
                    }
                    pixelCount++;
                }

            }
            pixelCount++;


            for (int i = 0; i < bitString.length(); i++) {
                if (i % 3 == 0) {
                    if (bitString.charAt(i) == '0') {
                        pixels.get(pixelCount).setRed(pixels.get(pixelCount).getRed() & 0xFE);
                    }
                } else if (i % 3 == 1) {
                    if (bitString.charAt(i) == '0') {
                        pixels.get(pixelCount).setBlue(pixels.get(pixelCount).getBlue() & 0xFE);
                    }
                } else {
                    if (bitString.charAt(i) == '0') {
                        pixels.get(pixelCount).setGreen(pixels.get(pixelCount).getGreen() & 0xFE);
                    }
                    pixelCount++;
                }

            }
            image.save(encoded);
            System.out.println(" " + (byteArray.length + 4) + " bytes hidden");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encoded;
    }

    public static File decode(File image, File decoded) {
        Pic toDecode = new Pic(image.getPath());
        try {
            ArrayList<Byte> bytes = new ArrayList<Byte>();
            Pixel[][] pix = toDecode.getPixels();
            ArrayList<Pixel> pixels = new ArrayList<Pixel>();

            for (Pixel[] array: pix) {
                for (Pixel element: array) {
                    pixels.add(element);
                }
            }

            int numBytes = 0x0;
            int pixelCount = 0;
            for (int i = 0; i < 32; i++) {
                if (i % 3 == 0) {
                    if ((pixels.get(pixelCount).getRed() & 0x1) == 0x1) {
                        numBytes = numBytes | (0x1 << (31 - i));
                    }
                } else if (i % 3 == 1) {
                    if ((pixels.get(pixelCount).getBlue() & 0x1) == 0x1) {
                        numBytes = numBytes | (0x1 << (31 - i));
                    }
                } else {
                    if ((pixels.get(pixelCount).getGreen() & 0x1) == 0x1) {
                        numBytes = numBytes | (0x1 << (31 - i));
                    }
                    pixelCount++;
                }
            }
            pixelCount++;


            StringBuilder byteString = new StringBuilder();
            for (int i = 0; i < numBytes * 8; i++) {
                if (i % 3 == 0) {
                    if ((pixels.get(pixelCount).getRed() & 0x1) == 0x1) {
                        byteString.append('1');
                    } else {
                        byteString.append('0');
                    }
                } else if (i % 3 == 1) {
                    if ((pixels.get(pixelCount).getBlue() & 0x1) == 0x1) {
                        byteString.append('1');
                    } else {
                        byteString.append('0');
                    }
                } else {
                    if ((pixels.get(pixelCount).getGreen() & 0x1) == 0x1) {
                        byteString.append('1');
                    } else {
                        byteString.append('0');
                    }
                    pixelCount++;
                }
            }

            for (int i = 0; i < byteString.length() / 8; i++) {
                String subString = byteString.substring(i * 8, (i * 8) + 8);
                int info = 0x0;
                for (int j = 0; j < subString.length(); j++) {
                    if (subString.charAt(j) == '1') {
                        info = info | (0x1 << (7 - j));
                    }
                }
                byte[] byteInfo = ByteBuffer.allocate(4).putInt(info).array();
                bytes.add(byteInfo[3]);
            }


            byte[] byteArray = new byte[bytes.size()];
            for (int i = 0; i < byteArray.length; i++) {
                byteArray[i] = bytes.get(i);
            }
            System.out.println(" " + (numBytes + 4) + " bytes found");
            FileOutputStream out = new FileOutputStream(decoded);
            out.write(byteArray);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decoded;
    }

}
