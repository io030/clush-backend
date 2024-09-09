package com.clush.clushbackend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ToDo {
    // Entity 클래스

    // PK , 자동 증가 생성
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "toDoNum",updatable = false)
    private int toDoNum;

    // 할 일 내용 , null X
    @Column(name="toDoContent",nullable = false)
    private String toDoContent;

    // 우선순위 , null X
    @Column(name="priority",nullable = false)
    @Check(constraints = "(priority IN ('high', 'normal', 'low'))")
    private String priority;

    // 할 일 상태, null X
    @Column(name="toDoStatus",nullable = false)
    @Check(constraints = "(to_do_status IN ('in_progress','done', 'yet'))")
    private String toDoStatus;

    // 태그 , null X
    @Column(name="tag",nullable = false)
    @Check(constraints = "(tag IN ('work', 'hobby', 'study', 'etc'))")
    private String tag;

    // 메모 , null O
    @Column(name="memo",nullable = true)
    private String memo;

    // 빌더를 사용하여 어디에 무슨 값이 들어가는지 좀 더 쉽게 확인 가능
    @Builder
    public ToDo(String toDoContent,String priority,String toDoStatus,String tag, String memo ){

        this.toDoContent = toDoContent;
        this.priority = priority;
        this.toDoStatus = toDoStatus;
        this.tag = tag;
        this.memo = memo;

    }

    // 할 일 수정
    public void update(String toDoContent,String priority,String toDoStatus, String tag,String memo){
        this.toDoContent = toDoContent;
        this.priority = priority;
        this.toDoStatus= toDoStatus;
        this.tag  = tag;
        this.memo = memo;

    }
}
