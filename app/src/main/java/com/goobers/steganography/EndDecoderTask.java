package com.goobers.steganography;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

/**
 * Created by ian on 10/25/15.
 */
public class EndDecoderTask extends AsyncTask<File, Integer, File> {

    @Override
    public File doInBackground(File... params) throws OutOfMemoryError{
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
            //return (new DecoderTask()).execute(params[0], params[1]).get();
        }
        return params[1];
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
