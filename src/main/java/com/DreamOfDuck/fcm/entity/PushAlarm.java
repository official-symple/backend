package com.DreamOfDuck.fcm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="pushAlarm")
public class PushAlarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="pushAlarmId")
    private Long id;
    private boolean reminder;
    private boolean resultCheck;
    private boolean random;
}
