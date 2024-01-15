package com.laiz.tcpserver.server;

import com.laiz.tcpserver.service.MessageService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TcpServerThread {

    @Autowired
    private TcpServer tcpServer;

    private Socket socket;
    private static boolean isRunning = false;

    public static void setIsRunning(boolean isRunning) {
        TcpServerThread.isRunning = isRunning;
    }

    @Async
    public void run() {

        while (isRunning) {
            try {
                if (socket == null) {
                    socket = tcpServer.getServer().accept();
                    log.info("Client connected. Waiting for message...");
                    MessageService.add("Процесс обмена по TCP", "Клиент подключен. Сервер ждет сообщение...");
                } else {
                    DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                    List<Byte> inputBytesList = new ArrayList<>();
                    for (int i = 0; i < 1000; i++) {
                        byte inputByte = dataInputStream.readByte();
                        if (inputByte == 13) break;
                        else inputBytesList.add(inputByte);
                    }

                    int messageLength = inputBytesList.size();
                    byte[] byteArray = new byte[messageLength];
                    for (int i = 0; i < messageLength; i++) byteArray[i] = inputBytesList.get(i);
                    String messageContent = new String(byteArray, StandardCharsets.UTF_8);

                    log.info("Message received. Message content: " + messageContent);
                    MessageService.add("Процесс обмена по TCP", "Сообщение получено. Его содержание: " + messageContent);

                    DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                    dataOutputStream.writeBytes(" === Server response: " + messageContent + " === ");
                    dataOutputStream.write(13);
                    dataOutputStream.write(10);
                    dataOutputStream.flush();

                    log.info("Response sent. Waiting for new message...");
                    MessageService.add("Процесс обмена по TCP", "Ответ отправлен. Сервер ждет новое сообщение...");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        return;
    }
}

