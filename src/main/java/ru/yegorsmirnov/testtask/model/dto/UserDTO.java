package ru.yegorsmirnov.testtask.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTO {

    private Long id;

    @NotBlank(message = "Почта пользователя не может быть пустой")
    @Email(message = "Email адрес должен быть в формате user@example.com")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Роль пользователя не может быть пустой")
    @JsonProperty("role")
    private String role;


}
