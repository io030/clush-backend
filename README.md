# 프로젝트 소개
할 일 목록 작성 서비스로 자신이 해야하는 일들을 내용, 우선순위 , 작업상태 , 태그, 메모와 함께 작성하여 목록화할 수 있다


우선순위는 할 일 목록들 중 무엇을 먼저 해야하는지 쉽게 분류할 수 있게 해주며 high, normal, low 총 3가지만 설정이 가능하며 각각 높음 중간 낮음으로 구별된다

작업상태는 할 일 목록들 중 무엇이 완료가 되었는지 혹은 진행 중인지 쉽게 분류할 수 있게 해주며 in_progress, done, yet 총 3가지만 설정이 가능하며 각각 현재 진행중, 작업 완료, 아직 시작안함으로 구별된다

태그는 할 일 목록들을 각각의 유형을 달아서 어떤 종류인지 쉽게 분류할 수 있게 해주며 work, hobby, study, etc 총 4가지만 설정이 가능하며 각각 직장, 취미 및 여가, 공부 , 기타 등등으로 구별된다

메모는 할 일에 대하여 추가적인 내용을 작성하여 특이사항과 세부 내용을 쉽게 기억할 수 있게 해준다


우선순위, 작업상태, 태그 각각의 기능들은 추가적으로 검색 기능을 구현하여 원하는대로 할 일 목록을 조회할 수 있다

### 주요기능
1. 할 일 생성
2. 할 일 삭제
3. 할 일 목록 조회
4. 할 일 조회
5. 할 일 수정
6. 할 일 우선순위 검색
7. 할 일 작업상태 검색
8. 할 일 태그 검색

### 프로젝트 구조

!! server.port = 8090 !!

사용한 의존성

    dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation group: 'org.springdoc', name: 'springdoc-openapi-starter-webmvc-ui', version: '2.6.0'
    compileOnly 'org.projectlombok:lombok'
    runtimeOnly 'com.mysql:mysql-connector-j'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
    }



Controller - Service - Repository 구조, 각 계층별 데이터 교환은 DTO 객체를 사용

SpringDataJpa를 사용하여 JpaRepository 인터페이스를 상속받아 구현

ToDo Entity 생성


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

ToDoControllerTest 테스트 클래스 생성 후 단위 테스트 코드를 작성하여 각 기능의 성능을 검증함
<details>
    <summary>단위 테스트 코드</summary>

<!-- summary 아래 한칸 공백 두고 내용 삽입 -->

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
</details>

---
### 주요 컴포넌트 설명

ToDoController.java : 각 요청에 대한 처리와 그에 따른 비즈니스 로직을 맡기고 그에 대한 결과를 돌려주는 역할을 합니다. ToDoService를 주입받아 비즈니스 로직을 맡기고 이 후 결과값들을 ResponseEntity를 통해 HttpBody에 결과값을 반환한다

ToDoService.java : 메인 기능이 되는 주요 비즈니스 로직을 처리하는 계층으로 ToDoRepository를 주입받아 데이터를 원하는 결과값에 맞게 처리한다.

ToDoRepository.java : JpaRepository를 상속받아서 JPA의 여러 메서드들을 간단하게 사용할 수 있다. EntityManager를 직접 관리할 필요가 없어 편리하며 메소드 이름으로 쿼리를 생성할 수도 있다.

DTO : Entity 객체인 ToDo를 그대로 사용하면 데이터가 노출되어 변경될 수 있기에 각 계층별 데이터 교환을 위한 객체를 만들어서 사용했다. 또한 각 요청에 의한 응답에 어떤 데이터들을 보여줄 지 처리하기 용이합니다


---

### 실행방법

### DB 

mysql 실행 후 root 계정으로 로그인 이 후 아래 쿼리문을 통해 사용자 생성 및 권한 부여, DB 생성 

    create user 'clush_test'@'localhost' identified by '040312';

    CREATE DATABASE clush;

    grant all privileges on clush.* to 'clush_test'@'localhost';

    FLUSH PRIVILEGES;

스프링 부트 내부의 data.sql을 통해 프로젝트 실행 시 데이터가 자동으로 추가됨 , 실행 시 매번 초기화되는 구조로 테스트 환경을 동일하게 유지시킴
    
### Spring

1. Github를 통해 프로젝트 복제 후 Intellij를 통해 실행
2. 우측 Gradle 탭을 클릭
3. Tasks - build 이동 후 bootjar 스크립트 더블 클릭하여 실행
4. 터미널을 통해 ~\ClushBackend\build\libs 경로로 이동
5. 터미널을 통해 아래 코드를 입력하여 프로젝트 실행
   
       java -jar clushbackend-0.0.1-SNAPSHOT.jar

6. 웹 주소창에 <http://localhost:8090/swagger-ui/index.html> 입력하여 이동

---
### API 명세서

Swagger를 사용하여 제작

API들의 이름과 설명을 추가적으로 작성하여 쉽게 알아볼 수 있으며 간단하게 테스트를 할 수 있다.

주소 : <http://localhost:8090/swagger-ui/index.html> 





   
