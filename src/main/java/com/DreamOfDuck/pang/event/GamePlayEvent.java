package com.DreamOfDuck.pang.event;

import com.DreamOfDuck.account.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GamePlayEvent {
    Member member;
    Long score;
}
