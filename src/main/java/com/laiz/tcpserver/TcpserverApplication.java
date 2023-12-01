package com.laiz.tcpserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class TcpserverApplication {

    public static void main(String[] args) throws Exception {


        ////////////SpringApplication.run(TcpserverApplication.class, args);


        ApplicationContext context = new AnnotationConfigApplicationContext("com.laiz.tcpserver");

        TcpServer tcpServer = context.getBean(TcpServer.class);
        tcpServer.start();
    }
}
