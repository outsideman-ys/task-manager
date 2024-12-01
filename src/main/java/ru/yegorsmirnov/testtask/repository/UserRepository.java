package ru.yegorsmirnov.testtask.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yegorsmirnov.testtask.model.entity.User;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isEnabled = :isEnabled WHERE u.id = :id")
    void updateEnabledById(@Param("id") Long id, @Param("isEnabled") Boolean isEnabled);

    List<User> findAllByIsEnabledTrue();

    Optional<User> findByIdAndIsEnabledTrue(Long id);

    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}