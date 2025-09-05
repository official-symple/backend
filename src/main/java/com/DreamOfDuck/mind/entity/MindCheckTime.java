package com.DreamOfDuck.mind.entity;

import com.DreamOfDuck.account.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="mindCheckTime")
public class MindCheckTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="mindCheckTimeId")
    private Long id;
    private DayOfWeek dayOfWeek;
    private LocalTime dayTime;
    private LocalTime nightTime;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="memberId")
    private Member host;
    //==연관관계 메서드==//
    public void addHost(Member member) {
        member.getMindCheckTimes().add(this);
        this.setHost(member);
    }
}
