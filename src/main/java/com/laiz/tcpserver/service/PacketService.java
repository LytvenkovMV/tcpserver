package com.laiz.tcpserver.service;

import com.laiz.tcpserver.entity.Packet;
import com.laiz.tcpserver.repository.PacketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PacketService {

    private static final byte START_BYTE = 0x68;
    private static final byte END_BYTE = 0x20;
    private final PacketRepository repository;

    @Transactional(propagation = Propagation.NEVER,
            isolation = Isolation.REPEATABLE_READ)
    public List<byte[]> saveMessageAndGetResponseList(byte[] message) {
        Packet packet = parseMessage(message);

        log.info("Input message parsed successfully...");
        UILogService.add("Соединение № " + Thread.currentThread().getId(), "Сообщение прочитано успешно");

        repository.save(packet);

        log.info("Input message saved...");
        UILogService.add("Соединение № " + Thread.currentThread().getId(), "Сообщение сохранено в БД");

        byte tagToTransmit;
        switch (packet.getTag()) {
            case 'M':
                tagToTransmit = 'C';
                break;
            case 'C':
                tagToTransmit = 'M';
                break;
            default:
                throw new IllegalArgumentException("Invalid message tag");
        }
        List<Packet> responsePackets = repository.findPacketsByKpAddressAndTag(packet.getKpAddress(), tagToTransmit);

        List<byte[]> responseMessages = new ArrayList<>();
        if (responsePackets != null) {
            responseMessages = responsePackets.stream()
                    .map(Packet::getData)
                    .collect(Collectors.toList());
            repository.deleteAll(responsePackets);
        }

        log.info("Found " + responseMessages.size() + " output messages");
        UILogService.add("Соединение № " + Thread.currentThread().getId(), "Найдено " + responseMessages.size() + " сообщений для ответа");

        return responseMessages;
    }

    private Packet parseMessage(byte[] message) {
        if (message[0] != START_BYTE || message[3] != START_BYTE) {
            throw new IllegalArgumentException("Invalid message start bytes");
        }
        if (message[message.length - 1] != END_BYTE) {
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
}