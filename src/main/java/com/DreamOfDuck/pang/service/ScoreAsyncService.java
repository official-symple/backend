package com.DreamOfDuck.pang.service;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.global.redis.RedisService;
import com.DreamOfDuck.pang.repository.ScoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScoreAsyncService {
    private final ScoreRepository scoreRepository;
    private final RedisService redisService;

    @Transactional
    @Async
    public void updateTotalPlayer(Member member){
        Long totalPlayer=redisService.getLongValues("totalPlayer");
        if(totalPlayer==null){
            redisService.setLongValue("totalPlayer", 1L, null, null);
            log.info("update toal player");
        } else if(member.getScores().isEmpty()){
            redisService.setLongValue("totalPlayer", totalPlayer+1, null, null);
            log.info("update toal player");
        }

    }
    @Transactional
    @Async
    public void updateWorldRecord(Long score){
        Long worldRecord=redisService.getLongValues("worldRecord");
        if(worldRecord==null){
            redisService.setLongValue("worldRecord", score, null, null);
            log.info("update worldRecord");
        }
        else if(worldRecord<score){
            redisService.setLongValue("worldRecord", score, null, null);
            log.info("update worldRecord");
        }
    }
}
