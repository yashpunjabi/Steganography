package com.goobers.steganography;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;


public class Encoder {
    public static final int OVERHEAD_SIZE = 32;
    private static int pixelRow;
    private static int pixelCol;
    public static File encode(File base, File secret, File encoded) throws OutOfMemoryError{
        pixelRow = 0;
        pixelCol = 0;
        try {
            Bitmap buffer = BitmapFactory.decodeFile(base.getPath()).copy(Bitmap.Config.ARGB_8888, true);
            byte[] byteArray = new byte[(int) secret.length()];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(secret));
                buf.read(byteArray, 0, byteArray.length);
                buf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            int numBitsPossible = ((buffer.getHeight() * buffer.getWidth()) * 3);
            if (numBitsPossible < ((byteArray.length * 8) + OVERHEAD_SIZE)) {
                return EndEncoder.encode(base, secret, encoded);
            }
            byte[] overhead = ByteBuffer.allocate(4).putInt(byteArray.length).array();

            int bitCount = 0;
            for (int i = 0; i < overhead.length; i++) {
                byte currentByte = overhead[i];
                for (int j = 7; j >= 0; j--) {
                    int bit = (currentByte & (0x1 << j)) >> j;
                    bit = bit & 0x1;
                    if (bitCount % 3 == 0) {
                        int red;
                        if (bit == 0) {
                            red = Color.red(buffer.getPixel(pixelCol, pixelRow)) & 0xFE;
                        } else {
                            red = Color.red(buffer.getPixel(pixelCol, pixelRow)) | 0x1;
                        }
                        buffer.setPixel(pixelCol, pixelRow, Color.argb(
                                Color.alpha(buffer.getPixel(pixelCol, pixelRow)), red,
                                Color.green(buffer.getPixel(pixelCol, pixelRow)),
                                Color.blue(buffer.getPixel(pixelCol, pixelRow))));
                    } else if (bitCount % 3 == 1) {
                        int blue;
                        if (bit == 0) {
                            blue = Color.blue(buffer.getPixel(pixelCol, pixelRow)) & 0xFE;
                        } else {
                            blue = Color.blue(buffer.getPixel(pixelCol, pixelRow)) | 0x1;
                        }
                        buffer.setPixel(pixelCol, pixelRow, Color.argb(
                                Color.alpha(buffer.getPixel(pixelCol, pixelRow)),
                                Color.red(buffer.getPixel(pixelCol, pixelRow)),
                                Color.green(buffer.getPixel(pixelCol, pixelRow)), blue));
                    } else {
                        int green;
                        if (bit == 0) {
                            green = Color.green(buffer.getPixel(pixelCol, pixelRow)) & 0xFE;
                        } else {
                            green = Color.green(buffer.getPixel(pixelCol, pixelRow)) | 0x1;
                        }
                        buffer.setPixel(pixelCol, pixelRow, Color.argb(
                                Color.alpha(buffer.getPixel(pixelCol, pixelRow)),
                                Color.red(buffer.getPixel(pixelCol, pixelRow)), green,
                                Color.blue(buffer.getPixel(pixelCol, pixelRow))));
                        incrementPixel(buffer.getWidth());
                    }
                    bitCount++;
                }
            }
            incrementPixel(buffer.getWidth());


            bitCount = 0;
            for (int i = 0; i < byteArray.length; i++) {
                byte currentByte = byteArray[i];
                for (int j = 7; j >= 0; j--) {
                    int bit = (currentByte & (0x1 << j)) >> j;
                    bit = bit & 0x1;
                    if (bitCount % 3 == 0) {
                        int red;
                        if (bit == 0) {
                            red = Color.red(buffer.getPixel(pixelCol, pixelRow)) & 0xFE;
                        } else {
                            red = Color.red(buffer.getPixel(pixelCol, pixelRow)) | 0x1;
                        }
                        buffer.setPixel(pixelCol, pixelRow, Color.argb(
                                Color.alpha(buffer.getPixel(pixelCol, pixelRow)), red,
                                Color.green(buffer.getPixel(pixelCol, pixelRow)),
                                Color.blue(buffer.getPixel(pixelCol, pixelRow))));
                    } else if (bitCount % 3 == 1) {
                        int blue;
                        if (bit == 0) {
                            blue = Color.blue(buffer.getPixel(pixelCol, pixelRow)) & 0xFE;
                        } else {
                            blue = Color.blue(buffer.getPixel(pixelCol, pixelRow)) | 0x1;
                        }
                        buffer.setPixel(pixelCol, pixelRow, Color.argb(
                                Color.alpha(buffer.getPixel(pixelCol, pixelRow)),
                                Color.red(buffer.getPixel(pixelCol, pixelRow)),
                                Color.green(buffer.getPixel(pixelCol, pixelRow)), blue));
                    } else {
                        int green;
                        if (bit == 0) {
                            green = Color.green(buffer.getPixel(pixelCol, pixelRow)) & 0xFE;
                        } else {
                            green = Color.green(buffer.getPixel(pixelCol, pixelRow)) | 0x1;
                        }
                        buffer.setPixel(pixelCol, pixelRow, Color.argb(
                                Color.alpha(buffer.getPixel(pixelCol, pixelRow)),
                                Color.red(buffer.getPixel(pixelCol, pixelRow)), green,
                                Color.blue(buffer.getPixel(pixelCol, pixelRow))));
                        incrementPixel(buffer.getWidth());
                    }
                    bitCount++;
                }
            }
            FileOutputStream out = new FileOutputStream(encoded);
            buffer.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (OutOfMemoryError e) {
            throw new OutOfMemoryError("Not Enough RAM");
        } catch (Exception e) {
            Log.wtf("Goober", e.getMessage());
        }
        return encoded;
    }
    private static void incrementPixel(int length) {
        pixelCol++;
        if (pixelCol == length) {
            pixelCol = 0;
            pixelRow++;
        }
    }

    public static File decode(File image, File decoded) throws OutOfMemoryError{
        pixelCol = 0;
        pixelRow = 0;
        try {
            Bitmap buffer = BitmapFactory.decodeFile(image.getPath()).copy(Bitmap.Config.ARGB_8888, true);
            int numBytes = 0x0;

            for (int i = 0; i < 32; i++) {
                if (i % 3 == 0) {
                    if ((Color.red(buffer.getPixel(pixelCol, pixelRow)) & 0x1) == 0x1) {
                        numBytes = numBytes | (0x1 << (31 - i));
                    }
                } else if (i % 3 == 1) {
                    if ((Color.blue(buffer.getPixel(pixelCol, pixelRow)) & 0x1) == 0x1) {
                        numBytes = numBytes | (0x1 << (31 - i));
                    }
                } else {
                    if ((Color.green(buffer.getPixel(pixelCol, pixelRow)) & 0x1) == 0x1) {
                        numBytes = numBytes | (0x1 << (31 - i));
                    }
                    incrementPixel(buffer.getWidth());
                }
            }

            incrementPixel(buffer.getWidth());
            int bitcount = 0;
            byte[] byteArray = new byte[numBytes];
            for (int i = 0; i < numBytes; i++) {
                int current = 0;
                for (int j = 7; j >= 0; j--) {
                    if (bitcount % 3 == 0) {
                        if ((Color.red(buffer.getPixel(pixelCol, pixelRow)) & 0x1) == 0x1) {
                            current = current | (0x1 << j);
                        }
                    } else if (bitcount % 3 == 1) {
                        if ((Color.blue(buffer.getPixel(pixelCol, pixelRow)) & 0x1) == 0x1) {
                            current = current | (0x1 << j);
                        }
                    } else {
                        if ((Color.green(buffer.getPixel(pixelCol, pixelRow)) & 0x1) == 0x1) {
                            current = current | (0x1 << j);
                        }
                        incrementPixel(buffer.getWidth());
                    }
                    bitcount++;
                }
                byteArray[i] = (byte) current;
            }

            FileOutputStream out = new FileOutputStream(decoded);
            out.write(byteArray);
            out.flush();
            out.close();
        } catch (OutOfMemoryError e) {
            throw new OutOfMemoryError("Not Enough RAM");
        } catch (Exception e) {
            Log.wtf("Goober", e.getMessage());
        }
        return decoded;
    }

}
