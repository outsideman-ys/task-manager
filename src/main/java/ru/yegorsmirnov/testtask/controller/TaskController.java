package ru.yegorsmirnov.testtask.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yegorsmirnov.testtask.model.dto.TaskCreateDTO;
import ru.yegorsmirnov.testtask.model.dto.TaskResponseDTO;
import ru.yegorsmirnov.testtask.model.dto.TaskUpdateStatusDTO;
import ru.yegorsmirnov.testtask.service.TaskService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public TaskResponseDTO createTask(@RequestBody @Valid TaskCreateDTO task) {
        return taskService.createTask(task);
    }

    @GetMapping
    public List<TaskResponseDTO> getTasks() {
        return taskService.getTasks();
    }

    @GetMapping("/{id}")
    public TaskResponseDTO getTaskById(@PathVariable @Min(1) Long id) {
        return taskService.getTask(id);
    }

    @PreAuthorize("@authorizationService.canDeleteTask(principal, #id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTaskById(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(id + " " + taskService.deleteTask(id) + " DELETED");
    }

    @PreAuthorize("@authorizationService.canModifyTask(principal, #id)")
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable @Min(1) Long id, @RequestBody @Valid TaskCreateDTO updatedTask) {
        return ResponseEntity.ok(taskService.updateTask(id, updatedTask));
    }

    @PreAuthorize("@authorizationService.canModifyTaskStatus(principal, #id)")
    @PutMapping("/{id}/status")
    public ResponseEntity<TaskResponseDTO> updateTaskStatus(@PathVariable @Min(1) Long id, @RequestBody @Valid TaskUpdateStatusDTO updatedTask) {
        return ResponseEntity.ok(taskService.updateTaskStatus(id, updatedTask));
    }

}
