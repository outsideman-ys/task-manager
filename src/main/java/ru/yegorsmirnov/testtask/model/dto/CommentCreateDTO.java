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
public class CommentCreateDTO {

    @NotNull(message = "Id задачи не может быть пустым")
    @Min(1)
    private Long taskId;

    @NotNull(message = "Id автора не может быть пустым")
    @Min(1)
    private Long authorId;

    @NotBlank(message = "Комментарий не может быть пустым")
    private String content;


}
