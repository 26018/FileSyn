package com.moon.demo;

import com.moon.demo.Util.Utils;
import com.moon.demo.config.FileConfig;
import com.moon.demo.config.SocketConfig;
import com.moon.demo.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import javax.annotation.Resource;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Stack;

/**
 * @author MoonLight
 */

@SpringBootApplication
@EnableConfigurationProperties({SocketConfig.class, FileConfig.class})
public class DemoApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    // 存储上传至服务器的文件列表
    @Resource
    Stack<String> receivedFiles;
    @Resource
    SocketConfig socketConfig;
    @Resource
    FileConfig fileConfig;
    // 过期文件队列
    Queue<String> expiredFiles = new ArrayDeque<>();

    private final static Logger logger = LoggerFactory.getLogger(DemoApplication.class);

    @Override
    public void run(String... args) throws Exception {
        // 过期文件处理线程
        new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(fileConfig.getTempFileCheckMs());
                    tempFileManager();
                }
            } catch (InterruptedException | FileNotFoundException e) {
                e.printStackTrace();
            }
        }).start();

        // 与客户端连接线程
        new Thread(()->{
            try {
                connectWithClient();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    public void connectWithClient() throws IOException {
        logger.info("file syn server running on {} port..", socketConfig.getPort());
        ServerSocket serverSocket = new ServerSocket(socketConfig.getPort());
        ObjectOutputStream objectOutputStream;
        while (true) {
            Socket accept = serverSocket.accept();
            logger.info("{}已连接服务", accept.getInetAddress());
            objectOutputStream = new ObjectOutputStream(accept.getOutputStream());
            while (true) {
                try {
                    if (!receivedFiles.isEmpty()) {
                        // 发送心跳包 若未应答，则捕获异常
                        accept.sendUrgentData(0xFF);
                        long start = System.currentTimeMillis();
                        Message message = Utils.fileToMessage(receivedFiles.peek());
                        objectOutputStream.writeObject(message);
                        objectOutputStream.flush();
                        long end = System.currentTimeMillis();
                        String pop = receivedFiles.pop();
                        expiredFiles.add(pop);
                        logger.info("{}接收文件{}共用时{}ms", accept.getInetAddress(), message.fileName, (end - start));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    objectOutputStream.close();
                    break;
                }
            }
        }
    }

    public void tempFileManager() throws FileNotFoundException {
        String tempFilePath = fileConfig.getTempFilePath();
        File file = new File(tempFilePath);
        if (!file.exists()) {
            file.mkdir();
        }
        if (file.isDirectory()) {
            while (!expiredFiles.isEmpty()) {
                File f = new File(expiredFiles.peek());
                if (System.currentTimeMillis() - f.lastModified() > fileConfig.getTempFileCheckMs()) {
                    f.delete();
                    expiredFiles.poll();
                }else {
                    break;
                }
            }
        }
    }
}