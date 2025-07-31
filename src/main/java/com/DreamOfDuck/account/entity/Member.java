package com.DreamOfDuck.account.entity;

import com.DreamOfDuck.feedback.Feedback;
import com.DreamOfDuck.global.entity.TimeStamp;
import com.DreamOfDuck.pang.entity.Item;
import com.DreamOfDuck.record.entity.Health;
import com.DreamOfDuck.talk.entity.Cause;
import com.DreamOfDuck.talk.entity.Interview;
import com.DreamOfDuck.talk.entity.Session;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="member")
public class Member extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="memberId")
    private Long id;
    private String email;
    private String password;
    private String nickname;
    private LocalDate birthday;
    @Enumerated(EnumType.STRING)
    private SocialType socialType;
    @Enumerated(EnumType.STRING)
    private Role role;
    private Boolean isMarketing;
    private Gender gender;
    @Enumerated(EnumType.STRING)
    private Cause concern;

    @ElementCollection
    @CollectionTable(name = "member_status", joinColumns = @JoinColumn(name = "member_id"))
    @Column(name = "status")
    private List<Integer> status =  new ArrayList<>();
    private String totalStatus;

    private Integer maxScore;
    private Integer heart;
    private Integer dia;
    private Integer feather;
    private Integer lv;
    private String duckname;

    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Session> sessions = new ArrayList<>();

    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Health> record = new ArrayList<>();

    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Feedback> feedback = new ArrayList<>();

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Item item;

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Interview interview;

}
