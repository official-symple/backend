package com.DreamOfDuck.account.event;

import com.DreamOfDuck.account.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceCreatedEvent {
    private String email;
    private LocalDate date;
}
