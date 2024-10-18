package com.laiz.tcpserver.server;

import com.laiz.tcpserver.enums.MessageTypeEnum;
import com.laiz.tcpserver.enums.StateEnum;
import com.laiz.tcpserver.service.MessageService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Slf4j
public class TcpRequestHandler {

    @Setter
    private static StateEnum threadState;
    @Setter
    private static MessageTypeEnum messageType;
    @Setter
    private static byte endByte;

    public static void handleRequest(Socket socket) {

        while (threadState == StateEnum.STARTED) {
            try {
                DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

                log.info("Client connected. Waiting for message...");
                MessageService.add("Процесс обмена по TCP", "Клиент подключен. Сервер ждет сообщение...");

                int messageLength = 0;
                byte[] inputBytes = new byte[1000];
                for (int i = 0; i < 1000; i++) {
                    byte inputByte = dataInputStream.readByte();
                    inputBytes[i] = inputByte;
                    if (inputByte == endByte) {
                        messageLength = i + 1;
                        break;
                    }
                }
                byte[] message = Arrays.copyOf(inputBytes, messageLength);

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

                log.info("Message received. Message content: " + messageContent);
                MessageService.add("Процесс обмена по TCP", "Сообщение получено. Его содержание: " + messageContent);

                dataOutputStream.write(message);
                dataOutputStream.flush();

                log.info("Response sent. Waiting for new message...");
                MessageService.add("Процесс обмена по TCP", "Ответ отправлен. Сервер ждет новое сообщение...");

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }

//        try {
//            if (socket != null) socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}

