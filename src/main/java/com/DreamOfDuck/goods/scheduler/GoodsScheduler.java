package com.DreamOfDuck.goods.scheduler;

import com.DreamOfDuck.account.entity.Language;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.entity.Subscribe;
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
            if(member.getSubscribe()==Subscribe.PREMIUM || member.getSubscribe()==Subscribe.PRO) continue;
            try {
                ZoneId userZone = ZoneId.of(member.getLocation()==null?"Asia/Seoul":member.getLocation());
                LocalTime userLocalTime = LocalTime.now(userZone);

                // 자정(00:00)에 초기화
                if (userLocalTime.getHour() == 0 && userLocalTime.getMinute() == 0) {
                    if(member.getSubscribe()== Subscribe.FREE || member.getSubscribe()==null) member.setHeart(6);
                    else if(member.getSubscribe()==Subscribe.BASIC) member.setHeart(10);
                }

            } catch (Exception e) {
                // location 값이 잘못됐거나 예외 발생 시 로그
                log.error("Invalid time zone for member {}: {}", member.getId(), member.getLocation(), e);
            }
        }
    }
    @Async
    @Scheduled(cron = "0 * * * * *") // 매 분마다 체크
    @Transactional
    public void resetCntTalkByMemberLocation() {
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            if(member.getSubscribe()==Subscribe.PREMIUM ) continue;
            try {
                ZoneId userZone = ZoneId.of(member.getLocation()==null?"Asia/Seoul":member.getLocation());
                LocalTime userLocalTime = LocalTime.now(userZone);

                // 자정(00:00)에 초기화
                if (userLocalTime.getHour() == 0 && userLocalTime.getMinute() == 0) {
                    if(member.getSubscribe()== Subscribe.FREE || member.getSubscribe()==null) member.setCntTalk(2);
                    else if(member.getSubscribe()==Subscribe.BASIC) member.setHeart(3);
                    else if(member.getSubscribe()==Subscribe.PRO) member.setHeart(5);
                }

            } catch (Exception e) {
                // location 값이 잘못됐거나 예외 발생 시 로그
                log.error("Invalid time zone for member {}: {}", member.getId(), member.getLocation(), e);
            }
        }
    }
}
