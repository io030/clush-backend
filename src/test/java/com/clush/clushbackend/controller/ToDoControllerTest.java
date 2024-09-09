package com.clush.clushbackend.controller;

import com.clush.clushbackend.domain.ToDo;
import com.clush.clushbackend.dto.AddToDoRequest;
import com.clush.clushbackend.dto.UpdateToDoRequest;
import com.clush.clushbackend.repository.ToDoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ToDoControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    ToDoRepository tr;

    @BeforeEach
    public void mockMvcSetUp(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        tr.deleteAll();
    }

    @DisplayName("addToDo : 할 일 목록 추가에 성공")
    @Test
    public void addToDo() throws Exception {

        //given
        final String url = "/todo/new";

        final String toDoContent = "알바 출근하기";
        final String priority = "high";
        final String toDoStatus = "yet";
        final String tag = "work";
        final String memo = "10시 30분까지 출근";

        final AddToDoRequest userRequest = new AddToDoRequest(toDoContent,priority,toDoStatus,tag,memo);

        // 객체 json으로 직렬화
        final String requestBody = objectMapper.writeValueAsString(userRequest);

        //when
        //설정한 내용을 바탕으로 요청 전송
        ResultActions result = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody));

        //then
        result.andExpect(status().isCreated());

        List<ToDo> toDo = tr.findAll();

        assertThat(toDo.size()).isEqualTo(1);
        assertThat(toDo.get(0).getToDoContent()).isEqualTo(toDoContent);
        assertThat(toDo.get(0).getPriority()).isEqualTo(priority);
        assertThat(toDo.get(0).getToDoStatus()).isEqualTo(toDoStatus);
        assertThat(toDo.get(0).getTag()).isEqualTo(tag);
        assertThat(toDo.get(0).getMemo()).isEqualTo(memo);
    }

    @DisplayName("findAllToDo : 할 일 목록 조회에 성공")
    @Test
    public void findAllToDo() throws Exception {

        //given
        final String url = "/todo/list";
        final String toDoContent = "공부하기";
        final String priority = "normal";
        final String toDoStatus = "in_progress";
        final String tag = "study";
        final String memo = "스프링 부트 공부";

        tr.save(ToDo.builder().toDoContent(toDoContent).priority(priority).
                toDoStatus(toDoStatus).tag(tag).memo(memo).build());

        //when
        final ResultActions resultActions = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].toDoContent").value(toDoContent))
                .andExpect(jsonPath("$[0].priority").value(priority))
                .andExpect(jsonPath("$[0].toDoStatus").value(toDoStatus))
                .andExpect(jsonPath("$[0].tag").value(tag))
                .andExpect(jsonPath("$[0].memo").value(memo));

    }

    @DisplayName("findToDo : 특정 할 일 조회에 성공")
    @Test
    public void findToDo() throws Exception {

        //given
        final String url = "/todo/{toDoNum}";
        final String toDoContent = "유트브 보기";
        final String priority = "low";
        final String toDoStatus = "yet";
        final String tag = "hobby";
        final String memo = "숏츠 보기";

        ToDo savedToDo = tr.save(ToDo.builder().toDoContent(toDoContent).priority(priority).
                toDoStatus(toDoStatus).tag(tag).memo(memo).build());

        //when
        final ResultActions resultActions = mockMvc.perform(get(url,savedToDo.getToDoNum()));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.toDoContent").value(toDoContent))
                .andExpect(jsonPath("$.priority").value(priority))
                .andExpect(jsonPath("$.toDoStatus").value(toDoStatus))
                .andExpect(jsonPath("$.tag").value(tag))
                .andExpect(jsonPath("$.memo").value(memo));

    }

    @DisplayName("deleteToDo : 할 일 삭제에 성공")
    @Test
    public void deleteToDo() throws Exception {

        //given
        final String url = "/todo/{toDoNum}";
        final String toDoContent = "공부하기";
        final String priority = "normal";
        final String toDoStatus = "in_progress";
        final String tag = "study";
        final String memo = "스프링 부트 공부";

        ToDo savedToDo = tr.save(ToDo.builder().toDoContent(toDoContent).priority(priority).
                toDoStatus(toDoStatus).tag(tag).memo(memo).build());

        //when
        mockMvc.perform(delete(url,savedToDo.getToDoNum())).andExpect(status().isOk());

        //then
        List<ToDo> toDoList = tr.findAll();

        assertThat(toDoList ).isEmpty();

    }

    @DisplayName("updateToDo : 할 일 수정에 성공")
    @Test
    public void updateToDo() throws Exception {

        //given
        final String url = "/todo/{toDoNum}";
        final String toDoContent = "아침먹기";
        final String priority = "normal";
        final String toDoStatus = "done";
        final String tag = "etc";
        final String memo = "샌드위치";

        ToDo savedToDo = tr.save(ToDo.builder().toDoContent(toDoContent).priority(priority).
                toDoStatus(toDoStatus).tag(tag).memo(memo).build());

        final String newToDoContent = "야식먹기";
        final String newPriority = "low";
        final String newToDoStatus = "yet";
        final String newTag = "etc";
        final String newMemo = "남은 피자 데워먹기";

        UpdateToDoRequest request = new UpdateToDoRequest(newToDoContent,newPriority,newToDoStatus,newTag,newMemo);

        //when
        ResultActions result = mockMvc.perform(put(url,savedToDo.getToDoNum())
                .contentType(MediaType.APPLICATION_JSON_VALUE).
                content(objectMapper.writeValueAsString(request)));

        //then
        result.andExpect(status().isOk());

        ToDo toDo = tr.findById(savedToDo.getToDoNum()).get();

        assertThat(toDo.getToDoContent()).isEqualTo(newToDoContent);
        assertThat(toDo.getPriority()).isEqualTo(newPriority);
        assertThat(toDo.getToDoStatus()).isEqualTo(newToDoStatus);
        assertThat(toDo.getTag()).isEqualTo(newTag);
        assertThat(toDo.getMemo()).isEqualTo(newMemo);

    }

    @DisplayName("findToDoPriority : 할 일 우선순위로 조회 성공")
    @Test
    public void findToDoPriority() throws Exception {

        //given
        final String url = "/todo/priority/{priority}";
        final String toDoContent = "공부하기";
        final String priority = "normal";
        final String toDoStatus = "in_progress";
        final String tag = "study";
        final String memo = "스프링 부트 공부";

        ToDo savedToDo = tr.save(ToDo.builder().toDoContent(toDoContent).priority(priority).
                toDoStatus(toDoStatus).tag(tag).memo(memo).build());

        //when
        final ResultActions resultActions = mockMvc.perform(get(url,savedToDo.getPriority()));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].toDoContent").value(toDoContent))
                .andExpect(jsonPath("$[0].priority").value(priority))
                .andExpect(jsonPath("$[0].toDoStatus").value(toDoStatus))
                .andExpect(jsonPath("$[0].tag").value(tag))
                .andExpect(jsonPath("$[0].memo").value(memo));

    }

    @DisplayName("findToDoStatus : 할 일 작업상태로 조회 성공")
    @Test
    public void findToDoStatus() throws Exception {

        //given
        final String url = "/todo/status/{toDoStatus}";
        final String toDoContent = "공부하기";
        final String priority = "normal";
        final String toDoStatus = "in_progress";
        final String tag = "study";
        final String memo = "스프링 부트 공부";

        ToDo savedToDo = tr.save(ToDo.builder().toDoContent(toDoContent).priority(priority).
                toDoStatus(toDoStatus).tag(tag).memo(memo).build());

        //when
        final ResultActions resultActions = mockMvc.perform(get(url,savedToDo.getToDoStatus()));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].toDoContent").value(toDoContent))
                .andExpect(jsonPath("$[0].priority").value(priority))
                .andExpect(jsonPath("$[0].toDoStatus").value(toDoStatus))
                .andExpect(jsonPath("$[0].tag").value(tag))
                .andExpect(jsonPath("$[0].memo").value(memo));

    }

    @DisplayName("findToDoTag : 할 일 태그로 조회 성공")
    @Test
    public void findToDoTag() throws Exception {

        //given
        final String url = "/todo/tag/{tag}";
        final String toDoContent = "공부하기";
        final String priority = "normal";
        final String toDoStatus = "in_progress";
        final String tag = "study";
        final String memo = "스프링 부트 공부";

        ToDo savedToDo = tr.save(ToDo.builder().toDoContent(toDoContent).priority(priority).
                toDoStatus(toDoStatus).tag(tag).memo(memo).build());

        //when
        final ResultActions resultActions = mockMvc.perform(get(url,savedToDo.getTag()));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].toDoContent").value(toDoContent))
                .andExpect(jsonPath("$[0].priority").value(priority))
                .andExpect(jsonPath("$[0].toDoStatus").value(toDoStatus))
                .andExpect(jsonPath("$[0].tag").value(tag))
                .andExpect(jsonPath("$[0].memo").value(memo));

    }
}