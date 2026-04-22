package com.manual.manual.service.impl;

import com.manual.manual.common.ErrorCode;
import com.manual.manual.exception.BusinessException;
import com.manual.manual.service.UploadService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class UploadServiceImpl implements UploadService {

    private static final Set<String> ALLOWED_BIZ = Set.of("product", "user");
    private static final long MAX_UPLOAD_SIZE = 100L * 1024L * 1024L;

    @Override
    public String upload(String biz, MultipartFile file) {
        if (!ALLOWED_BIZ.contains(biz)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Upload biz is invalid");
        }
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Upload file is required");
        }
        if (file.getSize() > MAX_UPLOAD_SIZE) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "上传文件不能超过100M");
        }
        String extension = getSafeExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
        Path uploadDirectory = Paths.get(System.getProperty("user.dir"), "upload", biz);
        try {
            Files.createDirectories(uploadDirectory);
            Files.copy(file.getInputStream(), uploadDirectory.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Upload file failed");
        }
        return "/upload/" + biz + "/" + fileName;
    }

    private String getSafeExtension(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "";
        }
        int index = originalFilename.lastIndexOf('.');
        if (index < 0 || index == originalFilename.length() - 1) {
            return "";
        }
        String extension = originalFilename.substring(index).toLowerCase();
        return extension.length() > 10 ? "" : extension;
    }
}
