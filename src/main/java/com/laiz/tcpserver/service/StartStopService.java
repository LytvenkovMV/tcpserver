package com.laiz.tcpserver.service;

import com.laiz.tcpserver.enums.StateEnum;
import com.laiz.tcpserver.server.TcpServer;
import com.laiz.tcpserver.settings.AppSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class StartStopService {
    private final ExecutorService tcpServerExecutor;
    private final AppSettings appSettings;
    private final TcpServer tcpServer;

    public void start(Optional<String> userPort,
                      Optional<String> userMessageType,
                      Optional<String> userEndByte,
                      Optional<String> userAddEnter) {
        if (tcpServer.getServerState() != StateEnum.STARTED) {
            log.info("Starting server...");
            UILogService.add("TCP сервер", "Запускается...");

            userPort.ifPresent(appSettings::initUserPort);
            userMessageType.ifPresent(appSettings::initMessageType);
            userEndByte.ifPresent(appSettings::initEndByte);
            userAddEnter.ifPresent(appSettings::initAddEnter);

            tcpServer.setServerState(StateEnum.STARTED);

            tcpServerExecutor.execute(tcpServer::runServer);
        }
    }

    public void stop() {
        if (tcpServer.getServerState() != StateEnum.STOPPED) {
            log.info("Stopping server...");
            UILogService.add("TCP сервер", "Останавливается...");

            tcpServer.setServerState(StateEnum.STOPPED);
        }
    }
}
