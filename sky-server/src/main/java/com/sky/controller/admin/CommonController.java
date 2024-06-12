package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api(tags = "通用接口")
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        //获得原始文件名
        String originalFilename = file.getOriginalFilename();
        //创建新的文件名
        String newName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
        //返回地址
        try {
            return Result.success(aliOssUtil.upload(file.getBytes(), newName));
        } catch (IOException e) {
            log.error(e.getMessage());
            return Result.error(MessageConstant.UPLOAD_FAILED + e.getMessage());
        }
    }
}
