package com.DreamOfDuck.account.entity;

import com.DreamOfDuck.global.entity.TimeStamp;
import com.DreamOfDuck.talk.entity.Cause;
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
@Table(name="member")
public class Member extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id")
    private Long id;

    private String email;

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
    private Integer blue;

}
