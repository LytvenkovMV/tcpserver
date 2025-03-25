package com.laiz.tcpserver.service;

import com.laiz.tcpserver.entity.Packet;
import com.laiz.tcpserver.repository.PacketRepository;
import com.laiz.tcpserver.settings.AppSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PacketService {
    private final AppSettings appSettings;
    private final PacketRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW,
            isolation = Isolation.REPEATABLE_READ)
    @Retryable(maxAttempts = 5,
            recover = "recoverMethod")
    @Lock(LockModeType.OPTIMISTIC)
    public List<byte[]> saveMessageAndGetResponseList(byte[] message) {
        int retryCount = RetrySynchronizationManager.getContext().getRetryCount();
        if (retryCount > 0) {
            log.warn("Retry after transaction rollback. Attempt #{}", RetrySynchronizationManager.getContext().getRetryCount() + 1);
        }

        Packet packet = parseMessage(message);

        log.info("Input message parsed successfully");
        UILogService.add("Соединение № " + Thread.currentThread().getId(), "Сообщение прочитано успешно");

        repository.save(packet);

        log.info("Input message saved");
        UILogService.add("Соединение № " + Thread.currentThread().getId(), "Сообщение сохранено в БД");

        List<Packet> responsePackets = repository.findPacketsByKpAddressAndTag(packet.getKpAddress(), getOppositeTag(packet.getTag()));
        List<byte[]> responseMessages = new ArrayList<>();
        if (responsePackets != null && (!responsePackets.isEmpty())) {
            responseMessages = responsePackets.stream()
                    .map(Packet::getData)
                    .collect(Collectors.toList());
            repository.deleteAll(responsePackets);
        }

        log.info("Found " + responseMessages.size() + " output messages");
        UILogService.add("Соединение № " + Thread.currentThread().getId(), "Найдено " + responseMessages.size() + " сообщений для ответа");

        return responseMessages;
    }

    @Recover
    public List<byte[]> recoverMethod(RuntimeException e, byte[] message) {
        log.warn("Retrying finished unsuccessfully. Begin recovering");

        return new ArrayList<>();
    }

    private Packet parseMessage(byte[] message) {
        byte startByte = appSettings.getStartByte();
        byte endByte = appSettings.getEndByte();

        if (message[0] != startByte || message[3] != startByte) {
            throw new IllegalArgumentException("Invalid message start bytes");
        }
        if (message[message.length - 1] != endByte) {
            throw new IllegalArgumentException("Invalid message end byte");
        }
        if (!(message[6] == 'C' || message[6] == 'M')) {
            throw new IllegalArgumentException("Invalid message tag");
        }

        Packet packet = new Packet();
        packet.setKpAddress(message[5]);
        packet.setTag(message[6]);
        packet.setData(message);

        return packet;
    }

    private byte getOppositeTag(byte tag) {
        switch (tag) {
            case 'M':
                return 'C';
            case 'C':
                return 'M';
            default:
                throw new IllegalArgumentException("Invalid message tag");
        }
    }
}

