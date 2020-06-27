package com.cebrail.botum.DTO.DTOUtils.Mappers;

import com.cebrail.botum.Model.MyPollOption;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public final class PollOptionMapper implements RowMapper<MyPollOption> {
    @Override
    public MyPollOption mapRow(ResultSet rs) {
        MyPollOption myPollOption = new MyPollOption();

        try {
            myPollOption.setId(rs.getInt("mypolloptionid"));
            myPollOption.setMySendPollId(rs.getInt("f_mysendpollid"));
            myPollOption.setOptionContent(rs.getString("content"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return myPollOption;
    }
}
