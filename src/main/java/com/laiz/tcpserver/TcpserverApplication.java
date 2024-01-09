package com.laiz.tcpserver;

import com.laiz.tcpserver.server.TcpServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TcpserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(TcpserverApplication.class, args);
        TcpServer.handleCommands();
    }
}
