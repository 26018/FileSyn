package com.moon.demo.entity;

import java.io.Serializable;

/**
 * @author JinHui
 * @date 2022/2/11
 */


public class Message implements Serializable {

    public byte[] object;

    public String fileName;

    public Long fileSize;

    public Message(String fName, byte[] bytes, Long fSize) {
        this.fileName = fName;
        this.object = bytes;
        this.fileSize = fSize;
    }
}
