package com.DreamOfDuck.fcm;

import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import feign.Request;
import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {
    private final String API_URL = "https://fcm.googleapis.com/v1/projects/duck-s-dream/messages:send";
    private final ObjectMapper objectMapper;

    public void sendMessageTo(String deviceToken, FcmRequest fcmRequest) throws IOException {
        log.info("Sending FCM message");
        fcmRequest.setDeviceToken(deviceToken);

        String message = makeMessage(fcmRequest);
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + getAccessToken());

        HttpEntity entity = new HttpEntity<>(message, headers);

        String API_URL = "https://fcm.googleapis.com/v1/projects/duck-s-dream/messages:send";
        ResponseEntity response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);
        log.info("success to send request");
        System.out.println(response.getStatusCode());
    }

    private String getAccessToken() throws IOException {

        String firebaseConfigPath = "firebase.json";

        try {
            final GoogleCredentials googleCredentials = GoogleCredentials
                    .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                    .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

            googleCredentials.refreshIfExpired();
            log.info("access token: {}",googleCredentials.getAccessToken());
            return googleCredentials.getAccessToken().getTokenValue();

        } catch (IOException e) {
            throw new CustomException(ErrorCode.GOOGLE_REQUEST_TOKEN_ERROR);
        }
    }

        googleCredentials.refreshIfExpired();
        log.info("ending access token");
        return googleCredentials.getAccessToken().getTokenValue();
    }

    private String makeMessage(FcmRequest fcmRequest) throws JsonProcessingException {

        ObjectMapper om = new ObjectMapper();
        FcmMessage fcmMessageDto = FcmMessage.builder()
                .message(FcmMessage.Message.builder()
                        .token(fcmRequest.getDeviceToken())
                        .notification(FcmMessage.Notification.builder()
                                .title(fcmRequest.getTitle())
                                .body(fcmRequest.getBody())
                                .image(null)
                                .build()
                        ).build()).validateOnly(false).build();
        return om.writeValueAsString(fcmMessageDto);
    }
}