package ru.yegorsmirnov.testtask.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yegorsmirnov.testtask.model.dto.CommentCreateDTO;
import ru.yegorsmirnov.testtask.model.dto.CommentResponseDTO;
import ru.yegorsmirnov.testtask.service.CommentaryService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/commentary")
@RequiredArgsConstructor
public class CommentaryController {

    private final CommentaryService commentaryService;

    @PostMapping
    public CommentResponseDTO createCommentary(@RequestBody @Valid CommentCreateDTO commentary) {
        return commentaryService.createCommentary(commentary);
    }

    @GetMapping
    public List<CommentResponseDTO> getCommentaries() {
        return commentaryService.getCommentaries();
    }

    @GetMapping("/{id}")
    public CommentResponseDTO getCommentaryById(@PathVariable @Min(1) Long id) {
        return commentaryService.getCommentary(id);
    }

    @PreAuthorize("@authorizationService.canDeleteCommentary(principal, #id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCommentaryById(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(id + " " + commentaryService.deleteCommentary(id) + " DELETED");
    }

    @PreAuthorize("@authorizationService.canModifyCommentary(principal, #id)")
    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseDTO> updateCommentary(@PathVariable @Min(1) Long id, @RequestBody @Valid CommentCreateDTO updatedCommentary) {
        return ResponseEntity.ok(commentaryService.updateCommentary(id, updatedCommentary));
    }

}
