package com.lebron.ws;

import com.lebron.ws.thread.SendMessageThread;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LebronWsApplication {

    public static void main(String[] args) {
        SendMessageThread.build().start();
        SpringApplication.run(LebronWsApplication.class, args);
    }

}

