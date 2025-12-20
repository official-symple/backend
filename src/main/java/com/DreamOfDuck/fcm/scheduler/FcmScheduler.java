package com.DreamOfDuck.fcm.scheduler;

import com.DreamOfDuck.account.entity.Language;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.repository.MemberRepository;
import com.DreamOfDuck.account.service.MemberService;
import com.DreamOfDuck.fcm.NotificationType;
import com.DreamOfDuck.fcm.dto.FcmRequest;
import com.DreamOfDuck.fcm.service.FcmService;
import com.DreamOfDuck.mind.entity.MindCheckTime;
import com.DreamOfDuck.mind.entity.MindChecks;
import com.DreamOfDuck.mind.repository.MindChecksRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmScheduler {
    private final MindChecksRepository mindChecksRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final FcmService fcmService; // 실제 FCM 전송 서비스

    private static final LocalTime DEFAULT_MORNING_TIME = LocalTime.of(9, 0); // 오전 9시
    private static final LocalTime DEFAULT_NIGHT_TIME = LocalTime.of(19, 0);  // 오후 7시
    // 매 분마다 체크
    @Scheduled(cron = "0 * * * * *")
    public void sendMindCheckPush() {
        // 1. 존재하는 타임존 목록 가져오기 (캐싱 권장)
        List<String> distinctZones = memberRepository.findDistinctLocations();
        // location이 null인 유저들을 위해 기본값 추가 (DB에 없을 수도 있으므로)
        if (!distinctZones.contains("Asia/Seoul")) {
            distinctZones.add("Asia/Seoul");
        }

        for (String zoneId : distinctZones) {
            try {
                // 2. 해당 지역의 현재 시간 계산
                ZoneId zone = ZoneId.of(zoneId);
                ZonedDateTime zoneNow = ZonedDateTime.now(zone);
                LocalTime nowTime = zoneNow.toLocalTime().truncatedTo(ChronoUnit.MINUTES);
                DayOfWeek dayOfWeek = zoneNow.getDayOfWeek();
                LocalDate todayDate = zoneNow.toLocalDate();

                log.info("Checking Zone: {}, Time: {}", zoneId, nowTime);

                // ==========================================
                // Case 1: 아침 알림 (정시)
                // ==========================================
                List<Member> morningTargets = memberRepository.findMorningTargets(zoneId, dayOfWeek, nowTime, DEFAULT_MORNING_TIME);
                sendBatch(morningTargets, NotificationType.MORNING_START);

                // ==========================================
                // Case 2: 밤 알림 (정시)
                // ==========================================
                List<Member> nightTargets = memberRepository.findNightTargets(zoneId, dayOfWeek, nowTime, DEFAULT_NIGHT_TIME);
                sendBatch(nightTargets, NotificationType.NIGHT_START);

                // ==========================================
                // Case 3: 아침 마감 10분 전 (현재시간 == 설정시간 + 50분) -> (설정시간 == 현재시간 - 50분)
                // ==========================================
                LocalTime targetTimeForWarning = nowTime.minusMinutes(50);
                List<Member> warningTargets = memberRepository.findNotCheckedTargets(zoneId, dayOfWeek, targetTimeForWarning, DEFAULT_MORNING_TIME, todayDate);
                sendBatch(warningTargets, NotificationType.MORNING_DEADLINE);

                // ==========================================
                // Case 4: 아침 마감 후 3시간 (미완료자 독려)
                // ==========================================
                LocalTime targetTimeForMissed = nowTime.minusHours(3);
                List<Member> missedTargets = memberRepository.findNotCheckedTargets(zoneId, dayOfWeek, targetTimeForMissed, DEFAULT_MORNING_TIME, todayDate);
                sendBatch(missedTargets, NotificationType.MORNING_MISSED);


                // ==========================================
                // Case 5: 22시 스트릭 알림 (특수 케이스)
                // ==========================================
                if (nowTime.getHour() == 22 && nowTime.getMinute() == 0) {
                    List<Member> allMember = memberRepository.findAll();
                    sendBatch(missedTargets, NotificationType.STREAK_REWARD);
                }

            } catch (Exception e) {
                log.error("Error processing zone {}: {}", zoneId, e.getMessage());
            }
        }
    }
    // 매 분 실행

    private void sendBatch(List<Member> members, NotificationType type) {
        if (members == null || members.isEmpty()) {
            return;
        }

        log.info("Sending batch notification [{}] to {} members", type, members.size());

        for (Member member : members) {
            // 여기서 비동기 메서드를 호출하므로 스케줄러는 기다리지 않고 바로 다음으로 넘어갑니다.
            fcmService.sendNotificationAsync(member, type);
        }
    }
}
