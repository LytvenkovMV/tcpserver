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
        log.info("Log records request");
        List<UiLogRecord> uiLogRecords = UILogService.poll();
        log.info(uiLogRecords.size() + " new log records sent");
        return uiLogRecords;
    }

    @PostMapping(value = {"/start", "/start/{type}"})
    public void startServer(@PathVariable("type") Optional<String> messageType,
                            @RequestParam("e") Optional<String> endOfMessage) {
        log.info("Start command received");
        service.start(messageType, endOfMessage);
    }

    @PostMapping("/stop")
    public void stopServer() {
        log.info("Stop command received");
        service.stop();
    }
}