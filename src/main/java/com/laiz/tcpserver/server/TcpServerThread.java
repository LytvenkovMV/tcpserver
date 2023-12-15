package com.laiz.tcpserver.server;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class TcpServerThread implements Runnable {

    private static ServerSocket server;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private static int length = 12;

    public TcpServerThread(ServerSocket server) {
        this.server = server;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (socket == null) {
                    socket = server.accept();
                } else {
                    log.info("Client connected. Waiting for message...");
                    byte[] inputBytes = new byte[length];
                    dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                    for (int i = 0; i < length; i++) inputBytes[i] = dataInputStream.readByte();
                    String messageContent = new String(inputBytes);
                    log.info("Message received. Message content: " + messageContent);
                    dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                    dataOutputStream.writeBytes(" ===Server response: " + messageContent + "=== ");
                    dataOutputStream.flush();
                    log.info("Response sent. Waiting for new message...");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}

