package com.erp.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.erp.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${oss.access-key-id}")
    private String accessKeyId;

    @Value("${oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${oss.bucket}")
    private String bucket;

    @Value("${oss.endpoint}")
    private String endpoint;

    @Value("${oss.base-url}")
    private String baseUrl;

    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        String ext = "";
        String originalName = file.getOriginalFilename();
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
        }
        String date = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        String objectKey = "product/" + date + "/" + UUID.randomUUID().toString().replace("-", "") + ext;

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            ossClient.putObject(bucket, objectKey, file.getInputStream());
        } finally {
            ossClient.shutdown();
        }

        String url = baseUrl.replaceAll("/$", "") + "/" + objectKey;
        return Result.success(url);
    }
}
