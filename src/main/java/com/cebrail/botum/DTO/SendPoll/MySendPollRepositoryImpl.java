package com.cebrail.botum.DTO.SendPoll;


import com.cebrail.botum.DTO.DTOUtils.Extractors.MySendPollExtractor;
import com.cebrail.botum.DTO.DTOUtils.Mappers.RowMapper;
import com.cebrail.botum.DTO.PollOption.MyPollOptionRepository;
import com.cebrail.botum.Model.MyPollOption;
import com.cebrail.botum.Model.MySendPoll;
import com.cebrail.botum.Util.JDBCUtil;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Repository
public class MySendPollRepositoryImpl implements MySendPollRepository{
    private final MyPollOptionRepository myPollOptionRepository;
    private final MySendPollExtractor mySendPollExtractor;
    private final RowMapper<MySendPoll> sendPollRowMapper;
    private final RowMapper<MyPollOption> pollOptionMapper;

    public MySendPollRepositoryImpl(MyPollOptionRepository myPollOptionRepository, MySendPollExtractor mySendPollExtractor, RowMapper<MySendPoll> sendPollRowMapper, RowMapper<MyPollOption> pollOptionMapper) {

        this.myPollOptionRepository = myPollOptionRepository;
        this.mySendPollExtractor = mySendPollExtractor;
        this.sendPollRowMapper = sendPollRowMapper;
        this.pollOptionMapper = pollOptionMapper;
    }


    @Override
    public void saveAll(List<MySendPoll> sendPollList, Integer quizPrimaryKey) {
        for(MySendPoll mySendPoll: sendPollList) {
            save(mySendPoll, quizPrimaryKey);
        }
    }

    @Override
    public void save(MySendPoll mySendPoll, Integer quizPrimaryKey) {
        String sql = "insert into public.mysendpoll(f_quizid, question, correctoptionid, userchatid) values(?, ?, ?, ?);";

        Connection connection = null;

        try {
            connection = JDBCUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, quizPrimaryKey);
            preparedStatement.setString(2, mySendPoll.getQuestion());
            preparedStatement.setInt(3, mySendPoll.getCorrectOptionId());
            preparedStatement.setString(4, mySendPoll.getChatId());

            //TODO: burası çok tehlikeli, kesin hata çıkacak,normalde Optional yoktu ama null değeri çıktığı için geçici olarak default olarak 0 veriyorum. sorunun
            // kaynağını bul.
            preparedStatement.setInt(3, Optional.of(mySendPoll.getCorrectOptionId()).orElse(0));


            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            Integer mySendPollGeneratedPK = null;
            while(generatedKeys.next()) {
                mySendPollGeneratedPK = generatedKeys.getInt("mysendpollid");
            }

            JDBCUtil.commit(connection);
            JDBCUtil.closeConnection(connection);
            JDBCUtil.closeStatement(preparedStatement);

            List<MyPollOption> mysendPollOptions = mySendPoll.getMyOptions();
            myPollOptionRepository.saveAll(mysendPollOptions, mySendPollGeneratedPK);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            JDBCUtil.rollback(connection);
            JDBCUtil.closeConnection(connection);
        } finally {
            JDBCUtil.closeConnection(connection);
        }
    }

    @Override
    public List<MySendPoll> getAll(Integer quizid) {
        String sql = "select * from public.mysendpoll where quizid=?";
        List<MySendPoll> result = null; //= sql.get();


        for(MySendPoll mySendPoll: result) {
            mySendPoll.setOptions(myPollOptionRepository.getStringOptions(mySendPoll.getId()));
        }
        return result;
    }

    @Override
    public MySendPoll get(Integer mysendpollid) {
        String sql = "select * from mysendpoll join mypolloptions on mysendpoll.mysendpollid=mypolloptions.f_mysendpollid and mysendpoll.mysendpollid=?";

        Connection connection = null;
        List<MySendPoll> mySendPolls = null;
        try {
            connection = JDBCUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, mysendpollid);
            ResultSet resultSet = preparedStatement.executeQuery();


            mySendPolls = mySendPollExtractor.extractData(resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return mySendPolls.get(0);
    }

    @Override
    public void delete(MySendPoll mySendPoll) {

        String sql = "delete from mysendpoll where mysendpoll.mysendpollid=?";

        Connection connection = null;

        try {
            connection = JDBCUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, mySendPoll.getId());

            preparedStatement.executeUpdate();

            for(MyPollOption myPollOption: mySendPoll.getMyOptions()) {
                myPollOptionRepository.delete(myPollOption);
            }

            JDBCUtil.commit(connection);
            JDBCUtil.closeConnection(connection);
            JDBCUtil.closeStatement(preparedStatement);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            JDBCUtil.rollback(connection);
        } finally {
            JDBCUtil.closeConnection(connection);
        }
    }

    @Override
    public Integer size() {
        String sql = "select count(*) size from mysendpoll;";
        Integer size = -1;
        Connection connection = null;

        try {
            connection = JDBCUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                size = resultSet.getInt("size");
                System.err.println("MySendPollId size -> " + size);
            }
            else {
                System.err.println("Fuck you, in size() method of MySendPollRepository.java");
            }

            JDBCUtil.commit(connection);
            JDBCUtil.closeConnection(connection);
            JDBCUtil.closeResultSet(resultSet);
            JDBCUtil.closeStatement(preparedStatement);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            JDBCUtil.rollback(connection);
        } finally {
            JDBCUtil.closeConnection(connection);
        }

        return size;
    }


}
