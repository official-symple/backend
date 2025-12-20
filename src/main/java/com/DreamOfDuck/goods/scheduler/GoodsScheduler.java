package com.DreamOfDuck.goods.scheduler;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.entity.Role;
import com.DreamOfDuck.account.repository.MemberRepository;
import com.DreamOfDuck.goods.dto.request.DiaRequest;
import com.DreamOfDuck.goods.service.GoodsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final GoodsService goodsService;

    @Scheduled(cron = "0 0 * * * *") // 매 시간마다 체크
    @Transactional
    public void resetHeartByMemberLocation() {
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            if(member.getRole() == Role.ROLE_PREMIUM) continue;
            try {
                ZoneId userZone = ZoneId.of(member.getLocation()==null?"Asia/Seoul":member.getLocation());
                LocalTime userLocalTime = LocalTime.now(userZone);

                // 자정(00:00)에 초기화
                if (userLocalTime.getHour() == 0 && userLocalTime.getMinute() == 0) {
                    goodsService.setHeartAsync(member, 6);
                }

            } catch (Exception e) {
                log.error("Invalid time zone for member {}: {}", member.getId(), member.getLocation(), e);
            }
        }
    }

    @Scheduled(cron = "0 0 * * * *") // 매 시간마다 체크
    @Transactional
    public void resetCntTalkByMemberLocation() {
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            if(member.getRole() == Role.ROLE_PREMIUM) continue;
            try {
                ZoneId userZone = ZoneId.of(member.getLocation()==null?"Asia/Seoul":member.getLocation());
                LocalTime userLocalTime = LocalTime.now(userZone);

                // 자정(00:00)에 초기화
                if (userLocalTime.getHour() == 0 && userLocalTime.getMinute() == 0) {
                    member.setCntTalk(2);
                }

            } catch (Exception e) {
                log.error("Invalid time zone for member {}: {}", member.getId(), member.getLocation(), e);
            }
        }
    }

    @Scheduled(cron = "0 0 0 1 * *", zone = "Asia/Seoul")
    @Transactional
    public void resetMonthlyDataByMemberLocation() {
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            if(member.getRole() != Role.ROLE_PREMIUM) continue;
            try {
                log.info("====== 매월 1일 00:00 - 월별 데이터 초기화 작업 시작 ======");
                DiaRequest diaRequest = new DiaRequest();
                diaRequest.setDia(300);
                goodsService.plusDia(member, diaRequest);
            } catch (Exception e) {
                log.error("Invalid time zone for member {}: {}", member.getId(), member.getLocation(), e);
            }
        }
    }
}
