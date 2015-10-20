package com.goobers.steganography;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
public class EndEncoder {

    public static File encode(File image, File toEncode, File encoded) {
        try {
            byte[] imageBinary = new byte[(int) image.length()];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(image));
                buf.read(imageBinary, 0, imageBinary.length);
                buf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] toEncodeBinary = new byte[(int) toEncode.length()];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(toEncode));
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
            FileOutputStream out = new FileOutputStream(encoded);
            out.write(bytes);
            out.flush();
            out.close();
            System.out.println(" " + toEncodeBinary.length + " bytes hidden");
        } catch (OutOfMemoryError e) {
          throw new OutOfMemoryError("Not Enough RAM");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encoded;
    }
    public static File decode(File file, File decoded) throws OutOfMemoryError{
        byte[] bytes;
        try {

            bytes = new byte[(int) file.length()];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
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
            FileOutputStream out = new FileOutputStream(decoded);
            out.write(decodedBytes);
            out.flush();
            out.close();
            System.out.println(" " + size + " bytes found");
        } catch (OutOfMemoryError e) {
            throw new OutOfMemoryError("Not Enough RAM");
        } catch (Exception e) {
            return Encoder.decode(file, decoded);
        }
        return decoded;
    }
}
