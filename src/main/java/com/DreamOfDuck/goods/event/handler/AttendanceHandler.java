package com.DreamOfDuck.goods.event.handler;

import com.DreamOfDuck.goods.event.AttendanceCreatedEvent;
import com.DreamOfDuck.goods.service.AttendanceAsyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class AttendanceHandler {
    private final AttendanceAsyncService attendanceAsyncService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAttendance(AttendanceCreatedEvent event) {
        attendanceAsyncService.addAttendance(event.getEmail(), event.getDate());

    }
}
