package com.DreamOfDuck.account.entity;

import com.DreamOfDuck.feedback.entity.Feedback;
import com.DreamOfDuck.global.entity.TimeStamp;
import com.DreamOfDuck.pang.entity.Score;
import com.DreamOfDuck.record.entity.Goal;
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
import java.util.*;

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
    private Language language;
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
    //꽥팡
    private Integer maxScore;
    private Long cntPlaying;
    //출석
    @ElementCollection
    @CollectionTable(name = "member_attendance", joinColumns = @JoinColumn(name = "member_id"))
    @Column(name = "attendance_date")
    private Set<LocalDate> attendedDates = new TreeSet<>();
    private Integer curStreak=0;
    private Integer longestStreak=0;
    private LocalDate LastDayOfLongestStreak;
    //재화
    private Integer heart;
    private Integer dia;
    private Integer feather;

    private Integer lv;
    //캐릭터-스토어 이후 분리하기
    private String duckname;
    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Session> sessions = new ArrayList<>();

    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Health> record = new ArrayList<>();

    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Goal> goal = new ArrayList<>();

    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Feedback> feedback = new ArrayList<>();

    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Score> scores = new ArrayList<>();

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Interview interview;

}
