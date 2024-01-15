package com.laiz.tcpserver.server;

import com.laiz.tcpserver.enums.TcpServerCmd;
import com.laiz.tcpserver.enums.TcpServerState;
import com.laiz.tcpserver.service.MessageService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ServerSocket;

@Slf4j
@Component
public final class TcpServer {

    @Autowired
    private TcpServerThread tcpServerThread;

    private static ServerSocket server;
    private static TcpServerState serverState = TcpServerState.STOPPED;
    private static TcpServerCmd startCmd = TcpServerCmd.NOT_ACTIVE;
    private static TcpServerCmd stopCmd = TcpServerCmd.NOT_ACTIVE;
    private static final int port = 2404;

    public static void start() {
        if (serverState != TcpServerState.STARTED) {
            startCmd = TcpServerCmd.ACTIVE;
            log.info("Starting server...");
            MessageService.add("TCP сервер", "Запускается...");
        }
    }

    public static void stop() {
        if (serverState != TcpServerState.STOPPED) {
            log.info("Stopping server...");
            MessageService.add("TCP сервер", "Останавливается...");
            stopCmd = TcpServerCmd.ACTIVE;
        }
    }

    public static ServerSocket getServer() {
        return server;
    }

    @Scheduled(fixedDelay = 100)
    public void handleCommands() {
        if (startCmd == TcpServerCmd.ACTIVE) {
            try {
                server = new ServerSocket(port);
                tcpServerThread.setIsRunning(true);
                tcpServerThread.run();
                serverState = TcpServerState.STARTED;
                startCmd = TcpServerCmd.NOT_ACTIVE;
                log.info("Server started. Waiting for the client connection...");
                MessageService.add("TCP сервер", "Запущен. Ждет подключения клиента...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (stopCmd == TcpServerCmd.ACTIVE) {
            try {
                tcpServerThread.setIsRunning(false);
                server.close();
                serverState = TcpServerState.STOPPED;
                stopCmd = TcpServerCmd.NOT_ACTIVE;
                log.info("Server stopped");
                MessageService.add("TCP сервер", "Остановлен");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

