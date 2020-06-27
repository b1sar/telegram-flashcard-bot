package com.cebrail.botum.DTO.DTOUtils.Mappers;

import com.cebrail.botum.Model.Entry;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public final class EntryRowMapper implements RowMapper<Entry>{

    @Override
    public Entry mapRow(ResultSet rs) {
        Entry entry = new Entry();
        try {
            entry.setUserChatId(rs.getLong("userchatid"));
            entry.setAnlami(rs.getString("anlami"));
            entry.setKelime(rs.getString("kelime"));
            entry.setId(rs.getLong("id"));
            entry.setTarihi(
                    new java.sql.Timestamp(
                            rs.getDate("tarih").getTime()
                    ).toLocalDateTime()
            );
            entry.setHowManyTimesAsked(rs.getInt("howmanytimesasked"));
            entry.setCorrectAnswerCount(rs.getInt("correctanswercount"));
            entry.setIncorrectAnswerCount(rs.getInt("incorrectanswercount"));
            entry.setCorrectnesPercentage(rs.getDouble("correctnespercentage"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return entry;
    }
}
