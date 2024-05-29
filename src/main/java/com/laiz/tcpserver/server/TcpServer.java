package com.laiz.tcpserver.server;

import com.laiz.tcpserver.enums.CmdEnum;
import com.laiz.tcpserver.enums.MessageTypeEnum;
import com.laiz.tcpserver.enums.StateEnum;
import com.laiz.tcpserver.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Optional;

@Slf4j
@Service
public class TcpServer {

    @Autowired
    private TcpServerThread tcpServerThread;

    private ServerSocket server;

    private StateEnum serverState;

    private CmdEnum startCmd;
    private CmdEnum stopCmd;

    Optional<String> userPort;
    Optional<String> userMessageType;
    Optional<String> userEndByte;
    Optional<String> userAddEnter;

    public void start(Optional<String> port, Optional<String> messageType, Optional<String> endByte, Optional<String> addEnter) {
        if (serverState != StateEnum.STARTED) {

            this.userPort = port;
            this.userMessageType = messageType;
            this.userEndByte = endByte;
            this.userAddEnter = addEnter;

            /* Activate START command */
            startCmd = CmdEnum.ACTIVE;

            log.info("Starting server...");
            MessageService.add("TCP сервер", "Запускается...");
        }
    }

    public void stop() {
        if (serverState != StateEnum.STOPPED) {

            /* Activate STOP command */
            stopCmd = CmdEnum.ACTIVE;

            log.info("Stopping server...");
            MessageService.add("TCP сервер", "Останавливается...");
        }
    }

    @Scheduled(fixedDelay = 100)
    public void handleCommands() {

        /* Handle START command */
        if (startCmd == CmdEnum.ACTIVE) {
            try {
                /* Create TCP server */
                int port = getPort();
                server = new ServerSocket(port);

                /* Launch TCP server thread */
                MessageTypeEnum messageType = getMessageType();
                byte endByte = getEndByte();
                boolean isAddEnter = getAddEnter();
                tcpServerThread.run(server, messageType, endByte, isAddEnter);

                /* Change server state and reset command */
                serverState = StateEnum.STARTED;
                startCmd = CmdEnum.NOT_ACTIVE;

                log.info("Server started. Waiting for the client connection...");
                MessageService.add("TCP сервер", "Запущен. Ждет подключения клиента...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /* Handle STOP command */
        if (stopCmd == CmdEnum.ACTIVE) {
            try {
                /* Terminate TCP server thread */
                tcpServerThread.stop();

                /* Wait */
                Thread.sleep(3000);

                /* Close TCP server */
                server.close();

                /* Change server state and reset command */
                serverState = StateEnum.STOPPED;
                stopCmd = CmdEnum.NOT_ACTIVE;

                log.info("Server stopped");
                MessageService.add("TCP сервер", "Остановлен");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int getPort() {
        int port = 2404;
        if (userPort.isPresent()) {
            try {
                port = Integer.parseInt(userPort.get());
            } catch (Exception e) {
            }
        }
        return port;
    }

    private MessageTypeEnum getMessageType() {
        MessageTypeEnum messageType = MessageTypeEnum.BYTES;
        if (userMessageType.isPresent()) {
            if (userMessageType.get().equals("string")) messageType = MessageTypeEnum.STRING;
            else if (userMessageType.get().equals("bytes")) messageType = MessageTypeEnum.BYTES;
        }
        return messageType;
    }

    private byte getEndByte() {
        byte endByte = Byte.parseByte("13");
        if (userEndByte.isPresent()) {
            try {
                endByte = Byte.parseByte(userEndByte.get());
            } catch (Exception e) {
            }
        }
        return endByte;
    }

    private boolean getAddEnter() {
        boolean isAddEnter = false;
        if (userAddEnter.isPresent()) {
            if (userAddEnter.get().equals("1")) isAddEnter = true;
        }
        return isAddEnter;
    }
}

