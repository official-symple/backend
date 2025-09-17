package com.DreamOfDuck.account.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Attendance {
    private LocalDate date;
    private Boolean isIce;
}
