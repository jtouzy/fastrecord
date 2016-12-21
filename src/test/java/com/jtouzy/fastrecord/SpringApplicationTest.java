package com.jtouzy.fastrecord;

import com.jtouzy.fastrecord.builders.Query;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.List;

@Configuration
public class SpringApplicationTest {
    @Test
    public void applicationTest() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.scan("com.jtouzy.fastrecord");
        context.refresh();

        List<Event> events = Query.from(Event.class).findAll();
        events.forEach(System.out::println);
    }

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl(
        );
        config.setUsername(
        );
        config.setPassword(
        );
        return new HikariDataSource(config);
    }
}
