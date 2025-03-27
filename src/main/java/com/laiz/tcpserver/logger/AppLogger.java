package com.laiz.tcpserver.logger;

import com.laiz.tcpserver.enums.MessageTypeEnum;
import com.laiz.tcpserver.service.UILogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class AppLogger {
    public String getThreadNum(Thread currThread) {
        return currThread.getName().split("-")[3];
    }

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

    public void connectionOpened(String threadNum) {
        log.info("Connection #{} opened. Client connected. Waiting for message...", threadNum);
        UILogService.add("Соединение № " + threadNum, "Клиент подключен. Сервер ждет сообщение...");
    }

    public void connectionClosed(String threadNum) {
        log.info("Connection #{} closed", threadNum);
        UILogService.add("Соединение № " + threadNum, "Соединение закрыто");
    }

    public void messageReceived(byte[] message, MessageTypeEnum messageType, String threadNum) {
        String content = messageToString(message, messageType);

        log.info("Connection #{}: Message received. Message content: {}", threadNum, content);
        UILogService.add("Соединение № " + threadNum, "Сообщение получено. Его содержание: " + content);
    }

    public void messageSent(byte[] message, MessageTypeEnum messageType, String threadNum) {
        String content = messageToString(message, messageType);

        log.info("Connection #{}: Response sent. Response content: {}", threadNum, content);
        UILogService.add("Соединение № " + threadNum, "Ответ отправлен. Его содержание: " + content);
    }

    public void inputMessageParsed(String threadNum) {
        log.info("Connection #{}: Input message parsed successfully", threadNum);
        UILogService.add("Соединение № " + threadNum, "Сообщение прочитано успешно");
    }

    public void inputMessageSaved(String threadNum) {
        log.info("Connection #{}: Input message saved in DB", threadNum);
        UILogService.add("Соединение № " + threadNum, "Сообщение сохранено в БД");
    }

    public void responseFound(byte[] message, MessageTypeEnum messageType, String threadNum) {
        String content = messageToString(message, messageType);

        log.info("Connection #{}: Response found in DB. Response content: {}", threadNum, content);
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
