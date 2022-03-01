package com.moon.demo.controller;

import com.moon.demo.config.FileConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Stack;

/**
 * @author JinHui
 * @date 2022/2/12
 */

@RestController()
@RequestMapping("/demo")
public class MainController {

    @Resource
    Stack<String> receivedFiles ;
    @Resource
    FileConfig pathConfig;

    private final static Logger logger = LoggerFactory.getLogger(MainController.class);

    @GetMapping("/index/{filePath}")
    public String index(@PathVariable String filePath) {
        logger.info("请求文件{}",filePath);
        if (filePath.trim().length() == 0) {
            return "Fail";
        }
        receivedFiles.add(pathConfig.getTempFilePath()+filePath);
        return "Success";
    }

}
