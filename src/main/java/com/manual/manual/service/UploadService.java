package com.manual.manual.service;

import org.springframework.web.multipart.MultipartFile;

public interface UploadService {

    String upload(String biz, MultipartFile file);
}