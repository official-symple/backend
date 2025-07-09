package com.DreamOfDuck.talk.dto.request;

import com.DreamOfDuck.talk.entity.Session;
import com.DreamOfDuck.talk.entity.Talker;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageFormatF {
    @NotNull
    String role;

    String content;

    public static List<MessageFormatF> fromSession(Session session) {
        return session.getConversation().stream()
                .map(message ->MessageFormatF.builder()
                        .role(setRole(message.getTalker()))
                        .content(message.getContent()).build())
                .collect(Collectors.toList());
    }
    private static String setRole(Talker talker) {
        if(talker==Talker.USER){
            return "user";
        }else{
            return "assistant";
        }
    }
}
