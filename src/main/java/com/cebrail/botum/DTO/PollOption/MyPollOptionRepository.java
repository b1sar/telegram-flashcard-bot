package com.cebrail.botum.DTO.PollOption;

import com.cebrail.botum.Model.MyPollOption;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MyPollOptionRepository {

    void saveAll(List<MyPollOption> pollOptionList, Integer sendPollGeneratedPrimaryKey);
    void save(MyPollOption myPollOption, Integer sendPollGeneratedPrimaryKey);
    void delete(MyPollOption myPollOption);
    List<String> getStringOptions(Integer sendPollId);

    //size
    Integer size();
}
