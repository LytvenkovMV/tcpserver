package com.laiz.tcpserver;

import com.laiz.tcpserver.server.TcpServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class TcpserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(TcpserverApplication.class, args);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new TcpServer());
        executor.shutdown();
    }
}
