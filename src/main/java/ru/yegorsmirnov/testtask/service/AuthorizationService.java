package ru.yegorsmirnov.testtask.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.yegorsmirnov.testtask.model.entity.Commentary;
import ru.yegorsmirnov.testtask.model.entity.Task;
import ru.yegorsmirnov.testtask.model.entity.User;
import ru.yegorsmirnov.testtask.repository.CommentaryRepository;
import ru.yegorsmirnov.testtask.repository.TaskRepository;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final TaskRepository taskRepository;
    private final CommentaryRepository commentaryRepository;

    public boolean canModifyTask(UserDetails principal, Long taskId) {
        if (isAdmin(principal)) return true;
        User user = (User) principal;
        Task task = taskRepository.findById(taskId).orElse(null);
        return task != null && (user.getId().equals(task.getAuthor().getId()));
    }

    public boolean canModifyTaskStatus(UserDetails principal, Long taskId) {
        if (isAdmin(principal)) return true;
        User user = (User) principal;
        Task task = taskRepository.findById(taskId).orElse(null);
        return task != null && (user.getId().equals(task.getExecutor().getId()) || user.getId().equals(task.getAuthor().getId()));
    }

    public boolean canDeleteTask(UserDetails principal, Long taskId) {
        if (isAdmin(principal)) return true;
        User user = (User) principal;
        return canModifyTask(user, taskId);
    }

    public boolean canModifyCommentary(User user, Long commentaryId) {
        if (isAdmin(user)) return true;
        Commentary commentary = commentaryRepository.findById(commentaryId).orElse(null);
        return commentary != null && (user.getId().equals(commentary.getAuthor().getId()));
    }

    public boolean canDeleteCommentary(User user, Long commentaryId) {
        if (isAdmin(user)) return true;
        return canModifyCommentary(user, commentaryId);
    }

    public boolean isAdmin(UserDetails user) {
        return user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

}
