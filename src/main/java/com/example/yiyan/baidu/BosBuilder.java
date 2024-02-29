package com.example.yiyan.baidu;

import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.BosClientConfiguration;
import com.baidubce.services.bos.model.CannedAccessControlList;
import com.baidubce.services.bos.model.ObjectMetadata;
import com.baidubce.services.bos.model.PutObjectResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Bos百度云存储
 */
public class BosBuilder {

    /**
     * 简单上传file
     * @param file
     * @return fileUrl
     * @throws IOException
     */
    public static String putObjectSimple(MultipartFile file) throws IOException {

        String ACCESS_KEY_ID = "09ed75f834cc45309cf6c8c2b5ebfd01";             // 用户的Access Key ID
        String SECRET_ACCESS_KEY = "feab12d5103c4a3b9cadad3d60dcaaf7";         // 用户的Secret Access Key
        String ENDPOINT = "fsh.bcebos.com";                                     // 用户自己指定的域名，参考说明文档
        String BUCKETNAME= "carefreedrive";									   // 用户BucketName名称

        // 初始化一个BosClient
        BosClientConfiguration config = new BosClientConfiguration();
        config.setCredentials(new DefaultBceCredentials(ACCESS_KEY_ID, SECRET_ACCESS_KEY));
        config.setEndpoint(ENDPOINT);
        BosClient client = new BosClient(config);

        //获取文件名后缀
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));

        //随机文件ID
        String fileId = getFileIdByTime();

        String key = "route/" + fileId + suffix;
        // 以文件形式上传Object
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        client.putObject(BUCKETNAME, key, file.getInputStream(), metadata);

        // 设置对象权限为公共读
        client.setObjectAcl(BUCKETNAME, key, CannedAccessControlList.PublicRead);

        //返回文件地址
        String fileUrl = "https://" + BUCKETNAME + "." + ENDPOINT + "/" +key;

        // 关闭客户端
        client.shutdown();
        return fileUrl;
    }


    /**
     * 生成文件ID
     * @return fileId
     */
    private static String getFileIdByTime() {
        //生成当前时间戳的ID
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String newDate = sdf.format(new Date());
        //生成5位随机数
        int randomNum = (int) ((Math.random() * 9 + 1) * 10000);
        String result = String.valueOf(randomNum);
        String fileId= newDate + result;
        return fileId;
    }

}