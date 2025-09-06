package com.DreamOfDuck.fcm.scheduler;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.repository.MemberRepository;
import com.DreamOfDuck.account.service.MemberService;
import com.DreamOfDuck.fcm.dto.FcmRequest;
import com.DreamOfDuck.fcm.service.FcmService;
import com.DreamOfDuck.mind.entity.MindCheckTime;
import com.DreamOfDuck.mind.entity.MindChecks;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmScheduler {
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final FcmService fcmService; // 실제 FCM 전송 서비스

    // 매 분마다 체크
    @Async
    @Scheduled(cron = "0 * * * * *") // 매 분 실행
    public void sendMindCheckPush() {
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            try {
                ZoneId userZone = ZoneId.of(member.getLocation()==null?"Asia/Seoul":member.getLocation());

                ZonedDateTime userNow = ZonedDateTime.now(userZone);
                DayOfWeek today = userNow.getDayOfWeek();
                LocalTime currentTime = userNow.toLocalTime();


                Optional<MindCheckTime> optionalTime = member.getMindCheckTimes().stream()
                        .filter(t -> t.getDayOfWeek() == today)
                        .findFirst()
                        .or(()->Optional.of(MindCheckTime.builder()
                                .dayTime(ZonedDateTime.of(userNow.toLocalDate(), LocalTime.of(8,0),userZone).toLocalTime())
                                .nightTime(ZonedDateTime.of(userNow.toLocalDate(), LocalTime.of(23,0),userZone).toLocalTime())
                                .build()));

                MindCheckTime mindCheckTime = optionalTime.get();

                //dayTime 알림
                if (currentTime.equals(mindCheckTime.getDayTime())) {
                    String[] messages = {
                            "좋은 아침이에요! 꽥! 지금 내 마음을 체크하고 하루를 시작해요.",
                            "눈뜨자마자 마음체크, " + member.getNickname() + "님, 오늘도 잊지 않았죠?",
                            "어제와 오늘 아침, 어떤 차이가 있는지 기록해볼까요?",
                            "지금 마음체크하면 깃털을 받을 수 있어요!"
                    };
                    Random random = new Random();

                    FcmRequest request = FcmRequest.builder()
                            .title("좋은 하루 보내고 있어요? ")
                            .body(messages[random.nextInt(messages.length)])
                            .deeplink("ducksdream://deeplink/mindCheck")
                            .build();
                    fcmService.sendMessageTo(member.getDeviceToken(), request);
                }
                //nightTime 알림
                if (currentTime.equals(mindCheckTime.getNightTime())) {
                    String[] messages={
                            "자기 전에 마음체크! "+member.getNickname()+ "님, 지금은 기분이 어때요?",
                            "지금 마음체크하면 깃털을 받을 수 있어요!",
                            "어제와 오늘 밤, 어떤 차이가 있는지 기록해볼까요?",
                            "밤에 하는 마음체크는 특히 중요해요. 내 마음 상태를 이해하는 첫 걸음!"
                    };
                    Random random = new Random();

                    FcmRequest request = FcmRequest.builder()
                            .title("좋은 하루 보내고 있어요? ")
                            .body(messages[random.nextInt(messages.length)])
                            .deeplink("ducksdream://deeplink/mindCheck")
                            .build();
                    fcmService.sendMessageTo(member.getDeviceToken(), request);
                }
                //아침 마음체크 마감 10분전
                if(currentTime.equals(mindCheckTime.getDayTime().plusMinutes(50))
                        && (memberService.hasTodayMindCheck(member, userNow.toLocalDate())==null)){
                    String[] messages={
                            "아침 마음체크를 할 수 있는 시간이 10분 밖에 남지 않았어요! 아침 기분은 하루의 나침반이에요.",
                            "꽥!! 아침 마음체크 마감까지 10분! 지금 체크해보세요.",
                            "지금 마음체크하면 더 정확한 마음차트를 받을 수 있어요."
                    };
                    Random random = new Random();
                    FcmRequest request = FcmRequest.builder()
                            .title("좋은 하루 보내고 있어요? ")
                            .body(messages[random.nextInt(messages.length)])
                            .deeplink("ducksdream://deeplink/mindCheck")
                            .build();
                    fcmService.sendMessageTo(member.getDeviceToken(), request);
                }
                //아침 미음체크 마감 후 2시간
                if(currentTime.equals(mindCheckTime.getDayTime().plusHours(3))){
                    String[] messages1={
                            "오늘은 아침 마음체크를 놓쳤어요. 밤에는 꼭 만나요!",
                            member.getNickname()+"님, 밤에는 마음체크 해줄거죠? 기다리고 있을게요!"
                    };
                    String[] messages2={
                            "오늘 하루를 완성하려면 밤 기록도 필요해요. 잊지 마세요!",
                            "아침 마음체크 성공! 최고최고! 밤에도 찾아와줄거죠?",
                            "아침 마음체크에 성공한 "+member.getNickname()+ "님은 역시 멋쟁이에요."
                    };
                    Random random = new Random();
                    //미완료
                    if(memberService.hasTodayMindCheck(member, userNow.toLocalDate())==null){
                        FcmRequest request = FcmRequest.builder()
                                .title("좋은 하루 보내고 있어요? ")
                                .body(messages1[random.nextInt(messages1.length)])
                                .deeplink("ducksdream://deeplink/record")
                                .build();
                        fcmService.sendMessageTo(member.getDeviceToken(), request);
                    }else{//완료
                        FcmRequest request = FcmRequest.builder()
                                .title("좋은 하루 보내고 있어요? ")
                                .body(messages2[random.nextInt(messages2.length)])
                                .deeplink("ducksdream://deeplink/record")
                                .build();
                        fcmService.sendMessageTo(member.getDeviceToken(), request);
                    }
                }
                //수면정보+1, 하루 종일 마음체크 x
                if(currentTime.equals(mindCheckTime.getNightTime().plusHours(1))
                        && (memberService.hasTodayMindCheck(member, userNow.toLocalDate())==null)){
                    String[] messages={
                            "오늘은 마음체크가 비어 있어요. 내일은 꼭 이어가볼까요?",
                            "유저의 "+ ThreadLocalRandom.current().nextInt(80,101) +"%가 마음체크를 꾸준히 완료하고 있어요. 내일은 "+member.getNickname()+"님도 으쌰으쌰!"
                    };
                    Random random = new Random();
                    FcmRequest request = FcmRequest.builder()
                            .title("좋은 하루 보내고 있어요? ")
                            .body(messages[random.nextInt(messages.length)])
                            .deeplink("ducksdream://deeplink/record")
                            .build();
                    fcmService.sendMessageTo(member.getDeviceToken(), request);
                }

                if(currentTime.getHour()==16 && currentTime.getMinute() == 0){
                    int streak = getConsecutiveMindChecks(member, userNow.toLocalDate());

                    if (streak >= 10) {
                        FcmRequest request = FcmRequest.builder()
                                .title("좋은 하루 보내고 있어요? ")
                                .body("아침 마음 기록 "+streak+"일째! "+member.getDuckname()+"도 뿌듯해해요.")
                                .deeplink("ducksdream://deeplink/record")
                                .build();
                        fcmService.sendMessageTo(member.getDeviceToken(), request);

                    } else if (streak == 7) {
                        FcmRequest request = FcmRequest.builder()
                                .title("좋은 하루 보내고 있어요? ")
                                .body("1주일째 아침 마음체크 중! 멋진 꾸준함이에요.")
                                .deeplink("ducksdream://deeplink/record")
                                .build();
                        fcmService.sendMessageTo(member.getDeviceToken(), request);

                    } else if (streak == 3) {
                        FcmRequest request = FcmRequest.builder()
                                .title("좋은 하루 보내고 있어요? ")
                                .body("3일째 아침 마음체크 성공! 패턴이 눈에 보이기 시작했어요.")
                                .deeplink("ducksdream://deeplink/record")
                                .build();
                        fcmService.sendMessageTo(member.getDeviceToken(), request);
                    }
                }

            } catch (Exception e) {
                log.error("Error sending push for member {}: {}", member.getId(), e.getMessage());
            }
        }
    }
    private int getConsecutiveMindChecks(Member member, LocalDate today) {
        int streak = 0;

        for (int i = 0; ; i++) {
            LocalDate date = today.minusDays(i);
            MindChecks check = memberService.hasTodayMindCheck(member, date);

            if (check != null && check.getDayMindCheck() != null) {
                streak++;
            } else {
                break; // 끊기면 중단
            }
        }

        return streak;
    }
}
