package com.cebrail.botum.DTO.DTOUtils.Mappers;

import com.cebrail.botum.Model.MySendPoll;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public final class SendPollRowMapper implements RowMapper<MySendPoll> {

    /**
     * This method does'nt set the MySendPoll'S PollOption's, that have to be done by the user of this method.
     * PollOptions should be set by a ResultSetExtractor.
     * @param resultSet A ResultSet object.
     * @return {@link MySendPoll} Returns a MySendPoll object.
     */
    @Override
    public MySendPoll mapRow(ResultSet resultSet) {
        MySendPoll mySendPoll = new MySendPoll();

        try {
            mySendPoll.setId(resultSet.getInt("mysendpollid"));
            mySendPoll.setQuizId(resultSet.getInt("f_quizid"));
            mySendPoll.setQuestion(resultSet.getString("question"));
            mySendPoll.setCorrectOptionId(resultSet.getInt("correctoptionid"));
            mySendPoll.setChatId(resultSet.getString("userchatid"));
            ((SendPoll) mySendPoll).setType("quiz");
            ((SendPoll) mySendPoll).setAnonymous(false);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return mySendPoll;
    }
}
