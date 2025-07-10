package com.DreamOfDuck.talk.entity;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.global.entity.TimeStamp;
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
@Table(name="Interview")
public class Interview extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="interviewId")
    private Long id;

    private String question1;
    private String question2;
    private String question3;
    private String question3_2;
    private String question3_3;
    private String question4;
    private String question5;
    private String question5_2;
    private String question6;
    private String question6_2;
    private String question6_3;
    private String question7;
    private String question8;
    private String question8_2;
    private String question9;
    private String question9_2;
    private String question10;

    @OneToOne
    @JoinColumn(name="memberId")
    private Member host;
}
