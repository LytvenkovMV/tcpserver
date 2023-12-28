package com.laiz.tcpserver.server;

import com.laiz.tcpserver.enums.TcpServerCmd;
import com.laiz.tcpserver.enums.TcpServerState;
import com.laiz.tcpserver.service.MessageService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public final class TcpServer {

    private static ServerSocket server;
    private static ExecutorService executor;
    private static TcpServerState serverState = TcpServerState.STOPPED;
    private static TcpServerCmd startCmd = TcpServerCmd.NOT_ACTIVE;
    private static TcpServerCmd stopCmd = TcpServerCmd.NOT_ACTIVE;
    private static int port = 2404;

    public static void start() {
        if (serverState != TcpServerState.STARTED) startCmd = TcpServerCmd.ACTIVE;
    }

    public static void stop() {
        if (serverState != TcpServerState.STOPPED) stopCmd = TcpServerCmd.ACTIVE;
    }

    public static void handleCommands() {

        while (true) {

            if (startCmd == TcpServerCmd.ACTIVE) {
                try {
                    log.info("Starting server...");
                    MessageService.add("TCP сервер", "Запускается...");
                    server = new ServerSocket(port);

                    executor = Executors.newFixedThreadPool(1);
                    executor.execute(new TcpServerThread(server));
                    executor.shutdown();

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
                    log.info("Stopping server...");
                    MessageService.add("TCP сервер", "Останавливается...");
                    executor.shutdownNow();
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
}

