package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * 文件的上传和下载
 */
@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${reggie.path}")
    private String path;
    /**
     * 文件上传方法
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){//file名称固定，与前端保持一致
        String originalFilename = file.getOriginalFilename();
        String filename = originalFilename.substring(originalFilename.lastIndexOf("."));
        filename= UUID.randomUUID()+filename;
        File file1 = new File(path);
        if(!file1.exists()){
            file1.mkdirs();
        }
        try {
            file.transferTo(new File(path+filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(filename);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        FileInputStream fileInputStream = null;
        ServletOutputStream outputStream = null;
        try {
            fileInputStream = new FileInputStream(new File(path + name));
             outputStream = response.getOutputStream();
             response.setContentType("image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];
            while((len=fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            try {
                if(fileInputStream!=null){
                    fileInputStream.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                if(outputStream!=null){
                    outputStream.close();

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
