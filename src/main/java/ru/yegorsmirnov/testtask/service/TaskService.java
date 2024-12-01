package ru.yegorsmirnov.testtask.service;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.yegorsmirnov.testtask.model.dto.TaskCreateDTO;
import ru.yegorsmirnov.testtask.model.dto.TaskResponseDTO;
import ru.yegorsmirnov.testtask.model.dto.TaskUpdateStatusDTO;
import ru.yegorsmirnov.testtask.model.entity.Task;
import ru.yegorsmirnov.testtask.model.entity.User;
import ru.yegorsmirnov.testtask.model.enums.TaskPriority;
import ru.yegorsmirnov.testtask.model.enums.TaskStatus;
import ru.yegorsmirnov.testtask.repository.TaskRepository;
import ru.yegorsmirnov.testtask.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.typeMap(TaskCreateDTO.class, Task.class).addMappings(mapper -> {
            mapper.skip(Task::setId);
            mapper.using(ctx -> {
                Long authorId = (Long) ctx.getSource();
                return userRepository.findById(authorId).orElse(null);
            }).map(TaskCreateDTO::getAuthorId, Task::setAuthor);

            mapper.using(ctx -> {
                Long executorId = (Long) ctx.getSource();
                return userRepository.findById(executorId).orElse(null);
            }).map(TaskCreateDTO::getExecutorId, Task::setExecutor);
        });
    }

    public TaskResponseDTO createTask(TaskCreateDTO task) {
        Task newTask = modelMapper.map(task, Task.class);
        Task savedTask = taskRepository.save(newTask);
        clearListCache();
        return modelMapper.map(savedTask, TaskResponseDTO.class);
    }

    @Cacheable(value = "task", key="#id")
    public TaskResponseDTO getTask(Long id) {
        Task foundTask = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id " + id + " when trying to access" +
                        " GET api/v1/task/" + id));

        return modelMapper.map(foundTask, TaskResponseDTO.class);
    }

    @Cacheable(value = "task", key="'all'")
    public List<TaskResponseDTO> getTasks() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(task->modelMapper.map(task, TaskResponseDTO.class))
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "task", key="#id")
    public String deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id " + id + " when trying to access" +
                        " DELETE api/v1/task/" + id));
        taskRepository.delete(task);
        clearListCache();
        return task.getTitle();
    }

    @CacheEvict(value = "task", key="#id")
    public TaskResponseDTO updateTask(Long id, TaskCreateDTO updatedTask) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id " + id + " when trying to access" +
                        " PUT api/v1/news/" + id));

        if (updatedTask.getAuthorId() != null) {
            User author = userRepository.findById(updatedTask.getAuthorId())
                    .orElseThrow(() -> new RuntimeException("Author not found with id " + updatedTask.getAuthorId()));
            task.setAuthor(author);
        }

        if (updatedTask.getExecutorId() != null) {
            User executor = userRepository.findById(updatedTask.getExecutorId())
                    .orElseThrow(() -> new RuntimeException("Executor not found with id " + updatedTask.getExecutorId()));
            task.setExecutor(executor);
        }
        if (updatedTask.getTitle() != null) {
            task.setTitle(updatedTask.getTitle());
        }
        if (updatedTask.getDescription() != null) {
            task.setDescription(updatedTask.getDescription());
        }
        if (updatedTask.getStatus() != null) {
            task.setStatus(TaskStatus.valueOf(updatedTask.getStatus()));
        }
        if (updatedTask.getPriority() != null) {
            task.setPriority(TaskPriority.valueOf(updatedTask.getPriority()));
        }
        task.setUpdatedAt(LocalDateTime.now());
        clearListCache();
        Task newTask = taskRepository.save(task);
        return modelMapper.map(newTask, TaskResponseDTO.class);
    }

    @CacheEvict(value = "task", key="#id")
    public TaskResponseDTO updateTaskStatus(Long id, TaskUpdateStatusDTO updatedTask) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id " + id + " when trying to access" +
                        " PUT api/v1/news/" + id));

        if (updatedTask.getStatus() != null) {
            task.setStatus(TaskStatus.valueOf(updatedTask.getStatus()));
        }
        task.setUpdatedAt(LocalDateTime.now());
        clearListCache();
        Task newTask = taskRepository.save(task);
        return modelMapper.map(newTask, TaskResponseDTO.class);
    }

    @CacheEvict(value = "task", key="'all'")
    public void clearListCache() {
    }

}
