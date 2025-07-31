package com.DreamOfDuck.account.scheduler;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberScheduler {
    private final MemberRepository memberRepository;
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")  // 매일 00:00에 실행
    @Transactional
    public void resetHeartEveryMidnight() {
        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            member.setHeart(2);
        }
    }
}
