package org.jeecg.modules.pengyipeng.utils;

/*
 * ClassName: ImageCompressUtil
 * Package: org.jeecg.modules.pengyipeng.utils
 * Description:
 * @Author: 王绍泽
 * @Create: 2025/9/10 - 16:11
 * @Version: v1.0
 */

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ImageCompressUtil {
    // 1. 下载单张图片到输入流
    private static InputStream downloadImage(String imageUrl, OkHttpClient client) throws IOException {
        Request request = new Request.Builder().url(imageUrl).build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("图片下载失败: " + imageUrl + ", 响应码: " + response.code());
        }
        return response.body().byteStream();
    }

    // 2. 批量图片压缩为ZIP（输出到临时文件）
    public static File compressImagesToZip(List<String> imageUrls, String zipFileName) throws IOException {
        OkHttpClient client = new OkHttpClient();
        File zipFile = File.createTempFile(zipFileName, ".zip"); // 临时ZIP文件
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
             BufferedOutputStream bos = new BufferedOutputStream(zipOut)) {

            for (String url : imageUrls) {
                // 提取图片文件名（如从URL末尾获取）
                String fileName = url.substring(url.lastIndexOf("/") + 1);
                // 防止文件名重复，可加时间戳或UUID
                //fileName = System.currentTimeMillis() + "_" + fileName;

                // 添加ZIP条目 + 写入图片流
                zipOut.putNextEntry(new ZipEntry(fileName));
                try (InputStream imageIn = downloadImage(url, client);
                     BufferedInputStream bis = new BufferedInputStream(imageIn)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = bis.read(buffer)) != -1) {
                        bos.write(buffer, 0, len);
                    }
                    bos.flush();
                }
                zipOut.closeEntry(); // 关闭当前ZIP条目
            }
        } finally {
            client.dispatcher().executorService().shutdown(); // 关闭OkHttp连接池
        }
        return zipFile;
    }
}
