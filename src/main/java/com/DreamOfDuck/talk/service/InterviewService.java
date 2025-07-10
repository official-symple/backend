package com.DreamOfDuck.talk.service;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.repository.MemberRepository;
import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import com.DreamOfDuck.talk.dto.request.InterviewCreateRequest;
import com.DreamOfDuck.talk.dto.response.InterviewResponse;
import com.DreamOfDuck.talk.entity.Interview;
import com.DreamOfDuck.talk.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InterviewService {
    private final InterviewRepository interviewRepository;

    @Transactional
    public InterviewResponse save(Member member, InterviewCreateRequest interviewCreateRequest) {
        Interview interview = interviewRepository.findByHost(member).orElse(null);
        if(interview == null){
            Interview newInterview = Interview.builder()
                    .question1(interviewCreateRequest.getQuestion1())
                    .question2(interviewCreateRequest.getQuestion2())
                    .question3(interviewCreateRequest.getQuestion3())
                    .question3_2(interviewCreateRequest.getQuestion3_2())
                    .question3_3(interviewCreateRequest.getQuestion3_3())
                    .question4(interviewCreateRequest.getQuestion4())
                    .question5(interviewCreateRequest.getQuestion5())
                    .question5_2(interviewCreateRequest.getQuestion5_2())
                    .question6(interviewCreateRequest.getQuestion6())
                    .question6_2(interviewCreateRequest.getQuestion6_2())
                    .question6_3(interviewCreateRequest.getQuestion6_3())
                    .question7(interviewCreateRequest.getQuestion7())
                    .question8(interviewCreateRequest.getQuestion8())
                    .question8_2(interviewCreateRequest.getQuestion8_2())
                    .question9(interviewCreateRequest.getQuestion9())
                    .question9_2(interviewCreateRequest.getQuestion9_2())
                    .question10(interviewCreateRequest.getQuestion10())
                    .host(member)
                    .build();

            interviewRepository.save(newInterview);
            return InterviewResponse.from(newInterview);
        }else{
            interview.setQuestion1(interviewCreateRequest.getQuestion1());
            interview.setQuestion2(interviewCreateRequest.getQuestion2());
            interview.setQuestion3(interviewCreateRequest.getQuestion3());
            interview.setQuestion3_2(interviewCreateRequest.getQuestion3_2());
            interview.setQuestion3_3(interviewCreateRequest.getQuestion3_3());
            interview.setQuestion4(interviewCreateRequest.getQuestion4());
            interview.setQuestion5(interviewCreateRequest.getQuestion5());
            interview.setQuestion5_2(interviewCreateRequest.getQuestion5_2());
            interview.setQuestion6(interviewCreateRequest.getQuestion6());
            interview.setQuestion6_2(interviewCreateRequest.getQuestion6_2());
            interview.setQuestion6_3(interviewCreateRequest.getQuestion6_3());
            interview.setQuestion7(interviewCreateRequest.getQuestion7());
            interview.setQuestion8(interviewCreateRequest.getQuestion8());
            interview.setQuestion8_2(interviewCreateRequest.getQuestion8_2());
            interview.setQuestion9(interviewCreateRequest.getQuestion9());
            interview.setQuestion9_2(interviewCreateRequest.getQuestion9_2());
            interview.setQuestion10(interviewCreateRequest.getQuestion10());
            return InterviewResponse.from(interview);

        }
    }
    public InterviewResponse getInterviewByHost(Member member){
        Interview interview = interviewRepository.findByHost(member).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_INTERVIEW));
        return InterviewResponse.from(interview);
    }

    @Transactional
    public void deleteById(Member member, Long id) {
        Interview interview = interviewRepository.findById(id).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_INTERVIEW));
        if(interview.getHost()!=member){
            throw new CustomException(ErrorCode.DIFFERENT_USER_INTERVIEW);
        }
        interviewRepository.delete(interview);
    }
    @Transactional
    public void deleteByUser(Member member) {
        Interview interview = interviewRepository.findByHost(member).orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_INTERVIEW));
        interviewRepository.delete(interview);
    }

}
