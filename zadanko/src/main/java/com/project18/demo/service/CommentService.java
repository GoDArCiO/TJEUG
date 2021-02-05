package com.project18.demo.service;

import com.project18.demo.exception.YouCantRemoveCommentException;
import com.project18.demo.model.Authors;
import com.project18.demo.model.Comment;
import com.project18.demo.model.Role;
import com.project18.demo.model.dto.CommentResponse;
import com.project18.demo.model.dto.NewComment;
import com.project18.demo.repository.AuthorsRepository;
import com.project18.demo.repository.BlogRepository;
import com.project18.demo.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final BlogRepository blogRepository;
    private final AuthorsRepository authorsRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, BlogRepository blogRepository, AuthorsRepository authorsRepository) {
        this.commentRepository = commentRepository;
        this.blogRepository = blogRepository;
        this.authorsRepository = authorsRepository;
    }

    public void save(NewComment comment, Long id) {
        Comment comment1 = Comment.builder()
                .comment(comment.getComment())
                .username(comment.getUsername())
                .blogModel(blogRepository.findById(id).get())
                .build();
        Comment saved = commentRepository.save(comment1);
        log.debug("Comment saved successfully {}", saved);
    }

    public void remove(String userEmail, long id) {
        Authors authors = authorsRepository.findByEmail(userEmail).get();
        Comment comment = commentRepository.findById(id).get();
        if (authors.getRole().equals(Role.ADMIN) || authors.getFullName().equals(comment.getUsername())) {
            commentRepository.delete(comment);
        }else{
            throw new YouCantRemoveCommentException("Nie masz uprawnień do usunięcia tego komentarza !", "code");
        }
    }

    public void saveLogin(NewComment comment, Long id, String userEmail) {
        Comment comment1 = Comment.builder()
                .comment(comment.getComment())
                .username( authorsRepository.findByEmail(userEmail).get().getFullName())
                .blogModel(blogRepository.findById(id).get())
                .build();
        Comment saved = commentRepository.save(comment1);
        log.debug("Comment saved successfully {}", saved);
    }

    public List<CommentResponse> findByEmail(String userEmail) {
        String fullName = authorsRepository.findByEmail(userEmail).get().getFullName();
        return commentRepository.findByUsername(fullName).stream().map(it ->
                new CommentResponse(it.getId(), it.username,it.getComment(), it.getBlogModel().getId())).collect(Collectors.toList());
    }
}
