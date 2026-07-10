package com.sky.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket服务 - 支持订单通知和座位状态实时同步
 */
@Component
@Slf4j
@ServerEndpoint("/ws/{sid}")
public class WebSocketServer {

    // 存放会话对象
    private static final Map<String, Session> sessionMap = new HashMap<>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        log.info("客户端：{} 建立连接", sid);
        sessionMap.put(sid, session);
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message, @PathParam("sid") String sid) {
        log.info("收到来自客户端：{} 的信息: {}", sid, message);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam("sid") String sid) {
        log.info("连接断开: {}", sid);
        sessionMap.remove(sid);
    }

    /**
     * 功能增强
     * 为指定店铺推送消息
     */
    public void sendMessage(String sid, String message) {
        Session session = sessionMap.get(sid);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.error("发送WebSocket消息失败", e);
            }
        }
    }

    /**
     * 群发
     */
    public void sendToAllClient(String message) {
        Collection<Session> sessions = sessionMap.values();
        for (Session session : sessions) {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    log.error("发送WebSocket消息失败", e);
                }
            }
        }
    }

    /**
     * 推送座位状态变更事件
     */
    public void notifySeatStatusChange(Long seatId, String seatCode, String oldStatus, String newStatus) {
        JSONObject event = new JSONObject();
        event.put("type", "SEAT_STATUS_CHANGE");
        event.put("seatId", seatId);
        event.put("seatCode", seatCode);
        event.put("oldStatus", oldStatus);
        event.put("newStatus", newStatus);
        event.put("timestamp", System.currentTimeMillis());

        String message = JSON.toJSONString(event);
        sendToAllClient(message);
        log.info("推送座位状态变更: seat={} {} -> {}", seatCode, oldStatus, newStatus);
    }

    /**
     * 推送新订单事件
     */
    public void notifyNewOrder(JSONObject orderData) {
        orderData.put("type", "NEW_ORDER");
        orderData.put("timestamp", System.currentTimeMillis());
        String message = JSON.toJSONString(orderData);
        sendToAllClient(message);
        log.info("推送新订单通知");
    }
}
