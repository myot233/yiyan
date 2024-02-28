package com.example.yiyan.common;

import java.io.*;
import java.net.*;

public class ImageHandler {
    public static void main(String[] args) {
        // 获取 PNG 图片的字节数组（假设 response 是 PNG 格式的图片）
        byte[] imageData = getResponseImageData();

        // 保存 PNG 图片到服务器，并返回 URL
        String imageUrl = saveImageToServer(imageData);

        // 返回图片的 URL
        System.out.println("Image URL: " + imageUrl);
    }

    // 模拟获取 PNG 图片的字节数组
    public static byte[] getResponseImageData() {
        // 假设 response 是 PNG 格式的图片字节数组
        return new byte[10]; // 这里应该替换成实际获取的字节数组
    }

    // 保存 PNG 图片到服务器，并返回 URL
    public static String saveImageToServer(byte[] imageData) {
        String imageUrl = "";
        try {
            // 获取系统文件路径分隔符
            String fileSeparator = System.getProperty("file.separator");

            // 假设保存图片的目录为 "/path/to/images/"
            String directory = "images" + fileSeparator + "route" + fileSeparator;

            // 生成唯一的文件名，确保不会覆盖现有图片
            String fileName = "image_" + System.currentTimeMillis() + ".png";

            // 创建文件输出流
            FileOutputStream fos = new FileOutputStream(directory + fileName);
            fos.write(imageData); // 将图片数据写入文件
            fos.close(); // 关闭文件输出流

            // 构建图片的 URL
            imageUrl = "http://127.0.0.1:8104/images/route/" + fileName; // 替换成你的服务器 URL

        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageUrl;
    }
}
