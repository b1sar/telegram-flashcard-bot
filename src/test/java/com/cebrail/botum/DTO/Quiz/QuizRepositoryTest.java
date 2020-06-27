package com.cebrail.botum.DTO.Quiz;

import com.cebrail.botum.Model.Quiz;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
class QuizRepositoryTest {
    @Autowired
    QuizRepository quizRepository;

    @Test
    void get() {
        Quiz q = quizRepository.get(59);
        System.err.println(q.toString());
        Assertions.assertThat(q).isNotNull();
        Assertions.assertThat(q.getTest().size()).isGreaterThan(2);
    }

    @Test
    void getAllByUserId() {
    }

    @Test
    void size() {
    }
}