package com.clush.clushbackend.repository;

import com.clush.clushbackend.domain.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ToDoRepository  extends JpaRepository<ToDo,Integer> {

    List<ToDo> findByPriority(String priority);

    List<ToDo> findByToDoStatus(String status);

    List<ToDo> findByTag(String tag);
}
