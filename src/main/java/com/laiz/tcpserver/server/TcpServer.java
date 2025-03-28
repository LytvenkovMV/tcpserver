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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class TcpServer {
    private final ExecutorService requestHandlerExecutor;
    private final AppSettings settings;
    private final AppLogger logger;
    private final TcpRequestHandler tcpRequestHandler;
    private final List<Future<Boolean>> requestHandlerFutures = new CopyOnWriteArrayList<>();
    @Getter
    @Setter
    private volatile StateEnum serverState = StateEnum.STOPPED;
    private volatile Socket socket;

    public boolean runServer() {

        try (ServerSocket server = new ServerSocket(settings.getPort())) {
            server.setSoTimeout(settings.getSoTimeout());
            serverState = StateEnum.STARTED;
            long connNum = 1L;

            logger.serverStarted();

            while (serverState == StateEnum.STARTED) {
                try {
                    socket = server.accept();

                    String connNumStr = String.valueOf(connNum);
                    Future<Boolean> future = requestHandlerExecutor.submit(() -> tcpRequestHandler.run(socket, connNumStr));
                    requestHandlerFutures.add(future);
                    connNum++;
                } catch (SocketTimeoutException e) {
                    log.trace("Accept timed out.");
                }
            }
        } catch (IOException e) {
            log.warn("Exception while communication with client", e);
        } finally {
            try {
                requestHandlerFutures.forEach(f -> {
                    try {
                        f.get(10, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        log.warn("Exception while stopping the request handlers", e);
                    }
                });

                if (socket != null) socket.close();

                logger.serverStopped();
            } catch (Exception e) {
                log.warn("Exception while stopping the server", e);
            }
        }

        return true;
    }
}
