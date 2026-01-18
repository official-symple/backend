package com.DreamOfDuck.pang.service;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.repository.MemberRepository;
import com.DreamOfDuck.global.redis.RedisService;
import com.DreamOfDuck.pang.dto.request.ScoreCreateRequest;
import com.DreamOfDuck.pang.dto.response.PersonalRecord;
import com.DreamOfDuck.pang.dto.response.RankingResponse;
import com.DreamOfDuck.pang.dto.response.ScoreResponse;
import com.DreamOfDuck.pang.entity.Score;

import com.DreamOfDuck.pang.repository.ScoreRepository;
import com.DreamOfDuck.pang.repository.ScoreRepositoryCustomImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScoreService {
    private final MemberRepository memberRepository;
    private final ScoreRepository scoreRepository;
    private final ScoreRepositoryCustomImpl scoreRepositoryCustom;
    private final ApplicationEventPublisher eventPublisher;
    private final RedisService redisService;

    @Transactional
    public ScoreResponse createScore(Member host, ScoreCreateRequest request){
        //entity 저장
        Score score=Score.builder()
                .score(request.getScore())
                .build();
        score.addHost(host);
        scoreRepository.save(score);
        host.setCntPlaying(host.getCntPlaying()+1);
        ZoneId userZone = ZoneId.of(host.getLocation()==null?"Asia/Seoul":host.getLocation());
        LocalDate now = LocalDate.now(userZone);
        //resopnse
        Long totalScore=scoreRepository.count();
        Long goePlayer=scoreRepositoryCustom.countByScoreGreaterThanEqual(request.getScore());
        Long worldRecord=redisService.getLongValues("worldRecord");
        if(worldRecord==null || worldRecord<request.getScore()){
            redisService.setLongValue("worldRecord", request.getScore(), null, null);
            worldRecord= request.getScore();
        }
        ScoreResponse res = ScoreResponse.from(score);

        res.setPercentile((double)goePlayer/totalScore*100);
        res.setRank(goePlayer);
        res.setWorldRecord(worldRecord);
        return res;
    }
    public RankingResponse getRanking(Member host) {
        RankingResponse res = new RankingResponse();
        res.setCntPlaying(host.getCntPlaying());

        List<Score> scores = scoreRepository.findAllByOrderByScoreDesc();
        Set<String> seenNicknames = new HashSet<>();

        List<PersonalRecord> rankingList = scores.stream()
                .filter(score -> seenNicknames.add(score.getHost().getNickname()))
                .map(score -> {
                    PersonalRecord personalRecord = new PersonalRecord();
                    personalRecord.setScore(score.getScore());
                    personalRecord.setNickname(score.getHost().getNickname());
                    return personalRecord;
                })
                .limit(5)
                .toList();

        res.setRankingList(rankingList);

        Long myBestScore = scoreRepository.findTopByHostOrderByScoreDesc(host)
                .map(Score::getScore)
                .orElse(0L);
        res.setMyBestScore(myBestScore);

        Long betterPlayersCount = scoreRepositoryCustom.countDistinctHostByScoreGreaterThan(myBestScore);
        res.setMyBestRank(betterPlayersCount + 1);

        return res;
    }



}
