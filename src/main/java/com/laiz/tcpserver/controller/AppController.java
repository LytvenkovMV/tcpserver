package com.laiz.tcpserver.controller;

import com.laiz.tcpserver.TcpServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping(path = "/")
public class AppController {

    @Autowired
    ApplicationContext context;

    @GetMapping("/")
    public String getIndex() {
        return "index.html";
    }

    @PostMapping(path = "/start-server")
    public String startServer() {
        try {
            context.getBean(TcpServer.class).start();
        } catch (IOException e) {
            e.printStackTrace();
            return "error1.html";
        }
        return "result1.html";
    }

    @PostMapping(path = "/stop-server")
    public String stopServer() {
        try {
            context.getBean(TcpServer.class).stop();
        } catch (IOException e) {
            e.printStackTrace();
            return "error1.html";
        }
        return "result1.html";
    }
}