package com.laiz.tcpserver.server;

import com.laiz.tcpserver.enums.MessageTypeEnum;
import com.laiz.tcpserver.enums.StateEnum;
import com.laiz.tcpserver.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Slf4j
@Component
public class TcpServerThread {

    private static StateEnum threadState;
    private static MessageTypeEnum messageType;

    private static byte endByte;

    public static void setThreadState(StateEnum threadState) {
        TcpServerThread.threadState = threadState;
    }

    public static void setMessageType(MessageTypeEnum messageType) {
        TcpServerThread.messageType = messageType;
    }

    public static void setEndByte(byte endByte) {
        TcpServerThread.endByte = endByte;
    }

    @Async
    public void run(ServerSocket server) {

        Socket socket = null;
        DataInputStream dataInputStream = null;
        DataOutputStream dataOutputStream = null;

        while (threadState == StateEnum.STARTED) {
            try {
                if (socket == null) {
                    socket = server.accept();
                    log.info("Client connected. Waiting for message...");
                    MessageService.add("Процесс обмена по TCP", "Клиент подключен. Сервер ждет сообщение...");
                } else {
                    dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
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
                                stringBuilder.append(Integer.toString(messageByte, 16));
                            }
                            messageContent = stringBuilder.toString();
                            break;
                        case STRING:
                            messageContent = new String(message, StandardCharsets.UTF_8);
                    }

                    log.info("Message received. Message content: " + messageContent);
                    MessageService.add("Процесс обмена по TCP", "Сообщение получено. Его содержание: " + messageContent);

                    dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                    dataOutputStream.write(message);
//                    dataOutputStream.write(13);
//                    dataOutputStream.write(10);
                    dataOutputStream.flush();

                    log.info("Response sent. Waiting for new message...");
                    MessageService.add("Процесс обмена по TCP", "Ответ отправлен. Сервер ждет новое сообщение...");
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }

        try {
            if (dataInputStream != null) dataInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (dataOutputStream != null) dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

