package com.DreamOfDuck.pang.entity;

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
@Table(name="score")
public class Score extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="scoreId")
    private Long id;

    private Long score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="memberId")
    private Member host;

    //==연관관계 메서드==//
    public void addHost(Member member) {
        member.getScores().add(this);
        this.setHost(member);
    }
}
