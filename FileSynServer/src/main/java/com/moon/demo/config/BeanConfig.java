package com.moon.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Stack;

/**
 * @author JinHui
 * @date 2022/2/12
 */

@Configuration
public class BeanConfig {

    @Bean
    public Stack<String> getStack(){
        return new Stack<>();
    }

}
