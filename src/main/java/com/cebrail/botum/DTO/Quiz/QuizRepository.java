package com.cebrail.botum.DTO.Quiz;

import com.cebrail.botum.Model.Quiz;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository {
    public int save(Quiz quiz);//implemented
    public Quiz get(Integer quizId);//implemented
    public List<Quiz> getAllByUserId(String chatId);//implemented
    public void update(Quiz quiz);
    public void delete(Quiz quiz);
    public Integer deleteAllQuizzesByUserChatId(Long userChatId);

    Integer size();
    public void sonrakiSoruyaGec(Integer quizId, Integer hangiSorudayiz);
    void closeAllQuizzes(String userChatId);

}
