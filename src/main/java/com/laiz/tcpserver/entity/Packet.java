package com.laiz.tcpserver.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "packets")
@Getter
@Setter
public class Packet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "kp_address")
    byte kpAddress;

    @Column(name = "tag")
    byte tag;

    @Column(name = "data")
    byte[] data;

    @Version
    @Column(name = "opt_lock")
    long version;
}
