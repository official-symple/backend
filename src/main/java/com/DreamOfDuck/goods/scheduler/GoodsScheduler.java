package com.DreamOfDuck.goods.scheduler;

import com.DreamOfDuck.account.entity.Language;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoodsScheduler {
    private final MemberRepository memberRepository;
    @Async
    @Scheduled(cron = "0 * * * * *") // 매 분마다 체크
    @Transactional
    public void resetHeartByMemberLocation() {
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            try {
                ZoneId userZone = ZoneId.of(member.getLocation()==null?"Asia/Seoul":member.getLocation());
                LocalTime userLocalTime = LocalTime.now(userZone);

                // 자정(00:00)에 초기화
                if (userLocalTime.getHour() == 0 && userLocalTime.getMinute() == 0) {
                    member.setHeart(2);
                }

            } catch (Exception e) {
                // location 값이 잘못됐거나 예외 발생 시 로그
                log.error("Invalid time zone for member {}: {}", member.getId(), member.getLocation(), e);
            }
        }
    }
}
