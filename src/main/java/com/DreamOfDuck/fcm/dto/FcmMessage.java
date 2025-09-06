package com.DreamOfDuck.fcm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class FcmMessage {
    private boolean validateOnly;
    private Message message;
    @Builder
    @AllArgsConstructor
    @Getter
    public static class Message {
        private String token;
        private Notification notification;
        private Data data;

    }
    @Builder
    @AllArgsConstructor
    @Getter
    public static class Notification {
        private String title;
        private String body;
        private String image;
    }
    @Builder
    @AllArgsConstructor
    @Getter
    public static class Data {
        private String deeplink;
    }

}
