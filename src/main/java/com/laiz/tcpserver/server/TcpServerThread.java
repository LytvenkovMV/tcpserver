package com.laiz.tcpserver.server;

import com.laiz.tcpserver.service.MessageService;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class TcpServerThread implements Runnable {

    private static ServerSocket server;
    private Socket socket;
    private final int length = 12;

    public TcpServerThread(ServerSocket server) {
        TcpServerThread.server = server;
    }

    @Override
    public void run() {

        while (true) {
            try {
                if (socket == null) {
                    socket = server.accept();
                    log.info("Client connected. Waiting for message...");
                    MessageService.add("Процесс обмена по TCP", "Клиент подключен. Сервер ждет сообщение...");
                } else {
                    byte[] inputBytes = new byte[length];
                    DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                    for (int i = 0; i < length; i++) inputBytes[i] = dataInputStream.readByte();
                    String messageContent = new String(inputBytes);
                    log.info("Message received. Message content: " + messageContent);
                    MessageService.add("Процесс обмена по TCP", "Сообщение получено. Его содержание: " + messageContent);
                    DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                    dataOutputStream.writeBytes(" === Server response: " + messageContent + " === ");
                    dataOutputStream.flush();
                    log.info("Response sent. Waiting for new message...");
                    MessageService.add("Процесс обмена по TCP", "Ответ отправлен. Сервер ждет новое сообщение...");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}

