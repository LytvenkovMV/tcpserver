package com.laiz.tcpserver.service;

import com.laiz.tcpserver.entity.Packet;
import com.laiz.tcpserver.logger.AppLogger;
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
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PacketService {
    private final AppSettings settings;
    private final AppLogger logger;
    private final PacketRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW,
            isolation = Isolation.REPEATABLE_READ)
    @Retryable(maxAttempts = 5,
            recover = "recoverMethod")
    @Lock(LockModeType.OPTIMISTIC)
    public List<byte[]> saveMessageAndGetResponseList(byte[] message) {
        String threadNum = logger.getThreadNum(Thread.currentThread());

        int retryCount = RetrySynchronizationManager.getContext().getRetryCount();
        if (retryCount > 0) {
            log.warn("Connection #{}: Retry after transaction rollback. Attempt #{}", threadNum, retryCount + 1);
        }

        Packet packet = messageToPacket(message, settings.getStartByte(), settings.getEndByte());
        logger.inputMessageParsed(threadNum);

        repository.save(packet);
        logger.inputMessageSaved(threadNum);

        List<Packet> responsePackets = repository.findPacketsByKpAddressAndTag(packet.getKpAddress(), getOppositeTag(packet.getTag()));

        List<byte[]> responseMessages = responsePackets.stream()
                .map(Packet::getData)
                .peek(resp -> logger.responseFound(resp, settings.getMessageType(), threadNum))
                .collect(Collectors.toList());

        repository.deleteAll(responsePackets);

        return responseMessages;
    }

    @Recover
    public List<byte[]> recoverMethod(RuntimeException e, byte[] message) {
        log.error("Connection #{}: Retrying finished unsuccessfully with reason: {}", logger.getThreadNum(Thread.currentThread()), e.getMessage(), e);

        return new ArrayList<>();
    }

    private Packet messageToPacket(byte[] message, byte start, byte end) {
        if (Objects.isNull(message)) {
            throw new IllegalArgumentException("Cannot parse input message. Message is empty");
        }
        if (message.length < 9) {
            throw new IllegalArgumentException("Cannot parse input message. Invalid message length");
        }
        if (message[0] != start || message[3] != start) {
            throw new IllegalArgumentException("Cannot parse input message. Invalid start bytes");
        }
        if (message[message.length - 1] != end) {
            throw new IllegalArgumentException("Cannot parse input message. Invalid end byte");
        }
        if (!(message[6] == 'C' || message[6] == 'M')) {
            throw new IllegalArgumentException("Cannot parse input message. Invalid tag");
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

