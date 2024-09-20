package com.example.chat.chat.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@SuppressWarnings("rawtypes")
public class CloudinaryController {

    private final CloudinaryService cloudinaryService;

    public CloudinaryController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    /**************************************************************************************************************/

    /**************************************************************************************************************/
    @PostMapping("/upload/{folder}")
    public Map upload(@PathVariable("folder") String folder, MultipartFile file) throws IOException {
        return cloudinaryService.upload(file, folder);
    }

    /**************************************************************************************************************/
    @DeleteMapping("/delete-by-folder/{folder}")
    public Map deleteByTag(@PathVariable("folder") String folder) throws Exception {
        return cloudinaryService.deleteByFolder("chat/" + folder);
    }

    /**************************************************************************************************************/

    @DeleteMapping("/delete-image")
    public Map delete(String id) throws IOException {
        return cloudinaryService.delete(id);
    }

    /**************************************************************************************************************/

}
