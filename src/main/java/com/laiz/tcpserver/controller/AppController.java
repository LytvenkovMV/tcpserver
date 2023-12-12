package com.laiz.tcpserver.controller;

import com.laiz.tcpserver.TcpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/")
public class AppController {

    @Autowired
    ApplicationContext context;

    @GetMapping("/")
    public String getIndex() {
        return "indexTCP.html";
    }

    @PostMapping("/start")
    public void startServer() {
        try {
            context.getBean(TcpServer.class).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/stop")
    public void stopServer() {
        try {
            context.getBean(TcpServer.class).stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}