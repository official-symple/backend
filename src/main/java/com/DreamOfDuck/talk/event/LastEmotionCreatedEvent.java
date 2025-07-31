package com.DreamOfDuck.talk.event;

import com.DreamOfDuck.account.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LastEmotionCreatedEvent {
    private Long sessionId;
    private Member member;
}
