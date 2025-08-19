package com.DreamOfDuck.feedback.service;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.feedback.entity.Feedback;
import com.DreamOfDuck.feedback.repository.FeedbackRepository;
import com.DreamOfDuck.feedback.dto.FeedbackCreateRequest;
import com.DreamOfDuck.feedback.dto.FeedbackResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly=true)
@RequiredArgsConstructor
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    @Transactional
    public FeedbackResponse save(Member host, FeedbackCreateRequest request) {
        Feedback feedback = Feedback.builder()
                .star(request.getStar())
                .content(request.getContent())
                .build();
        feedback.addHost(host);
        feedbackRepository.save(feedback);
        return FeedbackResponse.from(feedback);
    }

}
