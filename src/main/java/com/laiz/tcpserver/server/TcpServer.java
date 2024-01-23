package com.laiz.tcpserver.server;

import com.laiz.tcpserver.enums.cmdEnum;
import com.laiz.tcpserver.enums.stateEnum;
import com.laiz.tcpserver.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;

@Slf4j
@Component
public class TcpServer {

    private static TcpServerThread tcpServerThread;
    private static ServerSocket server;
    private static stateEnum serverState = stateEnum.STOPPED;
    private static cmdEnum startCmd = cmdEnum.NOT_ACTIVE;
    private static cmdEnum stopCmd = cmdEnum.NOT_ACTIVE;
    private static final int port = 2404;

    public TcpServer(TcpServerThread thread) {
        tcpServerThread = thread;
    }

    public static void start() {
        if (serverState != stateEnum.STARTED) {
            startCmd = cmdEnum.ACTIVE;
            log.info("Starting server...");
            MessageService.add("TCP сервер", "Запускается...");
        }
    }

    public static void stop() {
        if (serverState != stateEnum.STOPPED) {
            log.info("Stopping server...");
            MessageService.add("TCP сервер", "Останавливается...");
            stopCmd = cmdEnum.ACTIVE;
        }
    }

    @Scheduled(fixedDelay = 100)
    public void handleCommands() {
        if (startCmd == cmdEnum.ACTIVE) {
            try {
                server = new ServerSocket(port);
                TcpServerThread.setThreadState(stateEnum.STARTED);
                tcpServerThread.run(server);
                serverState = stateEnum.STARTED;
                startCmd = cmdEnum.NOT_ACTIVE;
                log.info("Server started. Waiting for the client connection...");
                MessageService.add("TCP сервер", "Запущен. Ждет подключения клиента...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (stopCmd == cmdEnum.ACTIVE) {
            try {
                TcpServerThread.setThreadState(stateEnum.STOPPED);
                server.close();
                serverState = stateEnum.STOPPED;
                stopCmd = cmdEnum.NOT_ACTIVE;
                log.info("Server stopped");
                MessageService.add("TCP сервер", "Остановлен");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

