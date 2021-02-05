package com.project18.demo.repository;

import com.project18.demo.dbtestcleaner.DatabaseCleaner;
import com.project18.demo.model.BlogModel;
import com.project18.demo.model.Comment;
import org.junit.After;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    DatabaseCleaner databaseCleaner;

    @After
    public void tearDown() {
        databaseCleaner.clearDatabase();
    }

    @Test
    public void shouldSaveAndFindById() {
        BlogModel blogModel = blogRepository.save( BlogModel.builder()
                .noRegister(true)
                .post_content("postContent")
                .post_tag("PostTAG")
                .build());
        Comment comment = Comment.builder()
                .comment("comment")
                .username("username")
                .blogModel(blogModel)
                .build();

        Comment saved = commentRepository.save(comment);
        Optional<Comment> byId = commentRepository.findById(comment.getId());

        assertEquals(saved.getId(), byId.get().getId());
        Assert.assertNotNull(byId.get().getBlogModel());
    }
}
