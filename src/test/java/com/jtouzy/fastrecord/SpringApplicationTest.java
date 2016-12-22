package com.jtouzy.fastrecord;

import com.jtouzy.fastrecord.builders.Query;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.sql.DataSource;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@TestPropertySource("/application.properties")
public class SpringApplicationTest {
    @Configuration
    @ComponentScan("com.jtouzy.fastrecord")
    public static class SpringTestConfiguration {
        @Bean
        public DataSource dataSource() {
            HikariConfig config = new HikariConfig();
            config.setDriverClassName("");
            config.setJdbcUrl("");
            config.setUsername("");
            config.setPassword("");
            return new HikariDataSource(config);
        }
    }

    @Test
    public void applicationTest() {
        List<Event> events = Query.from(Event.class).findAll();
        events.forEach(System.out::println);
    }
}
