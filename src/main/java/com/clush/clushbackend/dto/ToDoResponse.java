package com.clush.clushbackend.dto;

import com.clush.clushbackend.domain.ToDo;
import lombok.Getter;

@Getter
public class ToDoResponse {

    // 할 일 DTO
    private final int toDoNum;
    private final String toDoContent;
    private final String priority;
    private final String toDoStatus;
    private final String tag;
    private final String memo;

    // 할 일 데이터 값 가져오기
    public ToDoResponse(ToDo toDo) {

        this.toDoNum = toDo.getToDoNum();
        this.toDoContent = toDo.getToDoContent();
        this.priority = toDo.getPriority();
        this.toDoStatus = toDo.getToDoStatus();
        this.tag = toDo.getTag();
        this.memo = toDo.getMemo();

    }
}
