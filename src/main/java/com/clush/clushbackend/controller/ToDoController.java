package com.clush.clushbackend.controller;

import com.clush.clushbackend.domain.ToDo;
import com.clush.clushbackend.dto.AddToDoRequest;
import com.clush.clushbackend.dto.ToDoResponse;
import com.clush.clushbackend.dto.UpdateToDoRequest;
import com.clush.clushbackend.service.ToDoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "To Do", description = "To Do API")
@RestController
public class ToDoController {

    // Service 생성자 주입
    private final ToDoService ts;

    @Autowired
    public ToDoController(ToDoService ts){
        this.ts = ts;
    }

    // 할 일 생성
    @PostMapping("/todo/new")
    @Operation(summary = "할 일 추가", description = "자신이 해야 할 일을 목록에 추가한다")
    public ResponseEntity<ToDo> addToDo(@RequestBody AddToDoRequest request){

        ToDo newToDo = ts.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(newToDo);
    }

    // 할 일 목록 가져오기
    @GetMapping("/todo/list")
    @Operation(summary = "할 일 목록", description = "자신이 추가한 할 일 목록을 조회한다")
    public ResponseEntity<List<ToDoResponse>> findAllToDo(){
        List<ToDoResponse> toDoList = ts.findAll()
                .stream()
                .map(ToDoResponse::new)
                .toList();

        return ResponseEntity.ok().body(toDoList);

    }

    // 특정 할 일 가져오기
    @GetMapping("/todo/{toDoNum}")
    @Operation(summary = "할 일 조회", description = "toDoNum을 매개변수로 받아 선택한 할 일을 조회한다 ")
    @Parameters({
            @Parameter(name = "toDoNum", description = "할 일의 int 자료형인 PK", example = "10")
    })
    public ResponseEntity<ToDoResponse> findToDo(@PathVariable int toDoNum){
        ToDo findToDo = ts.findByToDoNum(toDoNum);

        return ResponseEntity.ok().body(new ToDoResponse(findToDo));
    }

    // 특정 할 일 삭제하기
    @DeleteMapping("/todo/{toDoNum}")
    @Operation(summary = "할 일 삭제", description = "toDoNum을 매개변수로 받아 선택한 할 일을 삭제한다 ")
    @Parameters({
            @Parameter(name = "toDoNum", description = "할 일의 int 자료형인 PK", example = "10")
    })
    public ResponseEntity<Void> deleteToDo(@PathVariable int toDoNum){
        ts.delete(toDoNum);

        return ResponseEntity.ok().build();
    }

    // 특정 할 일 수정하기
    @PutMapping("/todo/{toDoNum}")
    @Operation(summary = "할 일 수정", description = "toDoNum을 매개변수로 받아 선택한 할 일을 수정한다 ")
    @Parameters({
            @Parameter(name = "toDoNum", description = "할 일의 int 자료형인 PK", example = "10")
    })
    public ResponseEntity<ToDo> updateToDo(@PathVariable int toDoNum, @RequestBody UpdateToDoRequest request){
        ToDo updatedToDo = ts.update(toDoNum, request);

        return ResponseEntity.ok().body(updatedToDo);
    }



    // 할 일 목록 중 특정 우선순위(high , normal , low) 목록 가져오기
    @GetMapping("/todo/priority/{priority}")
    @Operation(summary = "할 일 우선순위 검색", description = "priority을 매개변수로 받아 선택한 우선순위의 할 일을 조회한다 ")
    @Parameters({
            @Parameter(name = "priority", description = "할 일의 String 자료형인 priority 컬럼, {high, normal, low} 값 만 가질 수 있다", example = "high")
    })
    public ResponseEntity<List<ToDoResponse>>  findToDoPriority(@PathVariable String priority){

        List<ToDoResponse> findPriority = ts.findByToDoPriority(priority)
                .stream()
                .map(ToDoResponse::new)
                .toList();

        return ResponseEntity.ok().body(findPriority);
    }

    // 할 일 목록 중 특정 작업상태(in_progress, done, yet) 목록 가져오기
    @GetMapping("/todo/status/{status}")
    @Operation(summary = "할 일 작업상태 검색", description = "status을 매개변수로 받아 선택한 작업상태인 할 일을 조회한다 ")
    @Parameters({
            @Parameter(name = "status", description = "할 일의 String 자료형인 status 컬럼, {in_progress, done, yet} 값 만 가질 수 있다", example = "done")
    })
    public ResponseEntity<List<ToDoResponse>>  findToDoStatus(@PathVariable String status){

        List<ToDoResponse> findStatus = ts.findByToDoStatus(status)
                .stream()
                .map(ToDoResponse::new)
                .toList();

        return ResponseEntity.ok().body(findStatus);
    }

    // 할 일 목록 중 특정 태그(work, hobby, study, etc) 목록 가져오기
    @GetMapping("/todo/tag/{tag}")
    @Operation(summary = "할 일 태그 검색", description = "tag를 매개변수로 받아 선택한 태그인 할 일을 조회한다 ")
    @Parameters({
            @Parameter(name = "tag", description = "할 일의 String 자료형인 tag 컬럼, {work, hobby, study, etc} 값 만 가질 수 있다", example = "work")
    })
    public ResponseEntity<List<ToDoResponse>>  findToDoTag(@PathVariable String tag){

        List<ToDoResponse> findTag = ts.findByToDoTag(tag)
                .stream()
                .map(ToDoResponse::new)
                .toList();

        return ResponseEntity.ok().body(findTag);
    }


}
