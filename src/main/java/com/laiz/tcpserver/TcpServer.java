package com.laiz.tcpserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class TcpServer {

    private static ServerSocket server;

    private static int port = 2404;

    private static int length = 5;

    public void start() throws IOException, InterruptedException {

        server = new ServerSocket(port);

        while (true) {

            log.info("Waiting for the client connection...");

            Socket socket = server.accept();

            log.info("Client connected");

            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            byte[] bytes = new byte[length];
            for (int i = 0; i < length; i++) {
                bytes[i] = dataInputStream.readByte();
            }

            log.info("Message received");

            String messageContent = new String(bytes);
            System.out.println("MESSAGE CONTENT: " + messageContent);

            DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            dataOutputStream.writeBytes("Your message: " + messageContent);

            log.info("Response sent");

            TimeUnit.SECONDS.sleep(5);

            dataInputStream.close();


//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!Exception
            /////////dataOutputStream.close();




            socket.close();

            log.info("Connection closed");
        }
    }
}
