package com.laiz.tcpserver.repository;

import com.laiz.tcpserver.entity.Packet;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface PacketRepository extends CrudRepository<Packet, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Packet> findFirstByKpAddressAndTag(Byte kpAddress, Byte tag);
}
