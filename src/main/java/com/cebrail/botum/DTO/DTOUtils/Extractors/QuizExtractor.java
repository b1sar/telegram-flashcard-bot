package com.cebrail.botum.DTO.DTOUtils.Extractors;

import com.cebrail.botum.DTO.DTOUtils.Mappers.RowMapper;
import com.cebrail.botum.Model.MyPollOption;
import com.cebrail.botum.Model.MySendPoll;
import com.cebrail.botum.Model.Quiz;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public final class QuizExtractor extends OneToManyAndOneToManyResultSetExtractor<Quiz, MySendPoll, MyPollOption, Integer, Integer>{

    RowMapper<Quiz> rootMapper;
    RowMapper<MySendPoll> childMapper;
    RowMapper<MyPollOption> grandChildMapper;

    public QuizExtractor(RowMapper<Quiz> rootMapper, RowMapper<MySendPoll> childMapper, RowMapper<MyPollOption> grandChildMapper) {
        super(rootMapper, childMapper, grandChildMapper);
        this.rootMapper = rootMapper;
        this.childMapper = childMapper;
        this.grandChildMapper = grandChildMapper;
    }

    @Override
    protected Integer mapRootPrimaryKey(ResultSet rs) throws SQLException {
        return rs.getInt("quiz_id");
    }


    @Override
    protected Integer mapChildForeignKey(ResultSet rs) throws SQLException {
        return rs.getInt("f_quizid");
    }


    @Override
    protected Integer mapChildPrimaryKey(ResultSet rs) throws SQLException {
        return rs.getInt("mysendpollid");
    }

    @Override
    protected Integer mapGrandChildForeignKey(ResultSet rs) throws SQLException {
        return rs.getInt("f_mysendpollid");
    }

    @Override
    protected void addChild(Quiz root, MySendPoll child) {
        root.addToTest(child);
    }

    @Override
    protected void addGrandChild(MySendPoll child, MyPollOption grandChild) {
        child.addToOptions(grandChild);
    }
}
