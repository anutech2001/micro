package com.ktb.payment.monitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class MonitorHandler {

    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
    	PaymentMonitor.sessionList.add(session);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
    	PaymentMonitor.sessionList.remove(session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
    	PaymentMonitor.broadcastMessage(message);
    }

}
