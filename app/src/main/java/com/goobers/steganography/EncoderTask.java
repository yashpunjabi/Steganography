package com.goobers.steganography;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;


public class EncoderTask extends AsyncTask<File, Integer, File> {

    private static final String LOG_TAG = EncoderTask.class.getSimpleName();

    private Context context;
    public static final int OVERHEAD_SIZE = 32;
    private int pixelRow;
    private int pixelCol;

    public EncoderTask(Context context) {
        this.context = context;
    }

    @Override
    protected File doInBackground(File... params) {
        pixelRow = 0;
        pixelCol = 0;
        try {
            params[0] = FileUtils.convert(params[0], context.getCacheDir().getPath());
            Bitmap buffer = BitmapFactory.decodeFile(params[0].getPath()).copy(Bitmap.Config
                    .ARGB_8888, true);
            byte[] byteArray = new byte[(int) params[1].length()];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(params[1]));
                buf.read(byteArray, 0, byteArray.length);
                buf.close();
            } catch (Exception e) {
                Log.e(LOG_TAG, "exception", e);
            }
            Log.v(LOG_TAG, "read into byte array");

            int numBitsPossible = ((buffer.getHeight() * buffer.getWidth()) * 3);
            if (numBitsPossible < ((byteArray.length * 8) + OVERHEAD_SIZE)) {
                return endEncode(params);
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
            Log.v(LOG_TAG, "encoded overhead");

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
                if (i % 1024 == 0) {
                    publishProgress((int) ((((double) bitCount) / ((double) (byteArray.length * 8))) * 100));
                }
            }
            Log.v(LOG_TAG, "encoded image");

            FileOutputStream out = new FileOutputStream(params[2]);
            buffer.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (OutOfMemoryError e) {
            throw new OutOfMemoryError("Not Enough RAM");
        } catch (Exception e) {
            Log.e(LOG_TAG, "exception", e);
        }
        Log.v(LOG_TAG, "done encoding");
        return params[2];
    }

    @Override
    protected void onProgressUpdate(Integer... values) {

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(File file) {
        Intent intent = new Intent(this.context, ImageActivity.class);
        intent.putExtra(EncodeActivity.EXTRA_FILE_TAG, file.getPath());
        context.startActivity(intent);
    }

    private void incrementPixel(int length) {
        pixelCol++;
        if (pixelCol == length) {
            pixelCol = 0;
            pixelRow++;
        }
    }

    private File endEncode(File... params) {
        try {
            byte[] imageBinary = new byte[(int) params[0].length()];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(params[0]));
                buf.read(imageBinary, 0, imageBinary.length);
                buf.close();
            } catch (Exception e) {
                Log.e(LOG_TAG, "exception", e);
            }

            byte[] toEncodeBinary = new byte[(int) params[1].length()];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(params[1]));
                buf.read(toEncodeBinary, 0, toEncodeBinary.length);
                buf.close();
            } catch (Exception e) {
                Log.e(LOG_TAG, "exception", e);
            }

            byte[] bytes = new byte[imageBinary.length + toEncodeBinary.length + 4];
            byte[] length = ByteBuffer.allocate(4).putInt(toEncodeBinary.length).array();
            int count = 0;
            for (byte element: imageBinary) {
                bytes[count] = element;
                count++;
            }
            for (byte element: length) {
                bytes[count] = element;
                count++;
            }
            for (byte element: toEncodeBinary) {
                bytes[count] = element;
                count++;
            }
            FileOutputStream out = new FileOutputStream(params[2]);
            out.write(bytes);
            out.flush();
            out.close();
        } catch (OutOfMemoryError e) {
            throw new OutOfMemoryError("Not Enough RAM");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params[2];
    }

}
