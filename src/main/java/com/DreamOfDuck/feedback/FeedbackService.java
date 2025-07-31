package com.DreamOfDuck.feedback;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.record.dto.request.HealthCreateRequest;
import com.DreamOfDuck.record.dto.response.HealthResponse;
import com.DreamOfDuck.record.entity.Health;
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
