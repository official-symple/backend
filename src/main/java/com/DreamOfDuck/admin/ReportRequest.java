package com.DreamOfDuck.admin;

import com.DreamOfDuck.talk.dto.request.MessageFormatF;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {
    Integer persona;
    Boolean formal;
    List<MessageFormatF> messages;
}

