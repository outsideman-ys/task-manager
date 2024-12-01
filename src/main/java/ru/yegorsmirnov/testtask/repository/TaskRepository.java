package ru.yegorsmirnov.testtask.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yegorsmirnov.testtask.model.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
