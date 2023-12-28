package com.laiz.tcpserver.controller;

import com.laiz.tcpserver.dao.AppMessage;
import com.laiz.tcpserver.service.MessageService;
import com.laiz.tcpserver.server.TcpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/tcp-server")
@CrossOrigin(origins = "*")
public class TcpServerController {

    @GetMapping("/output")
    public List<AppMessage> getOutputData() {
        log.info("Output data request");
        List<AppMessage> appMessages = MessageService.give();
        log.info(appMessages.size() + " new messages sent");
        return appMessages;
    }

    @PostMapping("/start")
    public void startServer() {
        log.info("Start command received");
        TcpServer.start();
    }

    @PostMapping("/stop")
    public void stopServer() {
        log.info("Stop command received");
        TcpServer.stop();
    }
}