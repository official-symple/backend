package com.DreamOfDuck.talk.entity;

import com.DreamOfDuck.global.entity.TimeStamp;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="Message")
public class Message extends TimeStamp {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="messageId")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="sessionId")
    @NotNull
    private Session session;

    @NotNull
    private Talker talker;
    @Lob
    @NotNull
    private String content;

    @Lob
    @NotNull
    private List<String> contents;


    //==연관관계 메서드==//
    public void addSession(Session session) {
        session.getConversation().add(this);
        this.setSession(session);
    }
}
