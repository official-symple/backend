package com.DreamOfDuck.talk.entity;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.global.entity.TimeStamp;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    private Talker duckType;

    private Boolean isFormal;

    @ElementCollection
    @CollectionTable(name = "session_emotion", joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "emotion")
    @NotNull
    private List<Emotion> emotion = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @NotNull
    private LastEmotion last_emotion;
    private String input_field; //last_emotion 4(기타)선택 시

    @NotNull
    private Cause cause;

    @Lob
    private String problem;

    @ElementCollection
    @CollectionTable(name = "session_solutions", joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "solution")
    private List<String> solutions =  new ArrayList<>();

    @Lob
    private String mission;

    @ElementCollection
    @CollectionTable(name = "session_advice", joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "advice")
    private List<String> advice =  new ArrayList<>();

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
