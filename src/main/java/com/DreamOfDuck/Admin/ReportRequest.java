package com.DreamOfDuck.Admin;

import com.DreamOfDuck.talk.dto.request.MessageFormatF;
import jakarta.validation.constraints.NotNull;
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

