package com.DreamOfDuck.mind.service;

import com.DreamOfDuck.account.dto.request.FeatherRequest;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.service.MemberService;
import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import com.DreamOfDuck.goods.service.GoodsService;
import com.DreamOfDuck.mind.dto.response.MindCheckReport;
import com.DreamOfDuck.mind.dto.response.MindCheckReportPeriod;
import com.DreamOfDuck.mind.dto.response.MindCheckTimeResponse;
import com.DreamOfDuck.mind.repository.MindCheckRepository;
import com.DreamOfDuck.mind.dto.request.MindCheckRequest;
import com.DreamOfDuck.mind.dto.response.MindCheckResponse;
import com.DreamOfDuck.mind.dto.request.MindCheckTimeRequest;
import com.DreamOfDuck.mind.entity.*;
import com.DreamOfDuck.mind.repository.MindCheckTimeRepository;
import com.DreamOfDuck.mind.repository.MindChecksRepository;
import com.DreamOfDuck.talk.entity.Emotion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MindCheckService {
    private final MindCheckRepository mindCheckRepository;
    private final MindChecksRepository mindChecksRepository;
    private final MindCheckTimeRepository mindCheckTimeRepository;
    private final MindCheckTimeService mindCheckTimeService;
    private final GoodsService goodsService;

    @Transactional
    public MindCheckResponse checkMind(Member member, MindCheckRequest request) {

        ZoneId userZone = ZoneId.of(member.getLocation()); // ex: "Asia/Seoul"
        LocalDateTime now = LocalDateTime.now(userZone);
        TimePeriod timePeriod = TimePeriod.of(now);
        //접근 가능한 시간 이후면 에러처리
        if(checkTime(member, userZone, now, timePeriod)) throw new CustomException(ErrorCode.NOT_PERMISSION_ACCESS);
        //6 to 6
        if(now.toLocalTime().isBefore(LocalTime.of(6,0))){
            now=now.minusDays(1);
        }
        LocalDate nowDate=now.toLocalDate();
        MindChecks mindChecks=mindChecksRepository.findByHostAndDate(member, nowDate).stream().findFirst().orElse(null);
        MindCheck mindCheck = MindCheck.builder()
                .question1(request.isQuestion1())
                .question2(request.isQuestion2())
                .question3(request.isQuestion3())
                .emotion(Emotion.fromId(request.getEmotion()))
                .createTime(now)
                .build();
        mindCheckRepository.save(mindCheck);
        //calculate score
        int cnt=0;
        if(request.isQuestion1()) cnt++;
        if(request.isQuestion2()) cnt++;
        if(request.isQuestion3()) cnt++;
        float score=100*cnt/3;
        mindCheck.setScore(score);

        if(mindChecks==null){
            mindChecks = new MindChecks();
            mindChecks.setDate(nowDate);
            mindChecks.addHost(member);
        }
        switch(timePeriod){
            case DAY:
                if(mindChecks.getDayMindCheck()!=null) throw new CustomException(ErrorCode.ALREADY_EXIST);
                mindChecks.setDayMindCheck(mindCheck);
                break;
            case NIGHT:
                if(mindChecks.getNightMindCheck()!=null) throw new CustomException(ErrorCode.ALREADY_EXIST);
                mindChecks.setNightMindCheck(mindCheck);
        }
        mindChecksRepository.save(mindChecks);
        FeatherRequest featherRequest=new FeatherRequest();
        featherRequest.setFeather(30);
        goodsService.updateFeather(member, featherRequest); //깃털 보상
        return MindCheckResponse.fromMindCheck(mindCheck);
    }
    private boolean checkTime(Member member, ZoneId userZone, LocalDateTime now, TimePeriod timePeriod) {
        MindCheckTime mindCheckTime = mindCheckTimeService.getMindCheckTime(member, now.getDayOfWeek());
        LocalTime dayTime, nightTime;
        //푸시알림 시간 확인
        if(mindCheckTime==null) {
            dayTime = ZonedDateTime.of(now.toLocalDate(), LocalTime.of(8,0),userZone).toLocalTime();
            nightTime = ZonedDateTime.of(now.toLocalDate(), LocalTime.of(23,0),userZone).toLocalTime();
        }else{
            dayTime = mindCheckTime.getDayTime();
            nightTime = mindCheckTime.getNightTime();
        }

        if(timePeriod==TimePeriod.DAY){
            return now.toLocalTime().isBefore(dayTime) || now.toLocalTime().isAfter(dayTime.plusHours(1));
        }else{
            LocalDateTime nightDateTime = LocalDateTime.of(now.toLocalDate(), nightTime);
            if(now.toLocalTime().isAfter(LocalTime.MIDNIGHT) && now.toLocalTime().isBefore(LocalTime.of(6, 0))) nightDateTime=nightDateTime.minusDays(1);
            log.info(nightDateTime.toString());
            LocalDateTime start = nightDateTime.minusHours(1);
            LocalDateTime end = nightDateTime.plusHours(1);
            log.info(start.toString());
            log.info(end.toString());
            log.info(now.toString());
            return now.isBefore(start) || now.isAfter(end);
        }
    }
    @Transactional
    public List<MindCheckTimeResponse> setMindCheckTime(Member member, MindCheckTimeRequest request) {
        List<MindCheckTime> mindCheckTimes = member.getMindCheckTimes();

        // 적용할 요일 리스트
        List<DayOfWeek> targetDays;
        if (request.getDayOfWeek() == null) {
            // 월~금
            targetDays = Arrays.asList(
                    DayOfWeek.MONDAY,
                    DayOfWeek.TUESDAY,
                    DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY,
                    DayOfWeek.FRIDAY,
                    DayOfWeek.SATURDAY,
                    DayOfWeek.SUNDAY
            );
        } else {
            targetDays = Collections.singletonList(DayOfWeek.valueOf(request.getDayOfWeek().toUpperCase()));
        }

        for (DayOfWeek day : targetDays) {
            Optional<MindCheckTime> existing = mindCheckTimes.stream()
                    .filter(time -> time.getDayOfWeek() == day)
                    .findFirst();
            //에러처리
            if (request.getDayTime().isBefore(LocalTime.of(6, 0)) || request.getDayTime().isAfter(LocalTime.of(13, 0))) {
                throw new CustomException(ErrorCode.IMPOSSIBLE_PERIOD);
            }
            LocalTime night = request.getNightTime();
            if (!((night.equals(LocalTime.of(18,0)) || night.isAfter(LocalTime.of(18,0)))
                    || (night.isBefore(LocalTime.of(4,0)) || night.equals(LocalTime.of(4,0))))) {
                throw new CustomException(ErrorCode.IMPOSSIBLE_PERIOD);
            }



            if (existing.isPresent()) {
                existing.get().setDayTime(request.getDayTime());
                existing.get().setNightTime(request.getNightTime());
            } else {
                MindCheckTime mindCheckTime = MindCheckTime.builder()
                        .dayOfWeek(day)
                        .dayTime(request.getDayTime())
                        .nightTime(request.getNightTime())
                        .build();
                mindCheckTime.addHost(member);
                mindCheckTimeRepository.save(mindCheckTime);
            }
        }
        return getMindCheckTimes(member);
    }
    public List<MindCheckTimeResponse> getMindCheckTimes(Member member) {
        List<MindCheckTime> times = member.getMindCheckTimes();

        Map<DayOfWeek, MindCheckTime> timeMap = new HashMap<>();
        if (times != null) {
            for (MindCheckTime t : times) {
                timeMap.put(t.getDayOfWeek(), t);
            }
        }

        List<MindCheckTimeResponse> result = Arrays.stream(DayOfWeek.values())
                .map(day -> {
                    MindCheckTime t = timeMap.get(day);
                    if (t == null) {
                        t = MindCheckTime.builder()
                                .dayOfWeek(day)
                                .dayTime(LocalTime.of(8, 0))
                                .nightTime(LocalTime.of(23, 0))
                                .host(member)
                                .build();
                    }
                    return MindCheckTimeResponse.of(t);
                })
                .collect(Collectors.toList());

        return result;
    }

    public MindCheckReport getMindCheckResult(Member member, LocalDate now) {
        MindChecks mindChecks = mindChecksRepository.findByHostAndDate(member, now).stream().findFirst().orElse(null);
        //에러처리
        //null인 경우
        if(mindChecks==null) throw new CustomException(ErrorCode.NULL_MIND_CHECK);
        //오늘 마음체크 미완료인데 요청한 경우
        ZoneId userZone = ZoneId.of(member.getLocation()==null?"Asia/Seoul":member.getLocation());
        ZonedDateTime userNow = ZonedDateTime.now(userZone);
        LocalDate currentDate = userNow.toLocalDate();

        MindCheckReport response = MindCheckReport.of(mindChecks);
        float score=0;
        if(mindChecks.getNightMindCheck()!=null && mindChecks.getDayMindCheck()!=null){
            score+= (float) (mindChecks.getDayMindCheck().getScore()*0.5);
            score+= (float) (mindChecks.getNightMindCheck().getScore()*0.5);
        }else if(mindChecks.getNightMindCheck()!=null){
            score+= (float) (mindChecks.getNightMindCheck().getScore());
        }else if(mindChecks.getDayMindCheck()!=null){
            score+= (float) (mindChecks.getDayMindCheck().getScore());
        }else throw new CustomException(ErrorCode.NULL_MIND_CHECK);
        response.setResult(calculateScore(score));
        if(currentDate.equals(now)){
            response.setCanView(mindChecks.getNightMindCheck()!=null && mindChecks.getDayMindCheck()!=null);
        }else{
            response.setCanView(true);
        }
        return response;
    }
    public MindCheckReportPeriod getMindCheckResultPer2Weeks(Member member, LocalDate now) {
        ZoneId userZone = ZoneId.of(member.getLocation()==null?"Asia/Seoul":member.getLocation());
        LocalDate userNowDate = LocalDate.now(userZone);
        //14일이 안되는 경우
        if(now.plusDays(13).isAfter(userNowDate)) {
            throw new CustomException(ErrorCode.NOT_ENOUGH_RECORDS);
        }
        List<MindChecks> mindChecks11 = mindChecksRepository.findByHostAndDateBetweenOrderByDateDesc(member, now, now.plusDays(10));
        List<MindChecks> mindChecks3 = mindChecksRepository.findByHostAndDateBetweenOrderByDateDesc(member, now.plusDays(11), now.plusDays(13));
        MindCheckReportPeriod curReport = getReportPeriod(member, mindChecks3, mindChecks11);
        List<MindChecks> mindChecks11_2 = mindChecksRepository.findByHostAndDateBetweenOrderByDateDesc(member, now.minusDays(14), now.minusDays(4));
        List<MindChecks> mindChecks3_2 = mindChecksRepository.findByHostAndDateBetweenOrderByDateDesc(member, now.minusDays(3), now.minusDays(1));
        MindCheckReportPeriod pastReport = getReportPeriod(member, mindChecks3_2, mindChecks11_2);
        if(pastReport.getResult()!=null) {
            //전체 결과
            return getMindCheckTrend(curReport, pastReport);
        }
        return curReport;
    }
    public MindCheckReportPeriod getMindCheckResultPer1Month(Member member, LocalDate now) {
        ZoneId userZone = ZoneId.of(member.getLocation()==null?"Asia/Seoul":member.getLocation());
        LocalDate userNowDate = LocalDate.now(userZone);
        //14일이 안되는 경우
        if(now.plusDays(29).isAfter(userNowDate)) {
            throw new CustomException(ErrorCode.NOT_ENOUGH_RECORDS);
        }
        List<MindChecks> mindChecks20 = mindChecksRepository.findByHostAndDateBetweenOrderByDateDesc(member, now, now.plusDays(19));
        List<MindChecks> mindChecks10 = mindChecksRepository.findByHostAndDateBetweenOrderByDateDesc(member, now.plusDays(20), now.plusDays(29));
        MindCheckReportPeriod curReport = getReportPeriod(member, mindChecks10, mindChecks20);
        List<MindChecks> mindChecks20_2 = mindChecksRepository.findByHostAndDateBetweenOrderByDateDesc(member, now.minusDays(30), now.minusDays(11));
        List<MindChecks> mindChecks10_2 = mindChecksRepository.findByHostAndDateBetweenOrderByDateDesc(member, now.minusDays(10), now.minusDays(1));
        MindCheckReportPeriod pastReport = getReportPeriod(member, mindChecks10_2, mindChecks20_2);
        if(pastReport.getResult()!=null) {
            //전체 결과
            return getMindCheckTrend(curReport, pastReport);
        }
        return curReport;
    }
    private MindCheckReportPeriod getMindCheckTrend(MindCheckReportPeriod curReport, MindCheckReportPeriod pastReport){
            String curResult=curReport.getResult();
            String pastResult=pastReport.getResult();
            String[] results = {"고위험", "위험", "주의", "안정"};
            int curIndex = Arrays.asList(results).indexOf(curResult);
            int pastIndex = Arrays.asList(results).indexOf(pastResult);

            String result;
            if(curIndex==pastIndex){
                result="그대로에요";
            }else if(curIndex<pastIndex){
                int diff=pastIndex-curIndex;
                if(diff==1){
                    result="한 단계 하락했어요";
                }else if(diff==2){
                    result="두 단계 하락했어요";
                }else{
                    result="세 단계 하락했어요";
                }
            }else{
                int diff=curIndex-pastIndex;
                if(diff==1){
                    result="한 단계 상승했어요";
                }else if(diff==2){
                    result="두 단계 상승했어요";
                }else{
                    result="세 단계 상승했어요";
                }
            }
            curReport.setResultTrend("지난 2주와 비교했을 때 "+result);
            //우울, 스트레스, 스트레스 조절 어려움
            String resultQ1=curReport.getResponseRateOfQ1()<pastReport.getResponseRateOfQ1()?"줄었고":
                    curReport.getResponseRateOfQ1().equals(pastReport.getResponseRateOfQ1())?"그대로고":"늘었고";
            String resultQ2=curReport.getResponseRateOfQ2()<pastReport.getResponseRateOfQ2()?"줄었어요.":
                    curReport.getResponseRateOfQ2().equals(pastReport.getResponseRateOfQ2())?"그대로예요.":"늘었어요.";
            String resultQ3=curReport.getResponseRateOfQ3()<pastReport.getResponseRateOfQ3()?"줄었어요.":
                    curReport.getResponseRateOfQ3().equals(pastReport.getResponseRateOfQ3())?"그대로예요.":"늘었어요.";
            curReport.setQuestionResponseTrend("우울 빈도가 "+resultQ1+" 스트레스 빈도는 "+resultQ2+"스트레스 조절 어려움 빈도는 "+resultQ3);
            //가장 자주 고른 감정
            String curEmotion = Emotion.fromId(curReport.getTop3Emotions().get(0).getEmotion()).getText();
            String curEmotionType = Emotion.fromText(curEmotion).getId()>=7?"부정 감정인":"긍정 감정인";
            String pastEmotion = Emotion.fromId(pastReport.getTop3Emotions().get(0).getEmotion()).getText();
            String pastEmotionType = Emotion.fromText(curEmotion).getId()>=7?"부정 감정인":"긍정 감정인";
            if(curEmotion.equals(pastEmotion)){
                curReport.setTopEmotionTrend(pastEmotionType+" '"+pastEmotion+"'에서 "+curEmotionType+" '"+curEmotion+"'로 그대로예요.");
            }else{
                curReport.setTopEmotionTrend(pastEmotionType+" '"+pastEmotion+"'에서 "+curEmotionType+" '"+curEmotion+"'로 달라졌어요.");
            }
            //부정 감정 비율
            float pastNegativeEmotionRate = pastReport.getNegativeEmotionRate();
            float curNegativeEmotionRate = curReport.getNegativeEmotionRate();
            if(pastNegativeEmotionRate<curNegativeEmotionRate){
                float diff = curNegativeEmotionRate - pastNegativeEmotionRate;
                curReport.setNegativeEmotionTrend("부정 감정의 비율이 "+String.format("%.2f", diff)+"% 증가했어요.");
            }else if(pastNegativeEmotionRate>curNegativeEmotionRate){
                float diff = pastNegativeEmotionRate - curNegativeEmotionRate;
                curReport.setNegativeEmotionTrend("부정 감정의 비율이 "+String.format("%.2f", diff)+"% 감소했어요.");
            }else{
                curReport.setNegativeEmotionTrend("부정 감정의 비율이 "+String.format("%.2f", curNegativeEmotionRate)+"%로 그대로예요.");
            }
            //마음체크 응답률
            float pastReportResponse = pastReport.getResponseRate();
            float pastDayReportResponse = pastReport.getDayResponseRate();
            float pastNightReportResponse = pastReport.getNightResponseRate();
            float curReportResponse = curReport.getResponseRate();
            float curDayReportResponse = curReport.getDayResponseRate();
            float curNightReportResponse = curReport.getNightResponseRate();
            float diff1, diff2, diff3;
            String entireResult, dayResult, nightResult;
            if(pastReportResponse<curDayReportResponse){
                diff1=curReportResponse-pastReportResponse;
                entireResult="가 늘었어요.";
            }else if(pastReportResponse>curDayReportResponse){
                diff1=pastReportResponse-curReportResponse;
                entireResult="가 감소했어요.";
            }else{
                diff1=pastReportResponse;
                entireResult="로 그대로예요.";
            }
            if(pastDayReportResponse<curDayReportResponse){
                diff2=curDayReportResponse-pastDayReportResponse;
                dayResult="가 늘었고";
            }else if(pastDayReportResponse>curDayReportResponse){
                diff2=pastDayReportResponse-curDayReportResponse;
                dayResult="가 줄었고";
            }else{
                diff2=pastDayReportResponse;
                dayResult="로 그대로고";
            }
            if(pastNightReportResponse<curNightReportResponse){
                diff3=curNightReportResponse-pastNightReportResponse;
                nightResult="가 늘었어요.";
            }else if(pastNightReportResponse>curNightReportResponse){
                diff3=pastNightReportResponse-curNightReportResponse;
                nightResult="가 줄었어요.";
            }else{
                diff3=pastNightReportResponse;
                nightResult="로 그대로예요.";
            }
            curReport.setResponseRateTrend("마음체크 응답률은 "+(int)diff1+"%"+entireResult+"\n 아침 응답률은 "+(int)diff2+"%"+dayResult+", 밤 응답률은 "+(int)diff3+"%"+nightResult);
            return curReport;
    }


    private MindCheckReportPeriod getReportPeriod(Member member, List<MindChecks> mindChecks1, List<MindChecks> mindChecks2) {

        if(mindChecks1.isEmpty() && mindChecks2.isEmpty()){
            return new MindCheckReportPeriod();
        }
        //가중치 높은 list
        float score1=0;
        int cntPositiveEmotion=0;
        int cntQuestion1=0;
        int cntQuestion2=0;
        int cntQuestion3=0;
        Map<Emotion, Integer> emotionCountMap = new HashMap<>();
        float totalResponses = 0;
        int dayResponse = 0;
        int nightResponse = 0;
        for(int i=0; i<mindChecks1.size(); i++){
            MindChecks mindCheck = mindChecks1.get(i);
            if(mindCheck.getDayMindCheck()!=null && mindCheck.getNightMindCheck()!=null) {
                score1+= (float)(mindCheck.getDayMindCheck().getScore()*0.5+mindCheck.getNightMindCheck().getScore()*0.5);
                totalResponses+=2;
                emotionCountMap.put(mindCheck.getDayMindCheck().getEmotion(), emotionCountMap.getOrDefault(mindCheck.getDayMindCheck().getEmotion(), 0)+1);
                emotionCountMap.put(mindCheck.getNightMindCheck().getEmotion(), emotionCountMap.getOrDefault(mindCheck.getNightMindCheck().getEmotion(), 0)+1);
                dayResponse++;
                nightResponse++;

                if(mindCheck.getDayMindCheck().getEmotion().getId()<7) cntPositiveEmotion++;
                if(mindCheck.getNightMindCheck().getEmotion().getId()<7) cntPositiveEmotion++;

                if(mindCheck.getDayMindCheck().isQuestion1() || mindCheck.getNightMindCheck().isQuestion1()) cntQuestion1++;
                if(mindCheck.getDayMindCheck().isQuestion2() || mindCheck.getNightMindCheck().isQuestion2()) cntQuestion2++;
                if(mindCheck.getDayMindCheck().isQuestion3() || mindCheck.getNightMindCheck().isQuestion3()) cntQuestion3++;

            }else if(mindCheck.getDayMindCheck()!=null){
                score1+= (float)(mindCheck.getDayMindCheck().getScore());
                totalResponses++;
                emotionCountMap.put(mindCheck.getDayMindCheck().getEmotion(), emotionCountMap.getOrDefault(mindCheck.getDayMindCheck().getEmotion(), 0)+1);
                dayResponse++;

                if(mindCheck.getDayMindCheck().getEmotion().getId()<7) cntPositiveEmotion++;

                if(mindCheck.getDayMindCheck().isQuestion1()) cntQuestion1++;
                if(mindCheck.getDayMindCheck().isQuestion2()) cntQuestion2++;
                if(mindCheck.getDayMindCheck().isQuestion3()) cntQuestion3++;

            }else if(mindCheck.getNightMindCheck()!=null){
                score1+= (float)(mindCheck.getNightMindCheck().getScore());
                emotionCountMap.put(mindCheck.getNightMindCheck().getEmotion(), emotionCountMap.getOrDefault(mindCheck.getNightMindCheck().getEmotion(), 0)+1);
                totalResponses++;
                nightResponse++;

                if(mindCheck.getNightMindCheck().getEmotion().getId()<7) cntPositiveEmotion++;

                if(mindCheck.getNightMindCheck().isQuestion1()) cntQuestion1++;
                if(mindCheck.getNightMindCheck().isQuestion2()) cntQuestion2++;
                if(mindCheck.getNightMindCheck().isQuestion3()) cntQuestion3++;
            }
        }
        if(!mindChecks1.isEmpty()) score1/=mindChecks1.size();
        //가중치 적은 list
        float score2=0;
        for(int i=0; i<mindChecks2.size(); i++){
            MindChecks mindCheck = mindChecks2.get(i);
            if(mindCheck.getDayMindCheck()!=null && mindCheck.getNightMindCheck()!=null) {
                score2+= (float)(mindCheck.getDayMindCheck().getScore()*0.5+mindCheck.getNightMindCheck().getScore()*0.5);
                totalResponses+=2;
                emotionCountMap.put(mindCheck.getDayMindCheck().getEmotion(), emotionCountMap.getOrDefault(mindCheck.getDayMindCheck().getEmotion(), 0)+1);
                emotionCountMap.put(mindCheck.getNightMindCheck().getEmotion(), emotionCountMap.getOrDefault(mindCheck.getNightMindCheck().getEmotion(), 0)+1);
                dayResponse++;
                nightResponse++;

                if(mindCheck.getDayMindCheck().getEmotion().getId()<7) cntPositiveEmotion++;
                if(mindCheck.getNightMindCheck().getEmotion().getId()<7) cntPositiveEmotion++;

                if(mindCheck.getDayMindCheck().isQuestion1() || mindCheck.getNightMindCheck().isQuestion1()) cntQuestion1++;
                if(mindCheck.getDayMindCheck().isQuestion2() || mindCheck.getNightMindCheck().isQuestion2()) cntQuestion2++;
                if(mindCheck.getDayMindCheck().isQuestion3() || mindCheck.getNightMindCheck().isQuestion3()) cntQuestion3++;

            }else if(mindCheck.getDayMindCheck()!=null){
                score2+= (float)(mindCheck.getDayMindCheck().getScore());
                totalResponses++;
                emotionCountMap.put(mindCheck.getDayMindCheck().getEmotion(), emotionCountMap.getOrDefault(mindCheck.getDayMindCheck().getEmotion(), 0)+1);
                dayResponse++;

                if(mindCheck.getDayMindCheck().getEmotion().getId()<7) cntPositiveEmotion++;

                if(mindCheck.getDayMindCheck().isQuestion1()) cntQuestion1++;
                if(mindCheck.getDayMindCheck().isQuestion2()) cntQuestion2++;
                if(mindCheck.getDayMindCheck().isQuestion3()) cntQuestion3++;

            }else if(mindCheck.getNightMindCheck()!=null){
                score2+= (float)(mindCheck.getNightMindCheck().getScore());
                emotionCountMap.put(mindCheck.getNightMindCheck().getEmotion(), emotionCountMap.getOrDefault(mindCheck.getNightMindCheck().getEmotion(), 0)+1);
                totalResponses++;
                nightResponse++;

                if(mindCheck.getNightMindCheck().getEmotion().getId()<7) cntPositiveEmotion++;

                if(mindCheck.getNightMindCheck().isQuestion1()) cntQuestion1++;
                if(mindCheck.getNightMindCheck().isQuestion2()) cntQuestion2++;
                if(mindCheck.getNightMindCheck().isQuestion3()) cntQuestion3++;
            }
        }
        if(!mindChecks2.isEmpty()) score2/=mindChecks2.size();
        float score=(float)(score1*0.5+score2*0.5);
        //결과 저장
        MindCheckReportPeriod response = new MindCheckReportPeriod();
        //결과
        response.setResult(calculateScore(score));
        //문항별 응답률
        response.setResponseRateOfQ1(cntQuestion1);
        response.setResponseRateOfQ2(cntQuestion2);
        response.setResponseRateOfQ3(cntQuestion3);
        //마음체크 응답률

        response.setResponseRate((float)(totalResponses/28f*100));
        response.setDayResponseRate((float)(dayResponse/totalResponses*100));
        response.setNightResponseRate((float)(nightResponse/totalResponses*100));
        //긍/부 감정응답
        response.setPositiveEmotionRate((float)(cntPositiveEmotion/totalResponses*100));
        response.setNegativeEmotionRate((float)(100-(cntPositiveEmotion/totalResponses*100)));
        //top3 emotion
        List<Map.Entry<Emotion, Integer>> sortedEmotions = new ArrayList<>(emotionCountMap.entrySet());
        sortedEmotions.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        float finalTotalResponses = totalResponses;
        response.setTop3Emotions(
                sortedEmotions.stream()
                        .limit(3)
                        .map(entry -> {
                            return new MindCheckReportPeriod.EmotionRatio(
                                    entry.getKey().getId(),
                                    (entry.getValue() * 100f) / finalTotalResponses
                            );
                        })
                        .collect(Collectors.toList()));
        return response;
    }
    private String calculateScore(float score) {
        if(score>=75){
            return "안정";
        }else if(score>=50){
            return "주의";
        }else if(score>=25){
            return "경고";
        }else{
            return "고위험";
        }
    }
}
