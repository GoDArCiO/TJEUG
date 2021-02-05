package com.project18.demo.repository;

import com.project18.demo.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    void deleteByBlogModelId(Long id);

    List<Comment> findAllByBlogModelId(Long id);

    List<Comment> findByUsername(String username);
}
