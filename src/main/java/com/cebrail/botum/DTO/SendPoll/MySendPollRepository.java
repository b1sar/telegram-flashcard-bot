package com.cebrail.botum.DTO.SendPoll;

import com.cebrail.botum.Model.MySendPoll;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Only quizid, mysendpollid and the pollOptions properties of the SendPoll object are stored
 * in the database.
 * But there are other properties need to be stored too.
 * For example chatId, correctOptionId etc.
 *
 * Storing this object without storing the mentioned properties is completely meaningless.
 * TODO: Consider storing all the properties of the SendPoll object.
 */

@Repository
public interface MySendPollRepository {
    public void saveAll(List<MySendPoll> sendPollList, Integer quizPrimaryKey);
    public void save(MySendPoll mySendPoll, Integer quizPrimaryKey);
    public List<MySendPoll> getAll(Integer quizid);
    public MySendPoll get(Integer mysendpollid);
    public void delete(MySendPoll mySendPoll);
    Integer size();
}
