package com.cebrail.botum.DTO.DTOUtils.Mappers;

import com.cebrail.botum.Model.Quiz;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public final class QuizRowMapper implements RowMapper<Quiz> {

    /**
     * This method does'nt set the Quiz'S SendPoll's, this have to be done by who use this method.
     * @param resultSet
     * @return
     */
    @Override
    public Quiz mapRow(ResultSet resultSet) {
        Quiz quiz = new Quiz();
        try {
            quiz.setId(resultSet.getInt("quiz_id"));
            quiz.setUserId(resultSet.getString("userid"));
            quiz.setTestingHangiSorusundayiz(resultSet.getInt("testinhangisorusundayiz"));//bu satırı unuttuğum için 4-5 saat uğraştım ammkkkkkkkkkk
            quiz.setToplamDogruSayisi(resultSet.getInt("toplamdogrusayisi"));
            quiz.setToplamYanlisSayisi(resultSet.getInt("toplamyanlissayisi"));
            quiz.setEnded(resultSet.getBoolean("ended"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return quiz;
    }
}
