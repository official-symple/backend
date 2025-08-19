package com.DreamOfDuck.goods.scheduler;

import com.DreamOfDuck.account.entity.Language;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoodsScheduler {
    private final MemberRepository memberRepository;
    @Async
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")  // 매일 00:00에 실행
    @Transactional
    public void resetHeartEveryMidnight() {
        List<Member> members = memberRepository.findByLanguage(Language.KOR);
        for (Member member : members) {
            member.setHeart(2);
        }
    }
    @Async
    @Scheduled(cron = "0 0 0 * * *", zone = "America/New_York")  // 매일 00:00에 실행
    @Transactional
    public void resetHeartEveryMidnightInUSA() {
        List<Member> members = memberRepository.findByLanguage(Language.ENG);
        for (Member member : members) {
            member.setHeart(2);
        }
    }
}
