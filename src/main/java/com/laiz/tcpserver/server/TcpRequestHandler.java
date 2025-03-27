package com.laiz.tcpserver.server;

import com.laiz.tcpserver.logger.AppLogger;
import com.laiz.tcpserver.service.PacketService;
import com.laiz.tcpserver.settings.AppSettings;
import lombok.RequiredArgsConstructor;
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

    public boolean run(Socket socket) {
        String threadNum = logger.getThreadNum(Thread.currentThread());
        logger.connectionOpened(threadNum);

        byte[] message;
        try (DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
             DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {
            message = receive(dataInputStream, settings.getStartByte(), settings.getEndByte());
            logger.messageReceived(message, settings.getMessageType(), threadNum);

            List<byte[]> responses = packetService.saveMessageAndGetResponseList(message);

            responses.forEach(resp -> {
                try {
                    send(resp, dataOutputStream);

                    logger.messageSent(resp, settings.getMessageType(), threadNum);
                } catch (IOException e) {
                    log.warn("Connection #{}: Exception was thrown during writing to socket", threadNum, e);
                }
            });

            logger.connectionClosed(threadNum);
        } catch (IOException e) {
            log.warn("Connection #{}: Exception was thrown during communication", threadNum, e);
        }

        return true;
    }

    private byte[] receive(DataInputStream dataInputStream, byte start, byte end) throws IOException {
        int length = 0;
        byte[] buffer = new byte[1000];
        boolean isStart = false;

        for (int i = 0; i < 1000; i++) {
            byte b = dataInputStream.readByte();

            if (b == start) isStart = true;

            if (isStart) {
                buffer[length] = b;
                length++;

                if (b == end) {
                    break;
                }
            }
        }

        return Arrays.copyOf(buffer, length);
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

