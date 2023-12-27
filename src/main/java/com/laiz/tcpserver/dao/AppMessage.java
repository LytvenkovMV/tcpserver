package com.laiz.tcpserver.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppMessage {

    private Integer time;
    private String source;
    private String information;
}
