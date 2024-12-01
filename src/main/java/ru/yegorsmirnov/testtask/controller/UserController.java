package ru.yegorsmirnov.testtask.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yegorsmirnov.testtask.repository.UserRepository;
import ru.yegorsmirnov.testtask.service.UserService;

@Validated
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/{id}/assign-admin")
    public ResponseEntity<String> assignAdminRole(@PathVariable @Min(1) Long id) {
        userService.assignAdminRole(id);
        return ResponseEntity.ok("User with ID " + id + " is now an ADMIN");
    }

}
