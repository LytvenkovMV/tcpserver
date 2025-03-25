package com.laiz.tcpserver.server;

import com.laiz.tcpserver.enums.StateEnum;
import com.laiz.tcpserver.service.UILogService;
import com.laiz.tcpserver.settings.AppSettings;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
@RequiredArgsConstructor
public class TcpServer {
    private final ExecutorService requestHandlerExecutor;
    private final AppSettings appSettings;
    private final TcpRequestHandler tcpRequestHandler;

    @Getter
    @Setter
    private StateEnum serverState = StateEnum.STOPPED;
    private Socket socket;

    public void runServer() {

        try (ServerSocket server = new ServerSocket(appSettings.getPort())) {
            server.setSoTimeout(appSettings.getSoTimeout());
            serverState = StateEnum.STARTED;

            log.info("Server started. Waiting for the client connection...");
            UILogService.add("TCP сервер", "Запущен. Ждет подключения клиента...");

            while (serverState != StateEnum.STOPPED) {
                try {
                    socket = server.accept();

                    tcpRequestHandler.setThreadState(StateEnum.STARTED);

                    requestHandlerExecutor.execute(() -> tcpRequestHandler.handleRequest(socket));
                } catch (SocketTimeoutException e) {
                    log.trace("Accept timed out.");
                }
            }
        } catch (IOException e) {
            log.warn("Exception while communication with client", e);
        } finally {
            tcpRequestHandler.setThreadState(StateEnum.STOPPED);

            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                log.warn("Exception while closing the socket", e);
            }

            log.info("Server stopped");
            UILogService.add("TCP сервер", "Остановлен");
        }
    }
}
