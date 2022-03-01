package com.moon.demo.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author JinHui
 * @date 2022/2/12
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "file", ignoreUnknownFields = true)
public class FileConfig {
    String tempFilePath;
    long tempFileCheckMs;
}
