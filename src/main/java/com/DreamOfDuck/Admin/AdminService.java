package com.DreamOfDuck.Admin;

import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import com.DreamOfDuck.talk.dto.response.ReportResponse;
import com.DreamOfDuck.talk.entity.Session;
import com.DreamOfDuck.talk.repository.SessionRepository;
import com.DreamOfDuck.talk.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {
    private final SessionRepository sessionRepository;
    SessionService sessionService;
    List<ReportResponse> getAllReport(){
        List<Session> sessions = sessionRepository.findAll();
        return sessions.stream()
                .filter(session ->
                        (session.getProblem() == null || session.getProblem().isEmpty())
                                && session.getSolutions().isEmpty()
                )
                .map(session -> ReportResponse.from(session))
                .collect(Collectors.toList());
    }
}
