package com.example.yiyan.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResourceUtil {

    public static String getResourceAsString(String path) {
        try {

            InputStream inputStream = ResourceUtil.class.getResourceAsStream(path);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toString("UTF-8");
        }catch (Exception exception){
            exception.printStackTrace();
        }
    return "";
    }


}
