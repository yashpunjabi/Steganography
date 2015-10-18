//package com.goobers.steganography;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.nio.ByteBuffer;
//import java.nio.file.Files;
//public class EndEncoder {
//
//    public static File encode(File image, File toEncode, File encoded) {
//        try {
//            byte[] imageBinary = Files.readAllBytes(image.toPath());
//            byte[] toEncodeBinary = Files.readAllBytes(toEncode.toPath());
//            byte[] bytes = new byte[imageBinary.length + toEncodeBinary.length + 4];
//            byte[] length = ByteBuffer.allocate(4).putInt(toEncodeBinary.length).array();
//            int count = 0;
//            for (byte element: imageBinary) {
//                bytes[count] = element;
//                count++;
//            }
//            for (byte element: length) {
//                bytes[count] = element;
//                count++;
//            }
//            for (byte element: toEncodeBinary) {
//                bytes[count] = element;
//                count++;
//            }
//            FileOutputStream out = new FileOutputStream(encoded);
//            out.write(bytes);
//            out.flush();
//            out.close();
//            System.out.println(" " + toEncodeBinary.length + " bytes hidden");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return encoded;
//    }
//    public static File decode(File file, File decoded) {
//        byte[] bytes;
//        try {
//            bytes = Files.readAllBytes(file.toPath());
//            int count = 8;
//            int size = ByteBuffer.wrap(bytes).getInt(8);
//            count += size + 12;
//            while(ByteBuffer.wrap(bytes).getInt(count + 4) != 0x49454E44) {
//                count += ByteBuffer.wrap(bytes).getInt(count) + 12;
//            }
//            count += 12;
//            size = ByteBuffer.wrap(bytes).getInt(count);
//            byte[] decodedBytes = new byte[size];
//            count += 4;
//            for (int i = 0; i < size; i++) {
//                decodedBytes[i] = bytes[count];
//                count++;
//            }
//            FileOutputStream out = new FileOutputStream(decoded);
//            out.write(decodedBytes);
//            out.flush();
//            out.close();
//            System.out.println(" " + size + " bytes found");
//        } catch (Exception e) {
//            return Encoder.decode(file, decoded);
//        }
//        return decoded;
//    }
//}
