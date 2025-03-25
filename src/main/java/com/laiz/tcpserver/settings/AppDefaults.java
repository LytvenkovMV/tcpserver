package com.laiz.tcpserver.settings;

import com.laiz.tcpserver.enums.MessageTypeEnum;

public class AppDefaults {
    public static final int TCP_REQUEST_HANDLER_THREADS_NUMBER = 32;
    public static final int SO_TIMEOUT = 3000;
    public static final int PORT = 2404;
    public static final byte START_BYTE = 0x68;
    public static final byte END_BYTE = 0x13;
    public static final boolean IS_ADD_ENTER = false;
    public static final MessageTypeEnum MESSAGE_TYPE = MessageTypeEnum.STRING;
}
