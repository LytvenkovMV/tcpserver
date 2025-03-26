package com.laiz.tcpserver.server;

import com.laiz.tcpserver.enums.StateEnum;
import com.laiz.tcpserver.logger.AppLogger;
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
    private final AppSettings settings;
    private final AppLogger logger;
    private final TcpRequestHandler tcpRequestHandler;

    @Getter
    @Setter
    private volatile StateEnum serverState = StateEnum.STOPPED;
    private Socket socket;

    public boolean runServer() {

        try (ServerSocket server = new ServerSocket(settings.getPort())) {
            server.setSoTimeout(settings.getSoTimeout());
            serverState = StateEnum.STARTED;

            logger.serverStarted();

            while (serverState != StateEnum.STOPPED) {
                try {
                    socket = server.accept();

                    tcpRequestHandler.setThreadState(StateEnum.STARTED);

                    requestHandlerExecutor.submit(() -> tcpRequestHandler.handleRequest(socket));
                } catch (SocketTimeoutException e) {
                    log.trace("Accept timed out.");
                }
            }
        } catch (IOException e) {
            log.warn("Exception while communication with client", e);
        } finally {
            try {
                tcpRequestHandler.setThreadState(StateEnum.STOPPED);

                if (socket != null) socket.close();

                logger.serverStopped();
            } catch (Exception e) {
                log.warn("Exception while stopping the server", e);
            }
        }

        return true;
    }
}
