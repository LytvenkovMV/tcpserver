package com.laiz.tcpserver.controller;

import com.laiz.tcpserver.dto.GetOutputDataRequestDto;
import com.laiz.tcpserver.server.TcpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;

@Slf4j
@RestController
@RequestMapping("/tcp-server")
public class TcpServerController {

    @GetMapping("/output")
    public GetOutputDataRequestDto getOutputData() {
        log.info("Get output data request");

        GetOutputDataRequestDto dto = new GetOutputDataRequestDto();
        LocalTime localTime = java.time.LocalTime.now();
        dto.setTime(localTime.toSecondOfDay());
        dto.setSource("TCP server");
        dto.setMessage("Hello from server!!!!!!!!");

        return dto;
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