package com.laiz.tcpserver.controller;

import com.laiz.tcpserver.dao.AppMessage;
import com.laiz.tcpserver.server.TcpServer;
import com.laiz.tcpserver.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/tcp-server")
@CrossOrigin(origins = "*")
public class TcpServerController {

    @Autowired
    TcpServer tcpServer;

    @GetMapping("/output")
    public List<AppMessage> getOutputData() {
        log.info("Output data request");
        List<AppMessage> appMessages = MessageService.give();
        log.info(appMessages.size() + " new messages sent");
        return appMessages;
    }

    @PostMapping(value = {"/start", "/start/{type}"})
    public void startServer(@PathVariable("type") Optional<String> messageType,
                            @RequestParam("p") Optional<String> port,
                            @RequestParam("e") Optional<String> endOfMessage,
                            @RequestParam("a") Optional<String> addEnter) {
        log.info("Start command received");
        tcpServer.start(port, messageType, endOfMessage, addEnter);
    }

    @PostMapping("/stop")
    public void stopServer() {
        log.info("Stop command received");
        tcpServer.stop();
    }
}