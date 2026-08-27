package com.yukihira.bookstore;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("migration-test")
class FlywayMigrationTests {

    @Autowired Flyway flyway;
    @Autowired JdbcTemplate jdbcTemplate;

    @Test
    void flywayCreatesAndHibernateValidatesCompleteSchema() {
        assertThat(flyway.info().current().getVersion().getVersion()).isEqualTo("1");
        Integer tableCount = jdbcTemplate.queryForObject("""
                select count(*) from information_schema.tables
                where table_schema = 'public'
                  and table_name in ('users', 'books', 'carts', 'orders', 'order_items')
                """, Integer.class);
        assertThat(tableCount).isEqualTo(5);
    }
}
