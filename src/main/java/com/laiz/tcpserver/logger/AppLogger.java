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
        log.debug("Starting server...");
        UILogService.add("TCP сервер", "Запускается...");
    }

    public void serverStopping() {
        log.debug("Stopping server...");
        UILogService.add("TCP сервер", "Останавливается...");
    }

    public void serverStarted() {
        log.debug("Server started. Waiting for the client connection...");
        UILogService.add("TCP сервер", "Запущен. Ждет подключения клиента...");
    }

    public void serverStopped() {
        log.debug("Server stopped");
        UILogService.add("TCP сервер", "Остановлен");
    }

    public void connectionOpened(String connNum) {
        log.debug("Connection #{} opened. Client connected. Waiting for message...", connNum);
        UILogService.add("Соединение № " + connNum, "Клиент подключен. Сервер ждет сообщение...");
    }

    public void connectionClosed(String connNum) {
        log.debug("Connection #{} closed", connNum);
        UILogService.add("Соединение № " + connNum, "Соединение закрыто");
    }

    public void messageReceived(byte[] message, MessageTypeEnum messageType, String connNum) {
        String content = messageToString(message, messageType);

        log.debug("Connection #{}: Message received. Message content: {}", connNum, content);
        UILogService.add("Соединение № " + connNum, "Сообщение получено. Его содержание: " + content);
    }

    public void messageSent(byte[] message, MessageTypeEnum messageType, String connNum) {
        String content = messageToString(message, messageType);

        log.debug("Connection #{}: Response sent. Response content: {}", connNum, content);
        UILogService.add("Соединение № " + connNum, "Ответ отправлен. Его содержание: " + content);
    }

    public void inputMessageParsed(String connNum) {
        log.debug("Connection #{}: Input message parsed successfully", connNum);
        UILogService.add("Соединение № " + connNum, "Сообщение прочитано успешно");
    }

    public void inputMessageSaved(String connNum) {
        log.debug("Connection #{}: Input message saved in DB", connNum);
        UILogService.add("Соединение № " + connNum, "Сообщение сохранено в БД");
    }

    public void responseFound(byte[] message, MessageTypeEnum messageType, String connNum) {
        String content = messageToString(message, messageType);

        log.debug("Connection #{}: Response found in DB. Response content: {}", connNum, content);
        UILogService.add("Соединение № " + connNum, "Ответ найден в БД. Его содержание: " + content);
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
