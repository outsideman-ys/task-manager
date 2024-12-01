package ru.yegorsmirnov.testtask.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.yegorsmirnov.testtask.model.entity.User;
import ru.yegorsmirnov.testtask.model.enums.Role;
import ru.yegorsmirnov.testtask.repository.UserRepository;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    public User save(User user) {
        return repository.save(user);
    }


    public User create(User user) {
        if (repository.existsByUsername(user.getUsername())) {
            // Заменить на свои исключения
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }

        if (repository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        return save(user);
    }

    public User getByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

    }

    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }

    @Transactional
    public void assignAdminRole(Long userId) {
        if (getCurrentUser().getRole() == Role.ROLE_ADMIN) {
            User user = repository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id " + userId));

            if (user.getRole() == Role.ROLE_ADMIN) {
                throw new IllegalStateException("User already has admin role");
            }

            user.setRole(Role.ROLE_ADMIN);
            repository.save(user);
        }
    }


}
