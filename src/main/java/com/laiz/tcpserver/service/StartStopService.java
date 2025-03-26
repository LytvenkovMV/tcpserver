package com.laiz.tcpserver.service;

import com.laiz.tcpserver.enums.StateEnum;
import com.laiz.tcpserver.logger.AppLogger;
import com.laiz.tcpserver.server.TcpServer;
import com.laiz.tcpserver.settings.AppSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class StartStopService {
    private final ExecutorService tcpServerExecutor;
    private final AppSettings settings;
    private final AppLogger logger;
    private final TcpServer tcpServer;
    private Future<Boolean> isStopped;

    public void start(Optional<String> userPort,
                      Optional<String> userMessageType,
                      Optional<String> userEndByte,
                      Optional<String> userAddEnter) {
        if (tcpServer.getServerState() != StateEnum.STARTED) {
            logger.serverStarting();

            userPort.ifPresent(settings::initUserPort);
            userMessageType.ifPresent(settings::initMessageType);
            userEndByte.ifPresent(settings::initEndByte);
            userAddEnter.ifPresent(settings::initAddEnter);

            tcpServer.setServerState(StateEnum.STARTED);

            isStopped = tcpServerExecutor.submit(tcpServer::runServer);
        }
    }

    public void stop() {
        if (tcpServer.getServerState() != StateEnum.STOPPED) {
            logger.serverStopping();

            tcpServer.setServerState(StateEnum.STOPPED);

            try {
                isStopped.get(10, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.warn("Exception while stopping the server", e);
            }
        }
    }
}
