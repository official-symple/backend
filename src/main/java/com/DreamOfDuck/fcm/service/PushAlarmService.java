package com.DreamOfDuck.fcm.service;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.fcm.dto.PushAlarmRequest;
import com.DreamOfDuck.fcm.dto.PushAlarmResponse;
import com.DreamOfDuck.fcm.entity.PushAlarm;
import com.DreamOfDuck.fcm.repository.PushAlarmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PushAlarmService {
    private final PushAlarmRepository pushAlarmRepository;
    @Transactional
    public PushAlarmResponse setAlarm(Member member, PushAlarmRequest request) {
        PushAlarm pushAlarm = member.getPushAlarm();

        if(pushAlarm == null) {
            PushAlarm newPushAlarm = PushAlarm.builder()
                    .random(request.isRandom())
                    .resultCheck(request.isResultCheck())
                    .reminder(request.isReminder())
                    .build();
            pushAlarmRepository.save(newPushAlarm);
            member.setPushAlarm(newPushAlarm);
            return PushAlarmResponse.from(newPushAlarm);
        }else{
            pushAlarm.setReminder(request.isReminder());
            pushAlarm.setRandom(request.isRandom());
            pushAlarm.setResultCheck(request.isResultCheck());
            return PushAlarmResponse.from(pushAlarm);
        }
    }
    public PushAlarmResponse getAlarm(Member member) {
        PushAlarm pushAlarm = member.getPushAlarm();
        if(pushAlarm == null) {
            return PushAlarmResponse.builder()
                    .resultCheck(true)
                    .reminder(true)
                    .random(true)
                    .build();
        }else{
            return PushAlarmResponse.from(pushAlarm);
        }
    }
}
