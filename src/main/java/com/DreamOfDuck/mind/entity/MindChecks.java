package com.DreamOfDuck.mind.entity;

import com.DreamOfDuck.account.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="mindChecks")
public class MindChecks{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="mindChecksId")
    private Long id;
    private LocalDate date;
    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private MindCheck dayMindCheck;
    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private MindCheck nightMindCheck;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="memberId")
    private Member host;

    //==연관관계 메서드==//
    public void addHost(Member member) {
        member.getMindChecks().add(this);
        this.setHost(member);
    }
}
