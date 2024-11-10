package com.laiz.tcpserver.service;

import com.laiz.tcpserver.entity.Packet;
import com.laiz.tcpserver.repository.PacketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PacketService {

    private final PacketRepository repository;

    @Transactional(propagation = Propagation.NEVER,
            isolation = Isolation.REPEATABLE_READ)
    public List<byte[]> saveMessageAndGetResponseList(byte[] message) {
        Packet packet = parseMessage(message);
        repository.save(packet);

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

        return responseMessages;
    }

    private Packet parseMessage(byte[] message) {
        if (message[0] != 0x68 || message[3] != 0x68) {
            throw new IllegalArgumentException("Invalid message start bytes");
        }
        if (message[message.length - 1] != 0x20) {
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
