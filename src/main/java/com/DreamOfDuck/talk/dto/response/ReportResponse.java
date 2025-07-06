package com.DreamOfDuck.talk.dto.response;

import com.DreamOfDuck.talk.entity.Emotion;
import com.DreamOfDuck.talk.entity.Session;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    @Schema(example="2025-05-08")
    LocalDate date;
    @Schema(example="[1,10, 30]")
    List<Integer> emotions;
    @Schema(example="4")
    Integer last_emotion;
    @Schema(example="내가 맡은 모든 일들을 잘하고 싶은데 오점이 생겼다는 생각에 너무 화가 나")
    String problem;
    @Schema(example = "[\"‘오점이 생겼다’ 대신, '이번엔 실수가 있었지만 배울 기회가 생겼어'라고 생각해보면 어떨까? 실패를 성장가능한 과정으로 보는 연습을 하면 마음이 가벼워질 거야.\", \"모든 일을 완벽히 해내야 한다는 목표보다는, 하루에 하나씩 작은 성취를 이루는 데 집중해보자.\"]")
    private List<String> solutions;


    public static ReportResponse from(Session session){
        return ReportResponse.builder()
                .date(session.getUpdatedAt().toLocalDate())
                .emotions(session.getEmotion().stream().map(Emotion::getId).collect(Collectors.toList()))
                .last_emotion(session.getLast_emotion().getId())
                .problem(session.getProblem())
                .solutions(session.getSolutions())
                .build();
    }
}
