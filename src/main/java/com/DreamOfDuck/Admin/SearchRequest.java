package com.DreamOfDuck.Admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {
    String gender;
    Integer age;
    String cause;
    String blueScore;
    String duckType;
    String nickname;
}
