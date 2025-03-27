package com.laiz.tcpserver.repository;

import com.laiz.tcpserver.entity.Packet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PacketRepository extends CrudRepository<Packet, Long> {

    Optional<Packet> findFirstByKpAddressAndTag(Byte kpAddress, Byte tag);
}
