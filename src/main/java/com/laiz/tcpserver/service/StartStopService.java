package com.laiz.tcpserver.service;

import com.laiz.tcpserver.enums.MessageTypeEnum;
import com.laiz.tcpserver.enums.StateEnum;
import com.laiz.tcpserver.server.TcpServer;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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

    public void start(Optional<String> messageTypeVar, Optional<String> endByteVar) {
        if (TcpServer.getServerState() != StateEnum.STARTED) {
            log.info("Starting server...");
            MessageService.add("TCP сервер", "Запускается...");

            if (messageTypeVar.isPresent()) {
                if (messageTypeVar.get().equals("string")) TcpServer.setMessageType(MessageTypeEnum.STRING);
                if (messageTypeVar.get().equals("bytes")) TcpServer.setMessageType(MessageTypeEnum.BYTES);
            }

            if (endByteVar.isPresent()) {
                try {
                    byte endByte = Byte.parseByte(endByteVar.get());
                    TcpServer.setEndByte(endByte);
                } catch (Exception e) {
                    log.info("End of message byte wasn't set by the client. Using default value");
                }
            }

            TcpServer.setServerState(StateEnum.STARTED);

            executor = Executors.newSingleThreadExecutor();
            executor.execute(tcpServer::runServer);
        }
    }

    public void stop() {
        if (TcpServer.getServerState() != StateEnum.STOPPED) {
            log.info("Stopping server...");
            MessageService.add("TCP сервер", "Останавливается...");

            TcpServer.setServerState(StateEnum.STOPPED);
            executor.shutdownNow();
        }
    }
}
