package com.DreamOfDuck.record.service;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import com.DreamOfDuck.record.repository.HealthRepository;
import com.DreamOfDuck.record.dto.request.HealthCreateRequest;
import com.DreamOfDuck.record.dto.request.HealthUpdateRequest;
import com.DreamOfDuck.record.dto.response.HealthResponse;
import com.DreamOfDuck.record.entity.Health;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly=true)
@RequiredArgsConstructor
public class HealthService{
    private final HealthRepository healthRepository;

    @Transactional
    public HealthResponse save(Member host, HealthCreateRequest request) {
        Health health = healthRepository.findByDateAndHost(request.getDate(), host).orElse(null);
       if (health == null) {
           Health newhealth = Health.builder()
                   .walking(request.getWalking())
                   .sleeping(request.getSleeping())
                   .heartbeat(request.getHeartbeat())
                   .screenTime(request.getScreenTime())
                   .lightening(request.getLightening())
                   .diary(request.getDiary())
                   .date(request.getDate())
                   .build();
           healthRepository.save(newhealth);
           newhealth.addHost(host);
       }else{
           health.setWalking(request.getWalking());
           health.setSleeping(request.getSleeping());
           health.setHeartbeat(request.getHeartbeat());
           health.setScreenTime(request.getScreenTime());
           health.setLightening(request.getLightening());
           health.setDiary(request.getDiary());
       }
        return HealthResponse.from(health);
    }

    @Transactional
    public HealthResponse updateDiary(Member host, Long healthId, String diary) {
        Health health = healthRepository.findById(healthId).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_HEALTH));
        if(health.getHost()!=host){
            throw new CustomException(ErrorCode.DIFFERENT_USER_HEALTH);
        }
        health.setDiary(diary);
        return HealthResponse.from(health);
    }

    @Transactional
    public HealthResponse updateHealth(Member host, Long healthId, HealthUpdateRequest request) {
        Health health = healthRepository.findById(healthId).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_HEALTH));
        if(health.getHost()!=host){
            throw new CustomException(ErrorCode.DIFFERENT_USER_HEALTH);
        }
        health.setWalking(request.getWalking());
        health.setSleeping(request.getSleeping());
        health.setHeartbeat(request.getHeartbeat());
        health.setScreenTime(request.getScreenTime());
        health.setLightening(request.getLightening());
        return HealthResponse.from(health);
    }

    @Transactional
    public HealthResponse updateDate(Member host, Long healthId, LocalDate date){
        Health health = healthRepository.findById(healthId).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_HEALTH));
        if(health.getHost()!=host){
            throw new CustomException(ErrorCode.DIFFERENT_USER_HEALTH);
        }
        health.setDate(date);
        return HealthResponse.from(health);
    }

    public HealthResponse getRecordById(Member host, Long healthId) {
        Health health = healthRepository.findById(healthId).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_HEALTH));
        if(health.getHost()!=host){
            throw new CustomException(ErrorCode.DIFFERENT_USER_HEALTH);
        }
        return HealthResponse.from(health);
    }
    public HealthResponse getRecordByDate(Member host, LocalDate date) {
        Health health = healthRepository.findByDateAndHost(date, host).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_HEALTH));
        if(health.getHost()!=host){
            throw new CustomException(ErrorCode.DIFFERENT_USER_HEALTH);
        }
        return HealthResponse.from(health);
    }
    public List<HealthResponse> getRecordsByDatePeriodAndHost(Member host, LocalDate startDate, LocalDate endDate) {
        List<Health> records = healthRepository.findByDatePeriodAndHost(startDate, endDate, host);
        if(!records.isEmpty() && records.get(0).getHost()!=host){
            throw new CustomException(ErrorCode.DIFFERENT_USER_HEALTH);
        }
        records.sort(Comparator.comparing(Health::getDate));
        return records.stream().map(HealthResponse::from).collect(Collectors.toList());
    }
    public List<HealthResponse> getRecordsByHost(Member host) {
        List<Health> records = healthRepository.findByHost(host);
        records.sort(Comparator.comparing(Health::getDate));
        return records.stream().map(HealthResponse::from).collect(Collectors.toList());
    }

    @Transactional
    public void delete(Member host, Long healthId) {
        Health health = healthRepository.findById(healthId).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_HEALTH));
        if(health.getHost()!=host){
            throw new CustomException(ErrorCode.DIFFERENT_USER_HEALTH);
        }
        healthRepository.delete(health);
    }


}
