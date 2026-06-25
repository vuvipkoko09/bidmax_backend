package com.example.daugiaonline.application.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.daugiaonline.application.service.FileUploadService;
import com.example.daugiaonline.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("folder", "daugiaonline"));
            return uploadResult.get("url").toString();
        } catch (IOException e) {
            throw new AppException("Lỗi tải ảnh lên hệ thống");
        }
    }
}
