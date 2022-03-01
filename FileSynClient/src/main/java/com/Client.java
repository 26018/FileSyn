package com;

import com.moon.demo.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author MoonLight
 */
class Client {

    private final static Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) {
        HashMap<String, String> map = new Client().readConfigurationFile();
        if (map == null) {
            logger.debug("读取配置文件出错...");
            return;
        }
        logger.debug("成功读取配置文件...");
        String fileSavePath = map.get("filePath");
        String ip = map.get("ip");
        while (true) {
            try {
                Socket socket = new Socket(ip, Integer.parseInt(map.get("serverPort")));
                try {
                    logger.debug("已连接主机{}", ip);
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    while (true) {
                        Message message = (Message) ois.readObject();
                        boolean b = Utils.messageToFile(message, fileSavePath);
                        String stat = "";
                        stat = b ? "成功" : "失败";
                        logger.info("接收来自{}的{}文件{}", ip, message.fileName, stat);
                    }
                } catch (Exception e) {
                    getErrorMessage(e);
                }
            } catch (Exception e) {
                getErrorMessage(e);
            }
        }
    }

    public HashMap<String, String> readConfigurationFile() {
        BufferedReader bufferedReader;
        HashMap<String, String> map = new HashMap<>(16);
        try {
            FileReader fileReader = new FileReader(Objects.requireNonNull(Client.class.getClassLoader().getResource("")).getPath()+"setting.txt");
            bufferedReader = new BufferedReader(fileReader);
            Stream<String> lines = bufferedReader.lines();
            for (Object o : lines.toArray()) {
                String s = (String) o;
                String[] split = s.split("::");
                map.put(split[0], split[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return map;
    }

    public static void getErrorMessage(Exception e) {
        String[] error = {"refused", "reset"};
        String errorMessage = e.getMessage();
        if (e.getMessage() == null) {
            logger.debug("异常空指针");
            e.printStackTrace();
        } else if (errorMessage.contains(error[0])) {
            logger.debug("主机拒绝连接...");
        } else if (errorMessage.contains(error[1])) {
            logger.debug("主机已重置...");
        } else {
            e.printStackTrace();
        }
    }
}
