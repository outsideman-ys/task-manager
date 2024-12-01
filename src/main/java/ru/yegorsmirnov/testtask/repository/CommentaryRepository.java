package ru.yegorsmirnov.testtask.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yegorsmirnov.testtask.model.entity.Commentary;

public interface CommentaryRepository extends JpaRepository<Commentary, Long> {
}
