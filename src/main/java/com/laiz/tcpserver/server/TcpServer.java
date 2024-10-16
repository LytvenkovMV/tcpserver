package com.laiz.tcpserver.server;

import com.laiz.tcpserver.enums.CmdEnum;
import com.laiz.tcpserver.enums.MessageTypeEnum;
import com.laiz.tcpserver.enums.StateEnum;
import com.laiz.tcpserver.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TcpServer {

    private static final int NTHREADS = 32;
    Executor executor = Executors.newFixedThreadPool(NTHREADS);

    private final TcpServerThread tcpServerThread;
    private static ServerSocket server;
    private static StateEnum serverState = StateEnum.STOPPED;
    private static MessageTypeEnum messageType = MessageTypeEnum.STRING;
    private static CmdEnum startCmd = CmdEnum.NOT_ACTIVE;
    private static CmdEnum stopCmd = CmdEnum.NOT_ACTIVE;
    private static final int port = 2404;
    private static final byte defaultEndByte = Byte.parseByte("13");
    private static byte endByte = defaultEndByte;

    public static void start(Optional<String> messageTypeVar, Optional<String> endByteVar) {
        if (serverState != StateEnum.STARTED) {
            startCmd = CmdEnum.ACTIVE;
            if (messageTypeVar.isPresent()) {
                if (messageTypeVar.get().equals("string")) messageType = MessageTypeEnum.STRING;
                if (messageTypeVar.get().equals("bytes")) messageType = MessageTypeEnum.BYTES;
            }
            if (endByteVar.isPresent()) {
                try {
                    endByte = Byte.parseByte(endByteVar.get());
                } catch (Exception e) {
                    endByte = defaultEndByte;
                }
            }
            log.info("Starting server...");
            MessageService.add("TCP сервер", "Запускается...");
        }
    }

    public static void stop() {
        if (serverState != StateEnum.STOPPED) {
            log.info("Stopping server...");
            MessageService.add("TCP сервер", "Останавливается...");
            stopCmd = CmdEnum.ACTIVE;
        }
    }

    @Scheduled(fixedDelay = 100)
    public void handleCommands() {
        if (startCmd == CmdEnum.ACTIVE) {
            try {
                server = new ServerSocket(port);
                TcpServerThread.setMessageType(messageType);
                TcpServerThread.setEndByte(endByte);
                TcpServerThread.setThreadState(StateEnum.STARTED);

                serverState = StateEnum.STARTED;
                startCmd = CmdEnum.NOT_ACTIVE;
                log.info("Server started. Waiting for the client connection...");
                MessageService.add("TCP сервер", "Запущен. Ждет подключения клиента...");

                Socket socket = server.accept();
                Runnable task = () -> tcpServerThread.handleRequest(socket);
                executor.execute(task);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (stopCmd == CmdEnum.ACTIVE) {
            try {
                TcpServerThread.setThreadState(StateEnum.STOPPED);

                server.close();
                serverState = StateEnum.STOPPED;
                stopCmd = CmdEnum.NOT_ACTIVE;

                log.info("Server stopped");
                MessageService.add("TCP сервер", "Остановлен");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

