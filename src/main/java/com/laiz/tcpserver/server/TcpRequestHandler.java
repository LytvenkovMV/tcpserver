package com.laiz.tcpserver.server;

import com.laiz.tcpserver.enums.StateEnum;
import com.laiz.tcpserver.logger.AppLogger;
import com.laiz.tcpserver.service.PacketService;
import com.laiz.tcpserver.settings.AppSettings;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TcpRequestHandler {
    private final PacketService packetService;
    private final AppSettings settings;
    private final AppLogger logger;
    @Setter
    private volatile StateEnum threadState;

    public void handleRequest(Socket socket) {

        while (threadState == StateEnum.STARTED) {

            DataInputStream dataInputStream;
            DataOutputStream dataOutputStream;
            try {
                dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

                logger.clientConnected(Thread.currentThread().getName());
            } catch (IOException e) {
                log.warn("IOException was thrown during creating socket input/output streams", e);
                break;
            }

            byte[] message;
            try {
                message = receive(dataInputStream, settings.getStartByte(), settings.getEndByte());

                logger.messageReceived(message, settings.getMessageType(), Thread.currentThread().getName());
            } catch (IOException e) {
                log.warn("IOException was thrown during reading from socket", e);
                break;
            }

            List<byte[]> responses = packetService.saveMessageAndGetResponseList(message);

            responses.forEach(resp -> {
                try {
                    send(resp, dataOutputStream);

                    logger.messageSent(resp, settings.getMessageType(), Thread.currentThread().getName());
                } catch (IOException e) {
                    log.warn("Exception was thrown during writing to socket", e);
                }
            });
        }
    }

    private byte[] receive(DataInputStream dataInputStream, byte start, byte end) throws IOException {
        int length = 0;
        byte[] bytes = new byte[1000];
        boolean isStart = false;

        for (int i = 0; i < 1000; i++) {
            byte b = dataInputStream.readByte();

            if (b == start) isStart = true;

            if (isStart) {
                bytes[length] = b;
                length++;

                if (b == end) {
                    break;
                }
            }
        }

        return Arrays.copyOf(bytes, length);
    }

    private void send(byte[] response, DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.write(response);

        if (settings.isAddEnter()) {
            dataOutputStream.write(13);
            dataOutputStream.write(10);
        }
        dataOutputStream.flush();
    }
}

