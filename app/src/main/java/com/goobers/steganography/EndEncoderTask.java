package com.goobers.steganography;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
public class EndEncoderTask extends AsyncTask<File, Integer, File> {

    @Override
    public File doInBackground(File... params) {
        try {
            byte[] imageBinary = new byte[(int) params[0].length()];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(params[0]));
                buf.read(imageBinary, 0, imageBinary.length);
                buf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] toEncodeBinary = new byte[(int) params[1].length()];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(params[1]));
                buf.read(toEncodeBinary, 0, toEncodeBinary.length);
                buf.close();
            } catch (Exception e) {
                e.printStackTrace();
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

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
    }

}
