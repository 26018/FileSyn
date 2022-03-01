package com.moon.demo.Util;

import com.moon.demo.entity.Message;

import java.io.*;

/**
 * @author JinHui
 * @date 2022/2/11
 */

public class Utils {

    public static Message fileToMessage(String filePath) throws IOException {
        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(file);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 1024];
        while (fileInputStream.read(buffer) != -1) {
            byteArrayOutputStream.write(buffer);
        }
        return new Message(file.getName(), byteArrayOutputStream.toByteArray(), file.length());
    }

    public static boolean messageToFile(Message message, String basePath) throws IOException {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(basePath + message.fileName);
            fileOutputStream.write(message.object);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
