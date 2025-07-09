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
                    .question4(interviewCreateRequest.getQuestion4())
                    .question5(interviewCreateRequest.getQuestion5())
                    .question6(interviewCreateRequest.getQuestion6())
                    .question7(interviewCreateRequest.getQuestion7())
                    .question8(interviewCreateRequest.getQuestion8())
                    .question9(interviewCreateRequest.getQuestion9())
                    .host(member)
                    .build();

            interviewRepository.save(newInterview);
            return InterviewResponse.from(newInterview);
        }else{
            interview.setQuestion1(interviewCreateRequest.getQuestion1());
            interview.setQuestion2(interviewCreateRequest.getQuestion2());
            interview.setQuestion3(interviewCreateRequest.getQuestion3());
            interview.setQuestion4(interviewCreateRequest.getQuestion4());
            interview.setQuestion5(interviewCreateRequest.getQuestion5());
            interview.setQuestion6(interviewCreateRequest.getQuestion6());
            interview.setQuestion7(interviewCreateRequest.getQuestion7());
            interview.setQuestion8(interviewCreateRequest.getQuestion8());
            interview.setQuestion9(interviewCreateRequest.getQuestion9());
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
