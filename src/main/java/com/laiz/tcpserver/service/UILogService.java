package com.laiz.tcpserver.service;

import com.laiz.tcpserver.dao.UiLogRecord;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class UILogService {

    private static final ArrayBlockingQueue<UiLogRecord> UI_LOG_RECORDS = new ArrayBlockingQueue<>(100000);

    public static void add(String source, String information) {
        LocalTime localTime = java.time.LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

        UiLogRecord uiLogRecord = new UiLogRecord();
        uiLogRecord.setTime(localTime.format(formatter));
        uiLogRecord.setSource(source);
        uiLogRecord.setInformation(information);
        UI_LOG_RECORDS.offer(uiLogRecord);
    }

    public static List<UiLogRecord> poll() {
        List<UiLogRecord> output = new ArrayList<>();
        for (int i = 0; i < UI_LOG_RECORDS.size(); i++) {
            output.add(UI_LOG_RECORDS.poll());
        }

        return output;
    }
}
