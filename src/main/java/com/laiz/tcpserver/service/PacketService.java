package com.laiz.tcpserver.service;

import com.laiz.tcpserver.entity.Packet;
import com.laiz.tcpserver.repository.PacketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PacketService {
    private final PacketRepository repository;

    public void save(Packet packet) {
        repository.save(packet);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW,
            isolation = Isolation.READ_COMMITTED)
    @Retryable(maxAttempts = 8,
            backoff = @Backoff(delay = 1000, maxDelay = 10000))
    public Optional<Packet> findFirst(byte kpAddress, byte tag, String connNum) {
        int retryCount = RetrySynchronizationManager.getContext().getRetryCount();

        if (retryCount > 0) {
            log.warn("Connection #{}: Retry find responses. Attempt #{}", connNum, retryCount + 1);
        }

        Packet response = repository.findFirstByKpAddressAndTag(kpAddress, tag)
                .orElseThrow(() -> new RuntimeException("Response not found in DB"));

        repository.delete(response);

        return Optional.of(response);
    }

    @Recover
    public Optional<Packet> findFirstRecover(RuntimeException e, byte kpAddress, byte tag, String connNum) {
        log.error("Connection #{}: Find responses retrying finished unsuccessfully with reason: {}", connNum, e.getMessage(), e);

        return Optional.empty();
    }
}

