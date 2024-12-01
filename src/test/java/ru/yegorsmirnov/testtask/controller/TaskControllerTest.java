package ru.yegorsmirnov.testtask.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
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
import ru.yegorsmirnov.testtask.model.dto.TaskCreateDTO;
import ru.yegorsmirnov.testtask.model.dto.TaskUpdateStatusDTO;
import ru.yegorsmirnov.testtask.model.enums.TaskPriority;
import ru.yegorsmirnov.testtask.model.enums.TaskStatus;
import ru.yegorsmirnov.testtask.repository.UserRepository;
import ru.yegorsmirnov.testtask.service.AuthorizationService;
import ru.yegorsmirnov.testtask.service.TaskService;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest extends AbstractIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private TaskController taskController;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthorizationService authorizationService;

    @Autowired
    private TaskService taskService;

    @Mock
    private UserRepository userRepository;

    @Test
    void shouldCreateTask() throws Exception {
        TaskCreateDTO task = new TaskCreateDTO();
        task.setTitle("Test Task");
        task.setDescription("Description of Test Task");
        task.setAuthorId(1L);
        task.setExecutorId(2L);
        task.setPriority(TaskPriority.LOW.name());
        task.setStatus(TaskStatus.PENDING.name());

        mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Test Task")));
    }

    @Test
    void shouldReturnTaskById() throws Exception {
        TaskCreateDTO task = new TaskCreateDTO();
        task.setTitle("Test Task");
        task.setDescription("Description of Test Task");
        task.setAuthorId(1L);
        task.setExecutorId(2L);
        task.setPriority(TaskPriority.LOW.name());
        task.setStatus(TaskStatus.PENDING.name());

        String response = mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        Long id = ((Number) responseMap.get("id")).longValue();

        mockMvc.perform(get("/task/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Test Task")));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"ADMIN"}, password = "12344321")
    void shouldDeleteTaskById() throws Exception {
        when(authorizationService.canDeleteTask(any(), anyLong())).thenReturn(true);

        TaskCreateDTO task = new TaskCreateDTO();
        task.setTitle("Test Task");
        task.setDescription("Description of Test Task");
        task.setAuthorId(1L);
        task.setExecutorId(2L);
        task.setPriority(TaskPriority.LOW.name());
        task.setStatus(TaskStatus.PENDING.name());

        String response = mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        Long taskId = ((Number) responseMap.get("id")).longValue();

        mockMvc.perform(delete("/task/" + taskId))
                .andExpect(status().isOk())
                .andExpect(content().string(taskId + " " + task.getTitle() + " DELETED"));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"ADMIN"}, password = "12344321")
    void shouldUpdateTaskStatus() throws Exception {
        TaskCreateDTO task = new TaskCreateDTO();
        task.setTitle("Test Task");
        task.setDescription("Description of Test Task");
        task.setAuthorId(1L);
        task.setExecutorId(2L);
        task.setPriority(TaskPriority.LOW.name());
        task.setStatus(TaskStatus.PENDING.name());

        when(authorizationService.canModifyTaskStatus(any(), anyLong())).thenReturn(true);

        String response = mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TaskUpdateStatusDTO updateStatusDTO = new TaskUpdateStatusDTO(TaskStatus.IN_PROGRESS.name());

        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        Long taskId = ((Number) responseMap.get("id")).longValue();

        mockMvc.perform(put("/task/" + taskId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStatusDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")));
    }
}
