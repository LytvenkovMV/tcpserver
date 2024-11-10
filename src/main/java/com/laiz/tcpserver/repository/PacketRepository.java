package com.laiz.tcpserver.repository;

import com.laiz.tcpserver.entity.Packet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PacketRepository extends CrudRepository<Packet, Long> {

    List<Packet> findPacketsByKpAddressAndTag(Byte kpAddress, Byte tag);
}
