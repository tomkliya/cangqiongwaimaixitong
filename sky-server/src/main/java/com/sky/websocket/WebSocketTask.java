package com.sky.websocket;

import com.sky.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
public class WebSocketTask {
    @Autowired
    private WebSocketServer webSocketServer;

    /**
     * 通过WebSocket每隔5秒向客户端发送消息
     */
    //@Scheduled(cron = "0/5 * * * * ?")
    public void sendMessageToClient() {
        String formattedTime = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
        String jsonMessage = "{\n" +
                "  \"message\": \"这是来自服务端的消息\",\n" +
                "  \"time\": \"" + formattedTime + "\"\n" +
                "}";
        webSocketServer.sendToAllClient(jsonMessage);
    }
}
