package com.laiz.tcpserver.server;

import com.laiz.tcpserver.enums.MessageTypeEnum;
import com.laiz.tcpserver.enums.StateEnum;
import com.laiz.tcpserver.service.UILogService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class TcpServer {

    private static final int NTHREADS = 200;
    public static final int SO_TIMEOUT = 3000;
    private static final int DEFAULT_PORT = 2404;
    private static final byte DEFAULT_END_BYTE = 0x13;

    @Getter
    @Setter
    private static StateEnum serverState = StateEnum.STOPPED;

    @Getter
    @Setter
    private static int port = DEFAULT_PORT;

    @Getter
    @Setter
    private static MessageTypeEnum messageType = MessageTypeEnum.STRING;

    @Getter
    @Setter
    private static byte endByte = DEFAULT_END_BYTE;

    @Getter
    @Setter
    private static boolean addEnter = false;

    private ExecutorService executor;

    @Autowired
    private TcpRequestHandler handler;

    private static ServerSocket server;
    private static Socket socket;

    public void runServer() {

        try {
            server = new ServerSocket(port);
            server.setSoTimeout(SO_TIMEOUT);
            serverState = StateEnum.STARTED;

            log.info("Server started. Waiting for the client connection...");
            UILogService.add("TCP сервер", "Запущен. Ждет подключения клиента...");

            while (serverState != StateEnum.STOPPED) {
                try {
                    socket = server.accept();

                    TcpRequestHandler.setMessageType(messageType);
                    TcpRequestHandler.setEndByte(endByte);
                    TcpRequestHandler.setAddEnter(addEnter);
                    TcpRequestHandler.setThreadState(StateEnum.STARTED);
                    Runnable task = () -> handler.handleRequest(socket);

                    executor = Executors.newFixedThreadPool(NTHREADS);
                    executor.execute(task);
                } catch (SocketTimeoutException e) {
                    log.trace("Accept timed out.");
                }
            }

            TcpRequestHandler.setThreadState(StateEnum.STOPPED);

            if (socket != null) socket.close();
            if (executor != null) executor.shutdownNow();
            server.close();

            log.info("Server stopped");
            UILogService.add("TCP сервер", "Остановлен");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
