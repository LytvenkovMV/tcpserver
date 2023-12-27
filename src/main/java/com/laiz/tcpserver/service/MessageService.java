package com.laiz.tcpserver.service;

import com.laiz.tcpserver.dao.AppMessage;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MessageService {

    private static int numberOfMessages = 0;
    private static int indexOfLastGiven = 0;
    private static List<AppMessage> appMessages = new ArrayList<>();


    public static void add(String source, String information) {
        LocalTime localTime = java.time.LocalTime.now();

        AppMessage appMessage = new AppMessage();
        appMessage.setTime(localTime.toSecondOfDay());
        appMessage.setSource(source);
        appMessage.setInformation(information);
        appMessages.add(appMessage);

        numberOfMessages++;
    }

    public static List<AppMessage> give() {
        List<AppMessage> given = new ArrayList<>();

        int i = indexOfLastGiven;
        while (i < numberOfMessages) {
            given.add(appMessages.get(i));
            i++;
        }
        indexOfLastGiven = i;

        return given;
    }
}
