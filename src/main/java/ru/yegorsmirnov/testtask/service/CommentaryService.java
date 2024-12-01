package ru.yegorsmirnov.testtask.service;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.yegorsmirnov.testtask.model.dto.CommentCreateDTO;
import ru.yegorsmirnov.testtask.model.dto.CommentResponseDTO;
import ru.yegorsmirnov.testtask.model.dto.TaskCreateDTO;
import ru.yegorsmirnov.testtask.model.entity.Commentary;
import ru.yegorsmirnov.testtask.model.entity.Task;
import ru.yegorsmirnov.testtask.model.entity.User;
import ru.yegorsmirnov.testtask.repository.CommentaryRepository;
import ru.yegorsmirnov.testtask.repository.TaskRepository;
import ru.yegorsmirnov.testtask.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentaryService {

    private final CommentaryRepository commentaryRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public CommentaryService(CommentaryRepository commentaryRepository, UserRepository userRepository,
                             TaskRepository taskRepository, UserService userService) {
        this.commentaryRepository = commentaryRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.typeMap(CommentCreateDTO.class, Commentary.class).addMappings(mapper -> {
            mapper.skip(Commentary::setId);
            mapper.using(ctx -> {
                Long authorId = (Long) ctx.getSource();
                return userRepository.findById(authorId).orElse(null);
            }).map(CommentCreateDTO::getAuthorId, Commentary::setAuthor);
        });
    }

    public CommentResponseDTO createCommentary(CommentCreateDTO commentary) {
        Commentary newCommentary = modelMapper.map(commentary, Commentary.class);
        System.out.println(commentary.getAuthorId());
        System.out.println(newCommentary.getAuthor().getId());
        Commentary savedCommentary = commentaryRepository.save(newCommentary);
        clearListCache();
        return modelMapper.map(savedCommentary, CommentResponseDTO.class);
    }

    @Cacheable(value = "comment", key="#id")
    public CommentResponseDTO getCommentary(Long id) {
        Commentary foundCommentary = commentaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commentary not found with id " + id + " when trying to access" +
                        " GET api/v1/commentary/" + id));
        return modelMapper.map(foundCommentary, CommentResponseDTO.class);
    }

    @Cacheable(value = "comment", key="'all'")
    public List<CommentResponseDTO> getCommentaries() {
        List<Commentary> commentaries = commentaryRepository.findAll();
        return commentaries.stream()
                .map(commentary->modelMapper.map(commentary, CommentResponseDTO.class))
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "comment", key="#id")
    public String deleteCommentary(Long id) {
        Commentary commentary = commentaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commentary not found with id " + id + " when trying to access" +
                        " DELETE api/v1/task/" + id));
        commentaryRepository.delete(commentary);
        clearListCache();
        return commentary.getContent();
    }

    @CachePut(value = "comment", key = "#id")
    public CommentResponseDTO updateCommentary(Long id, CommentCreateDTO updatedCommentary) {
        Commentary commentary = commentaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commentary not found with id " + id + " when trying to access" +
                        " PUT api/v1/commentary/" + id));

        if (updatedCommentary.getAuthorId() != null) {
            User author = userRepository.findById(updatedCommentary.getAuthorId())
                    .orElseThrow(() -> new RuntimeException("Author not found with id " + updatedCommentary.getAuthorId()));
            commentary.setAuthor(author);
        }

        if (updatedCommentary.getTaskId() != null) {
            Task task = taskRepository.findById(updatedCommentary.getTaskId())
                    .orElseThrow(() -> new RuntimeException("Task not found with id " + updatedCommentary.getTaskId()));
            commentary.setTask(task);
        }

        if (updatedCommentary.getContent() != null) {
            commentary.setContent(updatedCommentary.getContent());
        }

        Commentary newCommentary = commentaryRepository.save(commentary);
        clearListCache();
        return modelMapper.map(newCommentary, CommentResponseDTO.class);
    }

    @CacheEvict(value = "comment", key="'all'")
    public void clearListCache() {
    }

}
