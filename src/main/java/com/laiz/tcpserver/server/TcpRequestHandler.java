package com.laiz.tcpserver.server;

import com.laiz.tcpserver.enums.MessageTypeEnum;
import com.laiz.tcpserver.enums.StateEnum;
import com.laiz.tcpserver.service.PacketService;
import com.laiz.tcpserver.service.UILogService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TcpRequestHandler {

    private final PacketService packetService;
    private static final byte START_BYTE = 0x68;
    @Setter
    private static StateEnum threadState;
    @Setter
    private static MessageTypeEnum messageType;
    @Setter
    private static byte endByte;

    public void handleRequest(Socket socket) {

        while (threadState == StateEnum.STARTED) {

            DataInputStream dataInputStream;
            DataOutputStream dataOutputStream;
            try {
                dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            } catch (IOException e) {
                log.warn("IOException was thrown during creating socket input/output streams");
                break;
            }

            log.info("Client connected. Waiting for message...");
            UILogService.add("Соединение № " + Thread.currentThread().getId(), "Клиент подключен. Сервер ждет сообщение...");

            int messageLength = 0;
            byte[] inputBytes = new byte[1000];
            boolean isStart = false;

            try {
                for (int i = 0; i < 1000; i++) {
                    byte inputByte = dataInputStream.readByte();
                    if (inputByte == START_BYTE) isStart = true;
                    if (isStart) {
                        inputBytes[messageLength] = inputByte;
                        messageLength++;
                        if (inputByte == endByte) break;
                    }
                }
            } catch (Exception e) {
                log.warn("IOException was thrown during reading from socket");
                break;
            }

            byte[] message = Arrays.copyOf(inputBytes, messageLength);

            String rxMessageContent = getMessageContent(message);
            log.info("Message received. Message content: " + rxMessageContent);
            UILogService.add("Соединение № " + Thread.currentThread().getId(), "Сообщение получено. Его содержание: " + rxMessageContent);

            List<byte[]> responseMessages = packetService.saveMessageAndGetResponseList(message);

            if (!responseMessages.isEmpty()) {
                try {
                    for (byte[] m : responseMessages) {
                        dataOutputStream.write(m);
                        dataOutputStream.flush();

                        String txMessageContent = getMessageContent(message);
                        log.info("Response sent. Response content: " + txMessageContent);
                        UILogService.add("Соединение № " + Thread.currentThread().getId(), "Ответ отправлен. Его содержание: " + txMessageContent);
                    }
                } catch (Exception e) {
                    log.warn("IOException was thrown during writing to socket");
                    break;
                }
            }
        }
    }

    @NotNull
    private static String getMessageContent(byte[] message) {
        String messageContent = "";
        switch (messageType) {
            case BYTES:
                StringBuilder stringBuilder = new StringBuilder();
                for (byte messageByte : message) {
                    String strByte = String.format("%02X ", messageByte);
                    stringBuilder.append(strByte);
                }
                messageContent = stringBuilder.toString().trim();
                break;
            case STRING:
                messageContent = new String(message, StandardCharsets.UTF_8);
        }
        return messageContent;
    }
}

