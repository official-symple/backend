package com.DreamOfDuck.record.entity;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.global.entity.TimeStamp;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="Goal")
public class Goal extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="interviewId")
    private Long id;

    private HealthType healthType;
    private Integer value;
    private Boolean isSuccess;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="memberId")
    private Member host;

    //==연관관계 메서드==//
    public void addHost(Member member) {
        member.getGoal().add(this);
        this.setHost(member);
    }
}
