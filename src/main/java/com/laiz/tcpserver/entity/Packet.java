package com.laiz.tcpserver.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "packets")
@Getter
@Setter
public class Packet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String connNum;

    byte kpAddress;

    byte tag;

    byte[] data;

    long optLock;
}
