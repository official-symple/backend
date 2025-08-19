package com.DreamOfDuck.feedback.entity;

import com.DreamOfDuck.account.entity.Member;
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
@Table(name="feedback")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="feedbackId")
    private Long id;

    private Integer star;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="memberId")
    private Member host;

    //==연관관계 메서드==//
    public void addHost(Member member) {
        member.getFeedback().add(this);
        this.setHost(member);
    }
}
