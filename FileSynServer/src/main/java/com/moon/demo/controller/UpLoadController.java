package com.moon.demo.controller;

import com.moon.demo.config.FileConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

/**
 * @author MoonLight
 */
@RestController
@RequestMapping("/file")
public class UpLoadController {

    @Resource
    FileConfig pathConfig;
    @Resource
    Stack<String> receivedFiles;

    private final static Logger logger = LoggerFactory.getLogger(MainController.class);

    @PostMapping("/multiUpload")
    public Object multiUpload(@RequestParam("file")MultipartFile[] files){
        for (MultipartFile f : files){
            if (saveFile(f)) {
                receivedFiles.add(pathConfig.getTempFilePath()+f.getOriginalFilename());
            }else {
                logger.info("保存文件{}至磁盘失败", f.getOriginalFilename());
                // TODO 记录保存失败的文件名称，返回给浏览器
            }
        }
        return "ok";
    }
 
    private boolean saveFile(MultipartFile file){
        if (file.isEmpty()){
            return false;
        }
        String filename = file.getOriginalFilename();
        String filePath = pathConfig.getTempFilePath();
        File temp = new File(filePath);
        if (!temp.exists()){
            boolean mkdirs = temp.mkdirs();
            if (!mkdirs) {
                logger.error("创建目录{}失败", filePath);
            }
        }
        File localFile = new File(filePath+filename);
        try {
            //把上传的文件保存至本地
            file.transferTo(localFile);
            logger.info("{}文件已保存至磁盘",file.getOriginalFilename());
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}