package com.DreamOfDuck.fcm.service;

import com.DreamOfDuck.account.entity.Language;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.fcm.NotificationType;
import com.DreamOfDuck.fcm.dto.FcmMessage;
import com.DreamOfDuck.fcm.dto.FcmRequest;
import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import com.DreamOfDuck.mind.entity.MindChecks;
import com.DreamOfDuck.mind.repository.MindChecksRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FcmService {
    private final String API_URL = "https://fcm.googleapis.com/v1/projects/duck-s-dream/messages:send";
    private final ObjectMapper objectMapper;
    private final Random random = new Random();
    private final MindChecksRepository mindChecksRepository;

    @Async("threadPoolTaskExecutor") // ★ 핵심: 여기서 병렬 처리됨
    public void sendNotificationAsync(Member member, NotificationType type) {
        try {
            // 언어 설정 확인
            boolean isKor = member.getLanguage() == Language.KOR;

            // 제목, 본문, 딥링크 생성
            String title = isKor ? "좋은 하루 보내고 있어요?" : "Having a good day?";
            String body = getMessageBody(member, type, isKor);
            String deeplink = getDeepLink(type);

            // 보낼 메시지가 없으면 중단 (예: 스트릭 조건 불만족 시 null 리턴됨)
            if (body == null) return;

            // FCM 전송 요청 객체 생성
            FcmRequest request = FcmRequest.builder()
                    .title(title) // 스트릭일 경우 getMessageBody 내부에서 제목이 변경될 수 없어 고정 타이틀 사용하거나, 리턴 타입을 객체로 변경 고려 가능. 현재는 고정.
                    .body(body)
                    .deeplink(deeplink)
                    .build();

            // 만약 스트릭 보상의 경우 제목을 따로 설정해야 한다면 아래처럼 분기
            if (type == NotificationType.STREAK_REWARD) {
                request.setTitle(isKor ? "잘하고 있어요!" : "Keep it up, you got this!");
            }

            // 전송 (FcmService는 동기 메서드여야 함)
            sendMessageTo(member.getDeviceToken(), request);

        } catch (Exception e) {
            log.error("Failed to send FCM to member {}: {}", member.getId(), e.getMessage());
        }
    }

    // 메시지 내용을 결정하는 로직 (기존 스케줄러에 있던 코드 이동)
    private String getMessageBody(Member member, NotificationType type, boolean isKor) {

        String nickname = member.getNickname();

        switch (type) {
            case MORNING_START:
                if (isKor) {
                    String[] msgs = {
                            "좋은 아침이에요! 꽥! \n지금 내 마음을 체크하고 하루를 시작해요.",
                            "눈뜨자마자 마음체크, " + nickname + "님, 오늘도 잊지 않았죠?",
                            "어제와 오늘 아침, 어떤 차이가 있는지 기록해볼까요?",
                            "지금 마음체크하면 깃털을 받을 수 있어요!"
                    };
                    return msgs[random.nextInt(msgs.length)];
                } else {
                    String[] msgs = {
                            "Good morning! Quack! Check in with yourself and start your day.",
                            "First-thing mood check—" + nickname + ",\n you didn’t forget today, right?",
                            "Let’s log how this morning feels compared to yesterday.",
                            "Do a quick check-in now and earn feathers!"
                    };
                    return msgs[random.nextInt(msgs.length)];
                }

            case NIGHT_START:
                if (isKor) {
                    String[] msgs = {
                            "자기 전에 마음체크! " + nickname + "님, 지금은 기분이 어때요?",
                            "지금 마음체크하면 깃털을 받을 수 있어요!",
                            "어제와 오늘 밤, 어떤 차이가 있는지 기록해볼까요?"
                    };
                    return msgs[random.nextInt(msgs.length)];
                } else {
                    String[] msgs = {
                            "Pre-bed check-in!\n" + nickname + ", how are you feeling right now?",
                            "Do a quick check-in now and earn feathers!",
                            "How does tonight feel compared to last night?"
                    };
                    return msgs[random.nextInt(msgs.length)];
                }

            case MORNING_DEADLINE:
                if (isKor) {
                    String[] msgs = {
                            "아침 마음체크를 할 수 있는 시간이 10분 밖에 남지 않았어요!",
                            "꽥!! 아침 마음체크 마감까지 10분! 지금 체크해보세요.",
                            "지금 마음체크하면 더 정확한 마음차트를 받을 수 있어요."
                    };
                    return msgs[random.nextInt(msgs.length)];
                } else {
                    String[] msgs = {
                            "Only 10 minutes left for your morning check-in!",
                            "Quack!! 10 minutes until the morning check-in closes \n— log it now.",
                            "Check in now for a more accurate Mind Chart."
                    };
                    return msgs[random.nextInt(msgs.length)];
                }

            case MORNING_MISSED: // 아침 놓친 사람 (밤에 만나요)
                if (isKor) {
                    String[] msgs = {
                            "오늘은 아침 마음체크를 놓쳤어요. \n밤에는 꼭 만나요!",
                            nickname + "님, 밤에는 마음체크 해줄거죠? 기다리고 있을게요!",

                    };
                    return msgs[random.nextInt(msgs.length)];
                } else {
                    String[] msgs = {
                            "You missed this morning’s check-in.\nLet’s meet tonight!",
                            nickname + ", will you check in tonight?\nI’ll be waiting!"
                    };
                    return msgs[random.nextInt(msgs.length)];
                }
            case MORNING_COMPLETED:
                if (isKor) {
                    String[] msgs = {
                            "오늘 하루를 완성하려면 밤 기록도 필요해요. \n잊지 마세요!",
                            "아침 마음체크 성공! 최고최고! \n밤에도 찾아와줄거죠?",
                            "아침 마음체크에 성공한 " + nickname + "님은 역시 멋쟁이에요."
                    };
                    return msgs[random.nextInt(msgs.length)];
                } else {
                    String[] msgs = {
                            "To complete your day, you need a night check-in too \n— don’t forget!",
                            "Morning check-in complete — nice job!\nSee you again tonight?",
                            nickname + ", morning check-in done \n— you’re awesome."
                    };
                    return msgs[random.nextInt(msgs.length)];
                }
            case NIGHT_MISSED:
                int randomPercent = ThreadLocalRandom.current().nextInt(80, 101);
                if (isKor) {
                    String[] msgs = {
                            "오늘은 마음체크가 비어 있어요. \n내일은 꼭 이어가볼까요?",
                            "유저의 " + randomPercent + "%가 마음체크를 꾸준히 완료하고 있어요. \n내일은 " + nickname + "님도 으쌰으쌰!"
                    };
                    return msgs[random.nextInt(msgs.length)];
                } else {
                    String[] msgs = {
                            "No check-ins today. Shall we pick it back up tomorrow?",
                            randomPercent + "% of users are completing their check-ins consistently. \nCheering for you tomorrow," + nickname
                    };
                    return msgs[random.nextInt(msgs.length)];
                }
            case STREAK_REWARD:
                ZoneId userZone = ZoneId.of(member.getLocation() == null ? "Asia/Seoul" : member.getLocation());
                LocalDate today = LocalDate.now(userZone);
                int streak = getConsecutiveMindChecks(member, today);

                if (streak >= 10) {
                    return isKor ?
                            "아침 마음 기록 " + streak + "일째! " + member.getDuckname() + "도 뿌듯해해요." :
                            "Morning check-in day " + streak + "!\n" + member.getDuckname() + "is proud of you, too.";
                } else if (streak == 7) {
                    return isKor ?
                            "1주일째 아침 마음체크 중! \n멋진 꾸준함이에요." :
                            "One full week of morning check-ins! Love the consistency.";
                } else if (streak == 3) {
                    return isKor ?
                            "3일째 아침 마음체크 성공! \n패턴이 눈에 보이기 시작했어요." :
                            "Morning check-ins 3 days in a row! Your pattern is starting to show.";
                }
                return null; // 조건에 안 맞으면 안 보냄
            default:
                return null;
        }
    }
    private int getConsecutiveMindChecks(Member member, LocalDate today) {
        int streak = 0;
        // 오늘부터 역순으로 날짜를 빼가며 연속 체크 확인
        for (int i = 0; ; i++) {
            LocalDate date = today.minusDays(i);
            // 해당 날짜에 기록이 있는지 DB 조회 (Lazy Loading 주의: @Transactional 필요)
            MindChecks check = mindChecksRepository.findByHostAndDate(member, date)
                    .stream().findFirst().orElse(null);

            if (check != null && check.getDayMindCheck() != null) {
                streak++;
            } else {
                break; // 기록이 끊기면 루프 종료
            }
        }
        return streak;
    }
    private String getDeepLink(NotificationType type) {
        if (type == NotificationType.MORNING_MISSED || type == NotificationType.STREAK_REWARD) {
            return "ducksdream://deeplink/record";
        }
        return "ducksdream://deeplink/mindCheck";
    }

    public void sendMessageTo(String deviceToken, FcmRequest fcmRequest) throws IOException {
        FcmMessage.Notification notification = FcmMessage.Notification.builder()
                .title(fcmRequest.getTitle())
                .body(fcmRequest.getBody())
                .image(null) // 필요시 설정
                .build();

        FcmMessage.Message messagePayload = FcmMessage.Message.builder()
                .token(deviceToken)
                .notification(notification)
                .build();

        FcmMessage fcmMessage = FcmMessage.builder()
                .validateOnly(false)
                .message(messagePayload)
                .build();

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + getAccessToken());

        HttpEntity<FcmMessage> entity = new HttpEntity<>(fcmMessage, headers);

        String projectId = "duck-s-dream"; // firebase.json의 project_id와 같아야 함
        String API_URL = "https://fcm.googleapis.com/v1/projects/" + projectId + "/messages:send";

        try {
            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);
            log.info("success to send request. Status: {}", response.getStatusCode());
        } catch (Exception e) {
            log.error("FCM Send Error: ", e);
            throw new RuntimeException("FCM 전송 실패");
        }
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
                        )
                        .data(Map.of("deeplink", fcmRequest.getDeeplink()))
                        .build()).validateOnly(false).build();
        return om.writeValueAsString(fcmMessageDto);
    }
}