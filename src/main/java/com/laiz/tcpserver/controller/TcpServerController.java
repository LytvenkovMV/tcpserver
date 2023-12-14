package com.laiz.tcpserver.controller;

import com.laiz.tcpserver.TcpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/tcp-server")
public class TcpServerController {

    @PostMapping
    public void startServer() {
        log.info("Start command received");
        TcpServer.start();
    }

    @DeleteMapping
    public void stopServer() {
        log.info("Stop command received");
        TcpServer.stop();
    }
}