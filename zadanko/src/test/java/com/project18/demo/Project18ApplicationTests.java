package com.project18.demo;

import com.project18.demo.dbtestcleaner.DatabaseCleaner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class Project18ApplicationTests {

    @Autowired
    DatabaseCleaner databaseCleaner;

    @Test
    void shouldLoadDatabaseCleanerAndCleanDB() {
        databaseCleaner.clearDatabase();
    }

}
