package com.clush.clushbackend.service;

import com.clush.clushbackend.domain.ToDo;
import com.clush.clushbackend.dto.AddToDoRequest;
import com.clush.clushbackend.dto.UpdateToDoRequest;
import com.clush.clushbackend.repository.ToDoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ToDoService {

    // Repository 생성자 주입
    private final ToDoRepository tr;

    @Autowired
    public ToDoService(ToDoRepository tr){
        this.tr = tr;
    }

    // 할 일 추가하기
    public ToDo save(AddToDoRequest request) {

        return tr.save(request.toEntity());
    }

    // 할 일 전체 가져오기
    public List<ToDo> findAll() {
        return tr.findAll();
    }

    // 특정 할 일 가져오기
    public ToDo findByToDoNum(int toDoNum){
        return tr.findById(toDoNum).orElseThrow(()-> new IllegalArgumentException("not found : " + toDoNum));
    }

    // 할 일 삭제
    public void delete(int toDoNum){
        tr.deleteById(toDoNum);
    }

    // 할 일 수정하기
    @Transactional
    public ToDo update(int toDoNum, UpdateToDoRequest request){
        ToDo toDo = tr.findById(toDoNum)
                .orElseThrow(()->new IllegalArgumentException("not found: " + toDoNum));

        toDo.update(request.getToDoContent(),request.getPriority(),request.getToDoStatus(),request.getTag(),request.getMemo());

        return toDo;

    }

    public List<ToDo> findByToDoPriority(String priority){
        return tr.findByPriority(priority);
    }

    public List<ToDo> findByToDoStatus(String status) {

        return tr.findByToDoStatus(status);
    }

    public List<ToDo> findByToDoTag(String tag) {

        return tr.findByTag(tag);
    }
}

