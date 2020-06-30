package com.cebrail.botum.DTO.PollOption;

import com.cebrail.botum.Model.MyPollOption;
import com.cebrail.botum.Util.JDBCUtil;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MyPollOptionRepositoryImpl implements MyPollOptionRepository{
    @Override
    public void saveAll(List<MyPollOption> pollOptionList, Integer mySendPollGeneratedPrimaryKey) {
        for(MyPollOption myPollOption: pollOptionList) {
            save(myPollOption, mySendPollGeneratedPrimaryKey);
        }
    }

    @Override
    public void save(MyPollOption myPollOption, Integer mySendPollGeneratedPrimaryKey) {
        String sql = "insert into public.mypolloption(content, f_mysendpollid) values(?, ?);";

        Connection connection = null;

        try {
            connection = JDBCUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, myPollOption.getOptionContent());
            preparedStatement.setInt(2, mySendPollGeneratedPrimaryKey);

            preparedStatement.executeUpdate();

            JDBCUtil.commit(connection);
            JDBCUtil.closeConnection(connection);
            JDBCUtil.closeStatement(preparedStatement);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            JDBCUtil.rollback(connection);
            JDBCUtil.closeConnection(connection);
        } finally {
            JDBCUtil.closeConnection(connection);
        }
    }

    @Override
    public void delete(MyPollOption myPollOption) {
        String sql = "delete from mypolloption where mypolloption.mypolloptionid=?";

        Connection connection = null;

        try {
            connection = JDBCUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, myPollOption.getId());
            preparedStatement.executeUpdate();

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
    public List<String> getStringOptions(Integer sendPollId) {
        String sql = "select * from public.mypolloption where mysendpollid=?";
        List<String> heheh = null;//sql.get();
        return heheh;
    }

    @Override
    public Integer size() {
        String sql = "select count(*) size from mypolloption;";
        Integer size = -1;
        Connection connection = null;

        try {
            connection = JDBCUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                size = resultSet.getInt("size");
                System.err.println("MyPollOptionRepository.size() size= " + size);
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
