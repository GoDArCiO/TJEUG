package com.project18.demo.repository;

import com.project18.demo.dbtestcleaner.DatabaseCleaner;
import com.project18.demo.model.Authors;
import com.project18.demo.model.BlogModel;
import com.project18.demo.model.Comment;
import org.junit.After;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
class BlogRepositoryTest {

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    AuthorsRepository authorsRepository;

    @Autowired
    DatabaseCleaner databaseCleaner;

    @After
    public void tearDown() {
        databaseCleaner.clearDatabase();
    }

    @Test
    public void shouldSaveAndFindById() {
        BlogModel blogModel = BlogModel.builder()
                .noRegister(true)
                .post_content("postContent")
                .post_tag("PostTAG")
                .build();

        BlogModel saved = blogRepository.save(blogModel);
        Optional<BlogModel> byId = blogRepository.findById(blogModel.getId());

        assertEquals(saved.getId(), byId.get().getId());
    }

    @Test
    public void shouldSaveWithAuthors() {
        BlogModel blogModel = BlogModel.builder()
                .noRegister(true)
                .post_content("postContent")
                .post_tag("PostTAG")
                .build();
        BlogModel saved = blogRepository.save(blogModel);
        Authors firstAuthor = Authors.builder()
                .email("emaail@mail.pl")
                .enabled(true)
                .fullName("aaaa")
                .password("aaaaa")
                .posts(List.of(saved))
                .build();
        authorsRepository.save(firstAuthor);
        Authors secondAuthors = Authors.builder()
                .email("emaial@aa.pl")
                .enabled(true)
                .fullName("aaaa1")
                .password("aaaaaa1")
                .posts(List.of(saved))
                .build();
        authorsRepository.save(secondAuthors);

        Optional<BlogModel> byId = blogRepository.findById(blogModel.getId());
        assertEquals(saved.getId(), byId.get().getId());
        assertEquals(authorsRepository.findAll().get(0).getPosts().size(),1);
        assertEquals(authorsRepository.findAll().get(1).getPosts().size(),1);
    }

    @Test
    public void shouldSaveWithComments() {
        BlogModel blogModel = BlogModel.builder()
                .noRegister(true)
                .post_content("postContent")
                .post_tag("PostTAG")
                .build();

        BlogModel saved = blogRepository.save(blogModel);
        for (int i = 0; i < 5; i++) {
            commentRepository.save(Comment.builder()
                    .comment("comment")
                    .username("username")
                    .blogModel(saved)
                    .build());
        }

        Optional<BlogModel> byId = blogRepository.findById(blogModel.getId());
        assertEquals(saved.getId(), byId.get().getId());
        assertEquals(byId.get().getComments().size(), 5);
    }
}
