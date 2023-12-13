package com.laiz.tcpserver;

import com.laiz.tcpserver.enums.TcpServerCmd;
import com.laiz.tcpserver.enums.TcpServerState;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public final class TcpServer {

    private static ServerSocket server;
    private static Socket socket;
    private static DataInputStream dataInputStream;
    private static DataOutputStream dataOutputStream;
    private static TcpServerState serverState = TcpServerState.STOPPED;
    private static TcpServerCmd startCmd = TcpServerCmd.NOT_ACTIVE;
    private static TcpServerCmd stopCmd = TcpServerCmd.NOT_ACTIVE;
    private static int port = 2404;
    private static int length = 12;

    public static void start() {
        startCmd = TcpServerCmd.ACTIVE;
    }

    public static void stop() {
        stopCmd = TcpServerCmd.ACTIVE;
    }

    public static void runThread() {
        while (true) {
            switch (serverState) {
                case STOPPED:
                    if (startCmd == TcpServerCmd.ACTIVE) {
                        try {
                            server = new ServerSocket(port);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        serverState = TcpServerState.STARTED;
                        startCmd = TcpServerCmd.NOT_ACTIVE;
                        log.info("Server started. Waiting for the client connection...");
                    }
                    break;

                case STARTED:
                    if (stopCmd == TcpServerCmd.ACTIVE) {
                        try {
                            dataInputStream.close();
                            dataOutputStream.close();
                            socket.close();
                            server.close();
                            serverState = TcpServerState.STOPPED;
                            stopCmd = TcpServerCmd.NOT_ACTIVE;
                            log.info("Server stopped");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            socket = server.accept();
                            log.info("Client connected. Waiting for message...");

                            while (socket.isConnected()) {
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
                        }
                    }
                    break;
            }
        }
    }


//    public static void start() throws IOException {
//
//        if (!isStarted) {
//
//            server = new ServerSocket(port);
//            isStarted = true;
//
//            while (isStarted) {
//
//                log.info("Server started. Waiting for the client connection...");
//
//                socket = server.accept();
//
//                log.info("Client connected. Waiting for message...");
//
//                while (socket.isConnected()) {
//
//                    byte[] inputBytes = new byte[length];
//                    dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
//                    for (int i = 0; i < length; i++) {
//                        inputBytes[i] = dataInputStream.readByte();
//                    }
//
//                    String messageContent = new String(inputBytes);
//
//                    log.info("Message received. Message content: " + messageContent);
//
//                    dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
//                    dataOutputStream.writeBytes(" ===Server response:" + messageContent + "=== ");
//                    dataOutputStream.flush();
//
//                    log.info("Response sent. Waiting for new message...");
//                }
//            }
//        }
//    }
//
//    public static void stop123() throws IOException {
//        if (isStarted) {
//            if (dataInputStream != null) dataInputStream.close();
//            if (dataOutputStream != null) dataOutputStream.close();
//            if (socket != null) socket.close();
//            if (server != null) {
//                server.close();
//                isStarted = false;
//                log.info("Server stopped");
//            }
//        }
//    }
}
