package com.clush.clushbackend.dto;

import com.clush.clushbackend.domain.ToDo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddToDoRequest {

    // 할 일 추가 DTO
    private String toDoContent;

    private String priority;

    private String toDoStatus;

    private String tag;

    private String memo;

    // 할 일 추가 DTO를 Entity로 변환하기
    public ToDo toEntity(){
        return ToDo.builder().toDoContent(toDoContent).
                priority(priority).toDoStatus(toDoStatus).tag(tag).memo(memo).build();
    }


}
