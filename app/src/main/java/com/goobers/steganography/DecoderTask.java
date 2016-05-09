package com.goobers.steganography;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

/**
 * Created by yash on 10/23/15.
 */
public class DecoderTask extends AsyncTask<File, Integer, File> {

    private static final String LOG_TAG = DecoderTask.class.getSimpleName();

    private Context context;
    private ProgressBar progressBar;
    private int pixelRow;
    private int pixelCol;


    public DecoderTask(Context context, ProgressBar progressBar) {
        this.context = context;
        this.progressBar = progressBar;
    }

    @Override
    protected File doInBackground(File... params) {
        byte[] bytes;
        try {
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(params[1]));
            bytes = new byte[(int) params[0].length()];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(params[0]));
                buf.read(bytes, 0, bytes.length);
                buf.close();
            } catch (Exception e) {
                Log.e(LOG_TAG, "exception", e);
            }
            Log.v(LOG_TAG, "read into byte array");

            int count = 8;
            long size = ByteBuffer.wrap(bytes).getInt(8);
            count += size + 12;
            while(ByteBuffer.wrap(bytes).getInt(count + 4) != 0x49454E44) {
                count += ByteBuffer.wrap(bytes).getInt(count) + 12;
            }
            count += 12;
            size = ByteBuffer.wrap(bytes).getLong(count);
            count += 4;
            for (int i = 0; i < size; i++) {
                out.write(bytes[count]);
                count++;
                if (i % 1024 == 0) {
                    publishProgress((int)((((double) i) / ((double) (size))) * 100));
                }
            }


            out.flush();
            out.close();
            Log.v(LOG_TAG, "done decoding");
        } catch (OutOfMemoryError e) {
            throw new OutOfMemoryError("Not Enough RAM");
        } catch (Exception e) {
            return decode(params);
        }
        return params[1];
    }

    private File decode(File... params) {
        Log.v(LOG_TAG, "decoding using steg method");

        pixelCol = 0;
        pixelRow = 0;
        try {
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(params[1]));
            Bitmap buffer = BitmapFactory.decodeFile(params[0].getPath()).copy(Bitmap.Config
                    .ARGB_8888, true);
            long numBytes = 0x0;

            for (int i = 0; i < 64; i++) {
                if (i % 3 == 0) {
                    if ((Color.red(buffer.getPixel(pixelCol, pixelRow)) & 0x1) == 0x1) {
                        numBytes = numBytes | (0x1 << (63 - i));
                    }
                } else if (i % 3 == 1) {
                    if ((Color.blue(buffer.getPixel(pixelCol, pixelRow)) & 0x1) == 0x1) {
                        numBytes = numBytes | (0x1 << (63 - i));
                    }
                } else {
                    if ((Color.green(buffer.getPixel(pixelCol, pixelRow)) & 0x1) == 0x1) {
                        numBytes = numBytes | (0x1 << (63 - i));
                    }
                    incrementPixel(buffer.getWidth());
                }
            }
            Log.v(LOG_TAG, "got number of bytes from overhead");

            incrementPixel(buffer.getWidth());
            int bitcount = 0;
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
                out.write(current);
                if (i % 1024 == 0) {
                    publishProgress((int)((((double) bitcount) / ((double) (numBytes *  8))) * 100));
                }
            }

            out.flush();
            out.close();
            Log.v(LOG_TAG, "finished decoding");

        } catch (Exception e)  {
            Log.e(LOG_TAG, "exception" ,e);
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
        progressBar.setProgress(values[0]);
    }

    private void incrementPixel(int length) {
        pixelCol++;
        if (pixelCol == length) {
            pixelCol = 0;
            pixelRow++;
        }
    }
}
