package com.clush.clushbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateToDoRequest {

    // 할 일 수정 DTO
    private String toDoContent;
    private String priority;
    private String toDoStatus;
    private String tag;
    private String memo;
}
