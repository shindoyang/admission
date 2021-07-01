package com.ut.user.support;

import com.ut.user.feign.FeignFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadService {

    @Autowired
    private FeignFile feignFile;

    public void uploadFile(MultipartFile file, String appKey){
        feignFile.upload1(file, appKey);
    }

}
