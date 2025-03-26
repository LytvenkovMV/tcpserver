package com.laiz.tcpserver.logger;

import com.laiz.tcpserver.enums.MessageTypeEnum;
import com.laiz.tcpserver.service.UILogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class AppLogger {
    public void serverStarting() {
        log.info("Starting server...");
        UILogService.add("TCP сервер", "Запускается...");
    }

    public void serverStopping() {
        log.info("Stopping server...");
        UILogService.add("TCP сервер", "Останавливается...");
    }

    public void serverStarted() {
        log.info("Server started. Waiting for the client connection...");
        UILogService.add("TCP сервер", "Запущен. Ждет подключения клиента...");
    }

    public void serverStopped() {
        log.info("Server stopped");
        UILogService.add("TCP сервер", "Остановлен");
    }

    public void clientConnected(String threadName) {
        String threadNum = threadName.split("-")[3];

        log.info("Client connected. Waiting for message...");
        UILogService.add("Соединение № " + threadNum, "Клиент подключен. Сервер ждет сообщение...");
    }

    public void messageReceived(byte[] message, MessageTypeEnum messageType, String threadName) {
        String threadNum = threadName.split("-")[3];
        String content = messageToString(message, messageType);

        log.info("Message received. Message content: " + content);
        UILogService.add("Соединение № " + threadNum, "Сообщение получено. Его содержание: " + content);
    }

    public void messageSent(byte[] message, MessageTypeEnum messageType, String threadName) {
        String threadNum = threadName.split("-")[3];
        String content = messageToString(message, messageType);

        log.info("Response sent. Response content: " + content);
        UILogService.add("Соединение № " + threadNum, "Ответ отправлен. Его содержание: " + content);
    }

    public void inputMessageParsed(String threadName) {
        String threadNum = threadName.split("-")[3];

        log.info("Input message parsed successfully");
        UILogService.add("Соединение № " + threadNum, "Сообщение прочитано успешно");
    }

    public void inputMessageSaved(String threadName) {
        String threadNum = threadName.split("-")[3];

        log.info("Input message saved in DB");
        UILogService.add("Соединение № " + threadNum, "Сообщение сохранено в БД");
    }

    public void responseFound(byte[] message, MessageTypeEnum messageType, String threadName) {
        String threadNum = threadName.split("-")[3];
        String content = messageToString(message, messageType);

        log.info("Response found in DB. Response content: " + content);
        UILogService.add("Соединение № " + threadNum, "Ответ найден в БД. Его содержание: " + content);
    }

    private String messageToString(byte[] message, MessageTypeEnum messageType) {
        switch (messageType) {
            case BYTES:
                StringBuilder stringBuilder = new StringBuilder();

                for (byte messageByte : message) {
                    String strByte = String.format("%02X ", messageByte);
                    stringBuilder.append(strByte);
                }

                return stringBuilder.toString().trim();
            case STRING:
                return new String(message, StandardCharsets.UTF_8);
            default:
                return "";
        }
    }
}
