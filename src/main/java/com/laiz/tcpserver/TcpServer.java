package com.laiz.tcpserver;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

@Component
@Slf4j
@NoArgsConstructor
@Data
public class TcpServer {

    private static ServerSocket server;
    private static Socket socket;
    private static DataInputStream dataInputStream;
    private static DataOutputStream dataOutputStream;
    private static boolean isStarted = false;
    private static int port = 2404;
    private static int length = 12;

    public void start() throws IOException {

        if (!isStarted) {

            server = new ServerSocket(port);
            isStarted = true;

            while (isStarted) {

                log.info("Server started. Waiting for the client connection...");

                socket = server.accept();

                log.info("Client connected. Waiting for message...");

                while (socket.isConnected()) {

                    byte[] inputBytes = new byte[length];
                    dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                    for (int i = 0; i < length; i++) {
                        inputBytes[i] = dataInputStream.readByte();
                    }

                    String messageContent = new String(inputBytes);

                    log.info("Message received. Message content: " + messageContent);

                    dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                    dataOutputStream.writeBytes(" ===Server response:" + messageContent + "=== ");
                    dataOutputStream.flush();

                    log.info("Response sent. Waiting for new message...");
                }
            }
        }
    }

    public void stop() throws IOException {
        if (isStarted) {
            if (dataInputStream != null) dataInputStream.close();
            if (dataOutputStream != null) dataOutputStream.close();
            if (socket != null) socket.close();
            if (server != null) {
                server.close();
                isStarted = false;
                log.info("Server stopped");
            }
        }
    }
}
