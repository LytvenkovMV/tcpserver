package com.laiz.tcpserver.settings;

import com.laiz.tcpserver.enums.MessageTypeEnum;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Getter
@Component
public class AppSettings {
    private final int soTimeout = AppDefaults.SO_TIMEOUT;
    private final byte startByte = AppDefaults.START_BYTE;
    private int port = AppDefaults.PORT;
    private byte endByte = AppDefaults.END_BYTE;
    private boolean isAddEnter = AppDefaults.IS_ADD_ENTER;
    private MessageTypeEnum messageType = AppDefaults.MESSAGE_TYPE;

    public void initUserPort(String port) {
        try {
            this.port = Integer.parseInt(port);
        } catch (Exception e) {
            log.info("Cannot parse user port. Using default value");
        }
    }

    public void initMessageType(String messageType) {
        if (Objects.isNull(messageType)) return;

        if (messageType.equals("string")) {
            this.messageType = MessageTypeEnum.STRING;
        } else if (messageType.equals("bytes")) {
            this.messageType = MessageTypeEnum.BYTES;
        } else {
            log.info("Cannot parse user message type. Using default value");
        }
    }

    public void initEndByte(String endByte) {
        try {
            this.endByte = Byte.parseByte(endByte);
        } catch (Exception e) {
            log.info("Cannot parse user end of message byte. Using default value");
        }
    }

    public void initAddEnter(String isAddEnter) {
        if (isAddEnter.equals("1")) {
            this.isAddEnter = true;
        } else if (isAddEnter.equals("0")) {
            this.isAddEnter = false;
        } else {
            log.info("Cannot parse user add enter strategy. Using default value");
        }
    }
}
