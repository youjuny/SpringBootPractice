package com.korea.basic2;

import com.korea.basic2.answer.Answer;
import com.korea.basic2.answer.AnswerRepository;
import com.korea.basic2.question.Question;
import com.korea.basic2.question.QuestionRepository;
import com.korea.basic2.question.QuestionService;
import jdk.jfr.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class Basic2ApplicationTests {

    @Autowired
    private QuestionService questionService;

    @Test
    void testJpa() {
        for (int i = 0; i <= 300; i++) {
            String subject = String.format("테스트 데이터입니다:[$03d]",i);
            String content = "내용무";
            this.questionService.create(subject,content, null, "테스트데이터");
        }
    }

}
