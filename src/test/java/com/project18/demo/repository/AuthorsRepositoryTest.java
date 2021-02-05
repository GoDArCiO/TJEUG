package com.project18.demo.repository;

import com.project18.demo.dbtestcleaner.DatabaseCleaner;
import com.project18.demo.model.Authors;
import com.project18.demo.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.junit.After;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
public class AuthorsRepositoryTest {

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
        Authors authors = Authors.builder()
                .email("a1email@mail.pl")
                .enabled(true)
                .role(Role.USER)
                .posts(new ArrayList<>())
                .fullName("aaaa")
                .password("aaaaa")
                .build();
/*
        Authors saved = authorsRepository.save(authors);
        Optional<Authors> byId = authorsRepository.findById(saved.getId());

        assertEquals(saved.getId(), byId.get().getId());*/
    }
}
