package com.laiz.tcpserver.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UiLogRecord {

    private String time;
    private String source;
    private String information;
}
