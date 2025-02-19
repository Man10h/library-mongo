package com.web.Mongo.service.impl;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.web.Mongo.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Objects;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public Map upload(MultipartFile multipartFile) {
        try{
            File file = convert(multipartFile);
            return cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
        } catch (Exception e) {
            return null;
        }
    }

    private File convert(MultipartFile multipartFile) {
        try{
            File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(multipartFile.getBytes());
            fos.close();
            return file;
        }catch (Exception e){
            return null;
        }
    }
}
