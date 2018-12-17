package com.lebron.ws.connect;

import com.lebron.ws.websocket.LebronWebsocket;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Auther: lebron
 * @Date: 2018/12/18 01:23
 * @Description: 用于管理所有连接的websocket连接
 */
public class WebsocketConnect {

    private static final ConcurrentHashMap<String, CopyOnWriteArrayList<LebronWebsocket>> connMap = new ConcurrentHashMap<>();

    private WebsocketConnect() {
    }

    public static void addConnect(LebronWebsocket endpoint) {
        CopyOnWriteArrayList<LebronWebsocket> list = connMap.get(endpoint.getRoomId());
        if (list == null) {
            list = new CopyOnWriteArrayList<LebronWebsocket>();
        }
        list.add(endpoint);
        connMap.put(endpoint.getRoomId(), list);
    }

    public static void removeConnect(LebronWebsocket endpoint) {
        CopyOnWriteArrayList<LebronWebsocket> list = connMap.get(endpoint.getRoomId());
        if (list != null) {
            list.remove(endpoint);
        }
        if (list != null) {
            connMap.put(endpoint.getRoomId(), list);
        }
    }

    public static List<LebronWebsocket> getRoomConnect(String roomId) {
        return connMap.get(roomId);
    }
}
