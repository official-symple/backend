package com.DreamOfDuck.admin;

import com.DreamOfDuck.talk.entity.Emotion;
import com.DreamOfDuck.talk.entity.Session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponseA {
    Long sessionId;
    LocalDate date;
    String nickname;
    Integer age;
    String gender;
    String blueScore;

    Integer duckType;
    String cause;
    List<String> emotions;
    String lastEmotion;
    String problem;
    private List<String> solutions;


    public static ReportResponseA from(Session session){
        return ReportResponseA.builder()
                .sessionId(session.getId())
                .date(session.getUpdatedAt().toLocalDate())
                .nickname(session.getHost().getNickname())
                .age(Period.between(session.getHost().getBirthday(), LocalDate.now()).getYears())
                .gender(session.getHost().getGender().toString())
                .blueScore(session.getHost().getTotalStatus())
                .duckType(session.getDuckType().getValue())
                .cause(session.getCause().getText())
                .emotions(session.getEmotion().stream().map(Emotion::getText).collect(Collectors.toList()))
                .lastEmotion(session.getLastEmotion().getText())
                .problem(session.getProblem())
                .solutions(session.getSolutions())
                .build();
    }
}
