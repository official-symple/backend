package com.DreamOfDuck.account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Attendance implements Comparable<Attendance> {
    @Column(name = "attendance_date")
    private LocalDate date;

    private Boolean isIce;

    @Override
    public int compareTo(Attendance o) {
        return o.date.compareTo(this.date);
    }
}

