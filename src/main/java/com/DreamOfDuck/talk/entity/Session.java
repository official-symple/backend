package com.DreamOfDuck.talk.entity;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.global.entity.TimeStamp;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="Session")
public class Session extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="sessionId")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Talker duckType;

    private Boolean isFormal;

    @ElementCollection
    @CollectionTable(name = "session_emotion", joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "emotion")
    private List<Emotion> emotion = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private LastEmotion last_emotion;
    private String input_field; //last_emotion 4(기타)선택 시

    private Cause cause;
    @OneToMany(mappedBy="session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> conversation = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="memberId")
    private Member host;

    //==연관관계 메서드==//
    public void addHost(Member member) {
        member.getSessions().add(this);
        this.setHost(member);
    }
}
