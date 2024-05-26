package com.laiz.tcpserver.server;

import com.laiz.tcpserver.enums.stateEnum;
import com.laiz.tcpserver.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class TcpServerThread {

    private static stateEnum threadState = stateEnum.STOPPED;

    public static void setThreadState(stateEnum threadState) {
        TcpServerThread.threadState = threadState;
    }

    @Async
    public void run(ServerSocket server) {

        Socket socket = null;
        DataInputStream dataInputStream = null;
        DataOutputStream dataOutputStream = null;

        while (threadState == stateEnum.STARTED) {
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
                        if (inputByte == 13) {
                            messageLength = i;
                            break;
                        }
                        else inputBytes[i] = inputByte;
                    }
                    byte[] message = Arrays.copyOf(inputBytes, messageLength);
                    String messageContent = new String(message, StandardCharsets.UTF_8);

                    log.info("Message received. Message content: " + messageContent);
                    MessageService.add("Процесс обмена по TCP", "Сообщение получено. Его содержание: " + messageContent);

                    dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                    dataOutputStream.writeBytes(" === Server response: " + messageContent + " === ");
                    dataOutputStream.write(13);
                    dataOutputStream.write(10);
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

