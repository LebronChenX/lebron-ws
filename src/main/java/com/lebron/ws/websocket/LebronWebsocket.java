package com.lebron.ws.websocket;

import com.lebron.ws.config.MyEndpointConfigure;
import com.lebron.ws.connect.WebsocketConnect;
import com.lebron.ws.constant.MyConstant;
import com.lebron.ws.model.Message;
import com.lebron.ws.thread.SendMessageThread;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Auther: lebron
 * @Date: 2018/12/18 01:23
 * @Description: websocket连接类，需要置为多例
 */
@Slf4j
@Data
@Component
@Scope("prototype")
@ServerEndpoint(value = "/websocket/{roomId}", configurator = MyEndpointConfigure.class)
public class LebronWebsocket {

    private Session session;
    private String roomId;
    private String name;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        Map<String, List<String>> requestMap = session.getRequestParameterMap();

        roomId = requestMap.get("roomId").get(0);
        name = requestMap.get("name").get(0);
        if (Objects.isNull(roomId) || Objects.isNull(name)) {
            closeByParamError();
            return;
        }

        WebsocketConnect.addConnect(this);
        log.error("[websocket connect success] roomId:{} name:{}", roomId, name);
    }

    public void closeByParamError() {
        try {
            session.close(new CloseReason(CloseReason.CloseCodes.getCloseCode(MyConstant.CloseReason.PARAM_ERROR), "Parameter Error"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    @OnClose
    public void onClose(CloseReason closeReason) {
        log.error("[websocket connect remove] onClose roomId:{} name:{} closeCode:{}", roomId, name, closeReason);
        WebsocketConnect.removeConnect(this);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("[websocket connect remove] onError roomId:{} name:{} error:{}", roomId, name, error.getMessage());
        log.error(error.toString(), error);
        WebsocketConnect.removeConnect(this);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("[websocket receiver message] roomId:{} message:{}", roomId, message);
        Message msg = new Message(roomId, name, message);
        SendMessageThread.build().offerQueue(msg);
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

}
