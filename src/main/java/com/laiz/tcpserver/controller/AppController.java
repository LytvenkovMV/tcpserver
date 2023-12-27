package com.laiz.tcpserver.controller;

import com.laiz.tcpserver.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/")
public class AppController {

    @GetMapping
    public String getIndex() {
        log.info("Index page request");
        return "index.html";
    }
}