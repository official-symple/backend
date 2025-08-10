package com.DreamOfDuck.record.service;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import com.DreamOfDuck.record.dto.request.GoalCreateRequest;
import com.DreamOfDuck.record.dto.response.GoalResponse;
import com.DreamOfDuck.record.dto.response.GoalsResponse;
import com.DreamOfDuck.record.entity.Goal;
import com.DreamOfDuck.record.entity.HealthType;
import com.DreamOfDuck.record.repository.GoalRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly=true)
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;

    @Transactional
    public GoalResponse createGoal(Member host, GoalCreateRequest request){
        List<Goal> goals=goalRepository.findByHost(host);
        boolean existsType=goals.stream().anyMatch(g->g.getHealthType()==HealthType.valueOf(request.getHealthType().toUpperCase()));
        if(existsType){
            throw new CustomException(ErrorCode.GOAL_ALREADY_EXIST);
        }
        Goal goal = Goal.builder()
                .healthType(HealthType.valueOf(request.getHealthType().toUpperCase()))
                .value(request.getValue())
                .isSuccess(false)
                .build();
        goal.addHost(host);
        goalRepository.save(goal);
        return GoalResponse.from(goal);
    }
    @Transactional
    public GoalResponse updateGoal(Member host, Long id, GoalCreateRequest request){
        Goal goal = goalRepository.findById(id).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_GOAL));
        if(goal.getHost()!=host){
            throw new CustomException(ErrorCode.DIFFERENT_USER_GOAL);
        }
        goal.setHealthType(HealthType.valueOf(request.getHealthType().toUpperCase()));
        goal.setValue(request.getValue());
        return GoalResponse.from(goal);
    }
    @Transactional
    public GoalResponse toggleSuccess(Member host, Long id){
        Goal goal = goalRepository.findById(id).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_GOAL));
        if(goal.getHost()!=host){
            throw new CustomException(ErrorCode.DIFFERENT_USER_GOAL);
        }
        boolean cur=goal.getIsSuccess();
        goal.setIsSuccess(!cur);
        return GoalResponse.from(goal);
    }
    public GoalsResponse getGoals(Member host){
        List<Goal> goals= goalRepository.findByHost(host);
        List<GoalResponse> goalResponses = goals.stream().map(GoalResponse::from).collect(Collectors.toList());
        return GoalsResponse.builder()
                .goals(goalResponses)
                .build();
    }
}
