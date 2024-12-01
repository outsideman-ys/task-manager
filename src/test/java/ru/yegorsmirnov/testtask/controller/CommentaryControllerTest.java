package ru.yegorsmirnov.testtask.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.yegorsmirnov.testtask.AbstractIntegrationTests;
import ru.yegorsmirnov.testtask.model.dto.CommentCreateDTO;
import ru.yegorsmirnov.testtask.model.entity.Commentary;
import ru.yegorsmirnov.testtask.model.entity.Task;
import ru.yegorsmirnov.testtask.model.entity.User;
import ru.yegorsmirnov.testtask.model.enums.Role;
import ru.yegorsmirnov.testtask.model.enums.TaskPriority;
import ru.yegorsmirnov.testtask.model.enums.TaskStatus;
import ru.yegorsmirnov.testtask.repository.CommentaryRepository;
import ru.yegorsmirnov.testtask.repository.TaskRepository;
import ru.yegorsmirnov.testtask.repository.UserRepository;
import ru.yegorsmirnov.testtask.service.AuthorizationService;
import ru.yegorsmirnov.testtask.service.CommentaryService;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class CommentaryControllerTest extends AbstractIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private CommentaryController taskController;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthorizationService authorizationService;

    @Autowired
    private CommentaryService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentaryRepository commentaryRepository;

    @BeforeEach
    void setUp() {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Description of Test Task");
        task.setAuthor(new User(1L, "username", "1234abcd", "usr@gmail.com", Role.ROLE_ADMIN, true));
        task.setExecutor(new User(1L, "username", "1234abcd", "usr@gmail.com", Role.ROLE_ADMIN, true));
        task.setPriority(TaskPriority.LOW);
        task.setStatus(TaskStatus.PENDING);

        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));

        User user = new User(1L, "username", "1234abcd", "usr@gmail.com", Role.ROLE_ADMIN, true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Commentary commentary = new Commentary(1L, task, user, "Test Comment", LocalDateTime.now());

        when(commentaryRepository.findById(anyLong())).thenReturn(Optional.of(commentary));

    }

    @Test
    void shouldCreateCommentary() throws Exception {
        CommentCreateDTO comment = new CommentCreateDTO();
        comment.setContent("Test Comment");
        comment.setAuthorId(1L);
        comment.setTaskId(1L);

        mockMvc.perform(post("/commentary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is("Test Comment")));
    }

    @Test
    void shouldReturnCommentById() throws Exception {
        CommentCreateDTO comment = new CommentCreateDTO();
        comment.setContent("Test Comment");
        comment.setAuthorId(1L);
        comment.setTaskId(1L);

        String response = mockMvc.perform(post("/commentary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        Long id = ((Number) responseMap.get("id")).longValue();

        mockMvc.perform(get("/commentary/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is("Test Comment")));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"ADMIN"}, password = "12344321")
    void shouldDeleteCommentById() throws Exception {
        when(authorizationService.canDeleteCommentary(any(), anyLong())).thenReturn(true);

        CommentCreateDTO comment = new CommentCreateDTO();
        comment.setContent("Test Comment");
        comment.setAuthorId(1L);
        comment.setTaskId(1L);

        String response = mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        Long taskId = ((Number) responseMap.get("id")).longValue();

        mockMvc.perform(delete("/task/" + taskId))
                .andExpect(status().isOk())
                .andExpect(content().string(taskId + " " + comment.getContent() + " DELETED"));
    }

}
