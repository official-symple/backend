package com.DreamOfDuck.fcm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/*
* 모바일에서 전달 받은 객체
* */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FcmSend {
    private String token;

    private String title;
    private String body;
    public static FcmSend from(String token, String title, String body){
        return FcmSend.builder()
                .token(token)
                .title(title)
                .body(body)
                .build();
    }

}
