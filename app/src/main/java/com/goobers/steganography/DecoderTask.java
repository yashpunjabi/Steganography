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

/**
 * Created by yash on 10/23/15.
 */
public class DecoderTask extends AsyncTask<File, Integer, File> {

    private Context context;
    private int pixelRow;
    private int pixelCol;


    public DecoderTask(Context context) {
        this.context = context;
    }

    @Override
    protected File doInBackground(File... params) {
        byte[] bytes;
        try {

            bytes = new byte[(int) params[0].length()];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(params[0]));
                buf.read(bytes, 0, bytes.length);
                buf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            int count = 8;
            int size = ByteBuffer.wrap(bytes).getInt(8);
            count += size + 12;
            while(ByteBuffer.wrap(bytes).getInt(count + 4) != 0x49454E44) {
                count += ByteBuffer.wrap(bytes).getInt(count) + 12;
            }
            count += 12;
            size = ByteBuffer.wrap(bytes).getInt(count);
            byte[] decodedBytes = new byte[size];
            count += 4;
            for (int i = 0; i < size; i++) {
                decodedBytes[i] = bytes[count];
                count++;
            }
            FileOutputStream out = new FileOutputStream(params[1]);
            out.write(decodedBytes);
            out.flush();
            out.close();
        } catch (OutOfMemoryError e) {
            throw new OutOfMemoryError("Not Enough RAM");
        } catch (Exception e) {
            return decode(params);
        }
        return params[1];
    }

    private File decode(File... params) {
        pixelCol = 0;
        pixelRow = 0;
        try {
            Bitmap buffer = BitmapFactory.decodeFile(params[0].getPath()).copy(Bitmap.Config
                    .ARGB_8888, true);
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
                    publishProgress((int)((((double) bitcount) / ((double) (byteArray.length * 8))) * 100));
                }
                byteArray[i] = (byte) current;
            }

            FileOutputStream out = new FileOutputStream(params[1]);
            out.write(byteArray);
            out.flush();
            out.close();
        } catch (Exception e) {
            Log.wtf("Goober", e.getMessage());
        }
        return params[1];
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

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    private void incrementPixel(int length) {
        pixelCol++;
        if (pixelCol == length) {
            pixelCol = 0;
            pixelRow++;
        }
    }
}
