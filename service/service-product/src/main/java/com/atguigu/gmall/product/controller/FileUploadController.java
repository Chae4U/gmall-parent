package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.util.Result;
import lombok.SneakyThrows;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("admin/product/")
@CrossOrigin
public class FileUploadController {

    @SneakyThrows
    @RequestMapping("fileUpload")
    public Result<String> fileUpload(@RequestParam("file") MultipartFile file) throws Exception {

        String imgUrl = "http://192.168.134.129:";

        String path = FileUploadController.class.getClassLoader().getResource("tracker.conf").getPath();

        System.out.println("path= " +path);

        // 初始化fdfs的全局配置
        // 读取配置文件中的配置信息
        ClientGlobal.init(path);

        // 获得一个tracker链接
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer connection = trackerClient.getConnection();

        // 获取一个storage
        StorageClient storageClient = new StorageClient(connection,null);

        // 上传文件
        String[] urls = storageClient.upload_file(file.getBytes(), StringUtils.getFilenameExtension(file.getOriginalFilename()), null);

        System.out.println("urls= " +urls);

        for (String url : urls) {
            System.out.println("url= " +url);
            imgUrl = imgUrl + "/" + url;
        }

        System.out.println("imgUrl= " +imgUrl);
        return Result.ok(imgUrl);
    }
}
