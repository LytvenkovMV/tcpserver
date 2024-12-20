package com.laiz.tcpserver.controller;

import com.laiz.tcpserver.dao.UiLogRecord;
import com.laiz.tcpserver.service.UILogService;
import com.laiz.tcpserver.service.StartStopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/tcp-server")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TcpServerController {

    private final StartStopService service;

    @GetMapping("/output")
    public List<UiLogRecord> getLogRecords() {
        log.trace("Log records request");
        List<UiLogRecord> uiLogRecords = UILogService.poll();
        log.trace(uiLogRecords.size() + " new log records sent");
        return uiLogRecords;
    }

    @PostMapping(value = {"/start", "/start/{type}"})
    public void startServer(@PathVariable("type") Optional<String> messageType,
                            @RequestParam("p") Optional<String> port,
                            @RequestParam("e") Optional<String> endOfMessage,
                            @RequestParam("a") Optional<String> addEnter) {
        log.trace("Start command received");
        service.start(port, messageType, endOfMessage, addEnter);
    }

    @PostMapping("/stop")
    public void stopServer() {
        log.trace("Stop command received");
        service.stop();
    }
}