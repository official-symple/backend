package com.DreamOfDuck.fcm.service;

import com.DreamOfDuck.fcm.dto.FcmMessage;
import com.DreamOfDuck.fcm.dto.FcmSend;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Slf4j
public class FcmService {

    public int sendMessageTo(FcmSend fcmSend) throws IOException{
        String message = makeMessage(fcmSend);
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + getAccessToken());

        HttpEntity entity = new HttpEntity<>(message, headers);
        String API_URL = "<https://fcm.googleapis.com/v1/projects/duck-s-dream/messages:send>";
        ResponseEntity response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

        System.out.println(response.getStatusCode());

        return response.getStatusCode() == HttpStatus.OK ? 1 : 0;
    }
    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "firebase.json";

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("<https://www.googleapis.com/auth/cloud-platform>"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }
    private String makeMessage(FcmSend fcmSend) throws JsonProcessingException {

        ObjectMapper om = new ObjectMapper();
        FcmMessage fcmMessage = FcmMessage.builder()
                .message(FcmMessage.Message.builder()
                        .token(fcmSend.getToken())
                        .notification(FcmMessage.Notification.builder()
                                .title(fcmSend.getTitle())
                                .body(fcmSend.getBody())
                                .image(null)
                                .build()
                        ).build())
                .validateOnly(false).build();

        return om.writeValueAsString(fcmMessage);
    }
}
