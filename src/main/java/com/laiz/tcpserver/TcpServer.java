package com.laiz.tcpserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

@Component
@Slf4j
public class TcpServer {

    private static ServerSocket server;

    private static int port = 2404;

    private static int length = 12;

    public void start() throws IOException {

        server = new ServerSocket(port);

        while (true) {

            log.info("Server started. Waiting for the client connection...");

            Socket socket = server.accept();

            log.info("Client connected. Waiting for message...");

            while (true) {

                byte[] inputBytes = new byte[length];
                DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                for (int i = 0; i < length; i++) {
                    inputBytes[i] = dataInputStream.readByte();
                }

                String messageContent = new String(inputBytes);

                log.info("Message received. Message content: " + messageContent);

                DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                dataOutputStream.writeBytes(" ===Server response:" + messageContent + "=== ");
                dataOutputStream.flush();

                log.info("Response sent. Waiting for new message...");
            }
        }
    }

    public void stop() throws IOException {
        server.close();
    }
}
