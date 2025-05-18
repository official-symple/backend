package com.DreamOfDuck.talk.dto.response;

import com.DreamOfDuck.talk.entity.Emotion;
import com.DreamOfDuck.talk.entity.Message;
import com.DreamOfDuck.talk.entity.Session;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
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
public class SessionResponse {
    @Schema(example="1")
    Long sessionId;
    @Schema(
            example="1",
            description="꽥둥이=1, 꽥돌이=2, 꽥둑이=3"
    )
    Integer duckType;
    @Schema(example="[1,10, 30]")
    List<Integer> emotion;
    @Schema(example="1")
    Integer cause;
    @Schema(example="4")
    Integer last_emotion;
    @Schema(example="흠 잘 모르겠어")
    String input_field;
    @Schema(example="[\n" +
            "        {\n" +
            "            \"messageId\": 1,\n" +
            "            \"sessionId\": 1,\n" +
            "            \"talker\": 0,\n" +
            "            \"content\": \"오리 ㅎㅇ\",\n" +
            "            \"date\": \"2025-05-08\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"messageId\": 2,\n" +
            "            \"sessionId\": 1,\n" +
            "            \"talker\": 1,\n" +
            "            \"content\": \"꽥\",\n" +
            "            \"date\": \"2025-05-08\"\n" +
            "        }\n" +
            "    ]")
    List<MessageFormat> conversation;
    @Schema(example="2025-05-06")
    LocalDate date;
    public static SessionResponse from(Session session) {
        return SessionResponse.builder()
                .sessionId(session.getId())
                .duckType(session.getDuckType().getValue())
                .emotion(session.getEmotion().stream()
                        .map(Emotion::getId)
                        .collect(Collectors.toList()))
                .cause(session.getCause().getId())
                .last_emotion(session.getLast_emotion()!=null ? session.getLast_emotion().getId() : null)
                .input_field(session.getInput_field()!=null ? session.getInput_field() : null)
                .conversation(session.getConversation()!=null ?
                        session.getConversation().stream()
                        .map(MessageFormat::from)
                        .collect(Collectors.toList()): null)
                .date(session.getCreatedAt().toLocalDate())
                .build();
    }
}
