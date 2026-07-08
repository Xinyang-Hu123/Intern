package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 通用接口
 */
@RestController
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @PostMapping("/admin/common/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传：{}", file);

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String objectName = UUID.randomUUID().toString() + extension;

            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(objectName);
            Files.write(filePath, file.getBytes());

            log.info("文件保存到本地：{}", filePath);
            return Result.success(objectName);
        } catch (IOException e) {
            log.error("文件上传失败：{}", e);
        }

        return Result.error(MessageConstant.UPLOAD_FAILED);
    }

    @GetMapping("/common/download")
    @ApiOperation("文件下载")
    public void download(@RequestParam("name") String name, HttpServletResponse response) {
        log.info("文件下载：{}", name);

        String fileName = name;
        if (name.contains("/")) {
            fileName = name.substring(name.lastIndexOf("/") + 1);
        }

        try {
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            if (!Files.exists(filePath)) {
                ClassPathResource resource = new ClassPathResource("static/" + fileName);
                if (resource.exists()) {
                    serveFile(resource.getInputStream(), fileName, response);
                    return;
                }
                response.setStatus(404);
                return;
            }

            serveFile(Files.newInputStream(filePath), fileName, response);
        } catch (IOException e) {
            log.error("文件下载失败：{}", e);
            response.setStatus(500);
        }
    }

    private void serveFile(InputStream inputStream, String fileName, HttpServletResponse response) throws IOException {
        String contentType = "image/png";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            contentType = MediaType.IMAGE_JPEG_VALUE;
        }
        response.setContentType(contentType);

        OutputStream outputStream = response.getOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();
    }

}
