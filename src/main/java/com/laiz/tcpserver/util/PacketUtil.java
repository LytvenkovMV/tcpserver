package com.laiz.tcpserver.util;

import com.laiz.tcpserver.entity.Packet;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PacketUtil {
    public Packet messageToPacket(byte[] message, byte start, byte end, String connNum) {
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
        packet.setConnNum(connNum);
        packet.setKpAddress(message[5]);
        packet.setTag(message[6]);
        packet.setData(message);

        return packet;
    }

    public byte[] packetToMessage(Packet packet) {
        return packet.getData();
    }

    public byte getOppositeTag(byte tag) {
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
