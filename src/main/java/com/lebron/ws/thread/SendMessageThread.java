package com.lebron.ws.thread;

import com.lebron.ws.connect.WebsocketConnect;
import com.lebron.ws.model.Message;
import com.lebron.ws.websocket.LebronWebsocket;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @Auther: lebron
 * @Date: 2018/12/18 01:23
 * @Description: 线程把消息发送到对应websocket中
 */
@Slf4j
public class SendMessageThread extends Thread {

    private static final BlockingDeque<Message> messageQueue = new LinkedBlockingDeque<>(1024);

    private SendMessageThread(){ }

    private static SendMessageThread thread = null;

    public static SendMessageThread build() {
        if (thread == null) {
            synchronized (messageQueue) {
                if (thread == null) {
                    thread = new SendMessageThread();
                }
            }
        }
        return thread;
    }

    public int getQueueSize() {
        return messageQueue.size();
    }

    public void offerQueue(Message message) {
        try {
            messageQueue.offer(message);
            log.error("MessageInfoQueue accept message:{}", message);
        } catch (Exception e) {
            log.error("消息写入失败", e);
        }
    }

    @Override
    public void run() {
        List<Message> bufferList = new ArrayList<>();
        while (true) {
            try {
                bufferList.add(messageQueue.take());
                messageQueue.drainTo(bufferList);
                if (!bufferList.isEmpty()) {
                    for (Message message : bufferList) {
                        String roomId = message.getRoomId();
                        List<LebronWebsocket> roomConnect = WebsocketConnect.getRoomConnect(roomId);
                        if (roomConnect != null && !roomConnect.isEmpty()) {
                            for (LebronWebsocket endpoint : roomConnect
                            ) {
                                String msg = new StringBuffer(message.getName()).append(":").append(message.getMessage()).toString();
                                endpoint.sendMessage(msg);
                            }
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                // 防止缓冲队列填充数据出现异常时不断刷屏
                try {
                    Thread.sleep(1000);
                } catch (Exception eee) {
                    log.error(e.toString());
                }
            } finally {
                if (!bufferList.isEmpty()) {
                    bufferList.clear();
                }
            }


        }

    }
}
