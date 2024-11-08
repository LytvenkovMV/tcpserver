package com.laiz.tcpserver.server;

import com.laiz.tcpserver.enums.MessageTypeEnum;
import com.laiz.tcpserver.enums.StateEnum;
import com.laiz.tcpserver.service.MessageService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class TcpServer {

    private static final int NTHREADS = 32;
    private static final int PORT = 2404;
    public static final int SO_TIMEOUT = 3000;
    private static final byte DEFAULT_END_BYTE = Byte.parseByte("13");

    @Getter
    @Setter
    private static StateEnum serverState = StateEnum.STOPPED;

    @Getter
    @Setter
    private static MessageTypeEnum messageType = MessageTypeEnum.STRING;

    @Getter
    @Setter
    private static byte endByte = DEFAULT_END_BYTE;

    private static ExecutorService executor;

    private static ServerSocket server;
    private static Socket socket;

    public static void runServer() {

        try {
            server = new ServerSocket(PORT);
            server.setSoTimeout(SO_TIMEOUT);
            serverState = StateEnum.STARTED;

            log.info("Server started. Waiting for the client connection...");
            MessageService.add("TCP сервер", "Запущен. Ждет подключения клиента...");

            while (serverState != StateEnum.STOPPED) {
                try {
                    socket = server.accept();

                    TcpRequestHandler.setMessageType(messageType);
                    TcpRequestHandler.setEndByte(endByte);
                    TcpRequestHandler.setThreadState(StateEnum.STARTED);
                    Runnable task = () -> TcpRequestHandler.handleRequest(socket);

                    executor = Executors.newFixedThreadPool(NTHREADS);
                    executor.execute(task);
                } catch (SocketTimeoutException e) {
                    log.info("Accept timed out.");
                }
            }

            TcpRequestHandler.setThreadState(StateEnum.STOPPED);

            if (socket != null) socket.close();
            if (executor != null) executor.shutdownNow();
            server.close();

            log.info("Server stopped");
            MessageService.add("TCP сервер", "Остановлен");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

