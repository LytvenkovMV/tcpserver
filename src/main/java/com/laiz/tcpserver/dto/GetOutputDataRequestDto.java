package com.laiz.tcpserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetOutputDataRequestDto {

    private Integer time;
    private String source;
    private String message;
}
