package com.DreamOfDuck.mind.entity;

import com.DreamOfDuck.global.entity.TimeStamp;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="mindCheck")
public class MindCheck{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="mindChecksId")
    private Long id;
    private boolean question1;
    private boolean question2;
    private boolean question3;

    private PositiveEmotion positiveEmotion;
    private NegativeEmotion negativeEmotion;
    private float score;
    private LocalDateTime createTime;

}
