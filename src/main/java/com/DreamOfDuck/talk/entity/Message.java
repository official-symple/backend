package com.DreamOfDuck.talk.entity;

import com.DreamOfDuck.global.entity.TimeStamp;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="Message")
public class Message extends TimeStamp {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="messageId")
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="sessionId")
    private Session session;

    private Talker talker;
    private String content;

    //==연관관계 메서드==//
    public void addConversation(Session session) {
        session.getConversation().add(this);
        this.setSession(session);
    }

}
