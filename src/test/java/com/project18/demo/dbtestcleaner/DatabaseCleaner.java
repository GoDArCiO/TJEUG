package com.project18.demo.dbtestcleaner;

import lombok.extern.slf4j.Slf4j;
import org.h2.jdbcx.JdbcDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class DatabaseCleaner {

    private static final String SELECT_TABLE_NAME_QUERY = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES  where TABLE_SCHEMA='PUBLIC'";
    private static final String SELECT_SEQUENCE_NAME_QUERY = "SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_SCHEMA='PUBLIC'";
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;


    public void clearDatabase() {
        try {
            clearDataBase();
        } catch (Exception e) {
            log.error("Error during database cleaning", e);
        }
    }

    // https://stackoverflow.com/questions/8523423/reset-embedded-h2-database-periodically
    private void clearDataBase() throws SQLException {
        log.info("Method clearDataBase called");
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL(url);
        ds.setUser(username);
        ds.setPassword(password);
        Connection conn = ds.getConnection();
        Statement s = conn.createStatement();
        // Disable FK
        s.execute("SET REFERENTIAL_INTEGRITY FALSE");
        // Find all tables and truncate them
        Set<String> tables = new HashSet<String>();
        ResultSet rs = s.executeQuery(SELECT_TABLE_NAME_QUERY);
        while (rs.next()) {
            tables.add(rs.getString(1));
        }
        rs.close();
        for (String table : tables) {
            s.executeUpdate("TRUNCATE TABLE " + table);
        }
        // Idem for sequences
        Set<String> sequences = new HashSet<String>();
        rs = s.executeQuery(SELECT_SEQUENCE_NAME_QUERY);
        while (rs.next()) {
            sequences.add(rs.getString(1));
        }
        rs.close();
        for (String seq : sequences) {
            s.executeUpdate("ALTER SEQUENCE " + seq + " RESTART WITH 1");
        }
        // Enable FK
        s.execute("SET REFERENTIAL_INTEGRITY TRUE");
        s.close();
        conn.close();
        log.debug("Successful database cleanup");
    }
}
