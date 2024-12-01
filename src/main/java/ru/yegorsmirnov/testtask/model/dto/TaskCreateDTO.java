package ru.yegorsmirnov.testtask.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreateDTO {

    @NotBlank(message = "Название задачи не может быть пустым")
    private String title;

    private String description;

    @NotBlank(message = "Статус задачи не может быть пустым")
    private String status;

    @NotBlank(message = "Приоритет задачи не может быть пустым")
    private String priority;

    @NotNull(message = "Id автора не может быть пустым")
    @Min(1)
    private Long authorId;

    @NotNull(message = "Id исполнителя не может быть пустым")
    @Min(1)
    private Long executorId;

}
