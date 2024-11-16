package com.laiz.tcpserver.service;

import com.laiz.tcpserver.enums.MessageTypeEnum;
import com.laiz.tcpserver.enums.StateEnum;
import com.laiz.tcpserver.server.TcpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class StartStopService {

    @Autowired
    private TcpServer tcpServer;
    private ExecutorService executor;

    public void start(Optional<String> userPort
            , Optional<String> userMessageType
            , Optional<String> userEndByte
            , Optional<String> userAddEnter) {
        if (TcpServer.getServerState() != StateEnum.STARTED) {
            log.info("Starting server...");
            UILogService.add("TCP сервер", "Запускается...");

            TcpServer.setPort(getPort(userPort));
            TcpServer.setMessageType(getMessageType(userMessageType));
            TcpServer.setEndByte(getEndByte(userEndByte));
            TcpServer.setAddEnter(getAddEnter(userAddEnter));
            TcpServer.setServerState(StateEnum.STARTED);

            executor = Executors.newSingleThreadExecutor();
            executor.execute(tcpServer::runServer);
        }
    }

    public void stop() {
        if (TcpServer.getServerState() != StateEnum.STOPPED) {
            log.info("Stopping server...");
            UILogService.add("TCP сервер", "Останавливается...");

            TcpServer.setServerState(StateEnum.STOPPED);
            executor.shutdownNow();
        }
    }

    private int getPort(Optional<String> userPort) {
        int port = 2404;
        if (userPort.isPresent()) {
            try {
                port = Integer.parseInt(userPort.get());
            } catch (Exception e) {
                log.info("Port wasn't set by the user. Using default value");
            }
        }
        return port;
    }

    private MessageTypeEnum getMessageType(Optional<String> userMessageType) {
        MessageTypeEnum messageType = MessageTypeEnum.BYTES;
        if (userMessageType.isPresent()) {
            if (userMessageType.get().equals("string")) messageType = MessageTypeEnum.STRING;
        }
        return messageType;
    }

    private byte getEndByte(Optional<String> userEndByte) {
        byte endByte = Byte.parseByte("13");
        if (userEndByte.isPresent()) {
            try {
                endByte = Byte.parseByte(userEndByte.get());
            } catch (Exception e) {
                log.info("End of message wasn't set by the user. Using default value");
            }
        }
        return endByte;
    }

    private boolean getAddEnter(Optional<String> userAddEnter) {
        boolean isAddEnter = false;
        if (userAddEnter.isPresent()) {
            if (userAddEnter.get().equals("1")) isAddEnter = true;
        }
        return isAddEnter;
    }
}
