package com.cebrail.botum.DTO.Quiz;

import com.cebrail.botum.DTO.DTOUtils.Extractors.QuizExtractor;
import com.cebrail.botum.DTO.DTOUtils.Mappers.RowMapper;
import com.cebrail.botum.DTO.SendPoll.MySendPollRepository;
import com.cebrail.botum.Model.MyPollOption;
import com.cebrail.botum.Model.MySendPoll;
import com.cebrail.botum.Model.Quiz;
import com.cebrail.botum.Util.JDBCUtil;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

@Repository
public final class QuizRepositoryImpl implements QuizRepository {

    final MySendPollRepository mySendPollRepository;

    final RowMapper<Quiz> quizRowMapper;
    final RowMapper<MySendPoll> sendPollMapper;
    final RowMapper<MyPollOption> pollOptionMapper;

    public QuizRepositoryImpl(MySendPollRepository mySendPollRepository,
                              RowMapper<Quiz> quizRowMapper,
                              RowMapper<MySendPoll> sendPollMapper,
                              RowMapper<MyPollOption> pollOptionMapper) {
        this.mySendPollRepository = mySendPollRepository;
        this.quizRowMapper = quizRowMapper;
        this.sendPollMapper = sendPollMapper;
        this.pollOptionMapper = pollOptionMapper;
    }

    /**
     * This saving operations is done in three main consecutive operations, which have 2 intersteps.
     * First, the quiz objects single fields are committed to the quiz table.
     * Secondly, the remaining {@link MySendPoll} objects are sent to {@link MySendPollRepository#saveAll(List, Integer)} to be saved
     * Then, {@link MySendPollRepository#saveAll(List, Integer)} method, delegates the saving operation of MySendPollObtion objects
     * to {@link com.cebrail.botum.DTO.PollOption.MyPollOptionRepository#saveAll(List, Integer)} to be saved.
     * Lastly, this operation is delegated to the {@link com.cebrail.botum.DTO.PollOption.MyPollOptionRepository#save(MyPollOption, Integer)} of the
     * same class.
     * @param quiz The Quiz object to be persist
     */
    @Override
    public int save(Quiz quiz) {
        String sql = "insert into public.quizz(userid, testinhangisorusundayiz, " +
                "ended, toplamdogrusayisi, toplamyanlissayisi) values(?, ?, ?, ?, ?);";

        Connection connection = null;

        Integer primaryKey = null;
        try {
            connection = JDBCUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, quiz.getUserId());
            preparedStatement.setInt(2, quiz.getTestingHangiSorusundayiz());
            preparedStatement.setBoolean(3, quiz.getEnded());
            preparedStatement.setInt(4, quiz.getToplamDogruSayisi());
            preparedStatement.setInt(5, quiz.getToplamYanlisSayisi());

            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            generatedKeys.next();
            primaryKey = generatedKeys.getInt("quiz_id");
            System.err.println("Generated Key : " +primaryKey);


            JDBCUtil.commit(connection);
            JDBCUtil.closeConnection(connection);
            JDBCUtil.closeStatement(preparedStatement);

            //Send sendPolls to sendPolls repository in order they to be committed to the database
            List<MySendPoll> sendPolls = quiz.getTest();
            for(MySendPoll mySendPoll: sendPolls) {
                //System.err.println(mySendPoll.toString());
            }
            mySendPollRepository.saveAll(sendPolls, primaryKey);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            JDBCUtil.rollback(connection);
        } finally {
            JDBCUtil.closeConnection(connection);
        }
        return primaryKey;
    }

    @Override
    public Quiz get(Integer quizId) {
        String sql = "SELECT * " +
                "FROM ((mysendpoll " +
                "INNER JOIN quizz ON mysendpoll.f_quizid = quizz.quiz_id) " +
                "INNER JOIN mypolloption ON mysendpoll.mysendpollid = mypolloption.f_mysendpollid) " +
                "where quizz.quiz_id=?;";

        List<Quiz> quizzes = null;

        Connection connection = null;

        try {
            connection = JDBCUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, quizId);
            ResultSet resultSet = preparedStatement.executeQuery();

            QuizExtractor quizExtractor = new QuizExtractor(quizRowMapper, sendPollMapper, pollOptionMapper);

            quizzes = quizExtractor.extractData(resultSet);

            for(Quiz q: quizzes) {
                System.err.println(q.toString());
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return quizzes.get(0);
    }

    @Override
    public List<Quiz> getAllByUserId(String chatId) {
        String sql = "SELECT * " +
                "FROM ((mysendpoll " +
                "INNER JOIN quizz ON mysendpoll.f_quizid = quizz.quiz_id) " +
                "INNER JOIN mypolloption ON mysendpoll.mysendpollid = mypolloption.f_mysendpollid) " +
                "where quizz.userid=?;";
        Connection connection = null;
        List<Quiz> quizzes = null;
        try {
            connection = JDBCUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, chatId);
            ResultSet resultSet = preparedStatement.executeQuery();

            QuizExtractor quizExtractor = new QuizExtractor(quizRowMapper, sendPollMapper, pollOptionMapper);

            quizzes = quizExtractor.extractData(resultSet);

            for(Quiz q: quizzes) {
                for(MySendPoll mySendPoll: q.getTest()) {
                    mySendPoll.setMyOptions(mySendPoll.getMyOptions());
                }
            }
            /*for test purposes
            for(Quiz q: quizzes) {
                System.err.println(q.toString());
            }
            /*--------------*/


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return quizzes;
    }

    @Override
    public void update(Quiz quiz) {

        String sql = "update quizz set testinhangisorusundayiz=?, ended=?, toplamdogrusayisi=?," +
                "toplamyanlissayisi=? where quiz_id=?";

        Connection connection = null;

        try {
            connection = JDBCUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, quiz.getTestingHangiSorusundayiz());
            ps.setBoolean(2, quiz.getEnded());
            ps.setInt(3, quiz.getToplamDogruSayisi());
            ps.setInt(4, quiz.getToplamYanlisSayisi());
            ps.setInt(5, quiz.getId());

            ps.executeUpdate();

            JDBCUtil.commit(connection);
            JDBCUtil.closeStatement(ps);
            JDBCUtil.closeConnection(connection);
        } catch (SQLException throwables) {
            JDBCUtil.rollback(connection);
            throwables.printStackTrace();
        } finally {
            JDBCUtil.closeConnection(connection);
        }


    }

    @Override
    public void delete(Quiz quiz) {

        List<MySendPoll> mySendPolls = quiz.getTest();
        for (MySendPoll mySendPoll: mySendPolls) {
            mySendPollRepository.delete(mySendPoll);
        }

        String sql = "delete from quizz where quizz.quiz_id=?";

        Connection connection = null;

        try {
            connection = JDBCUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, quiz.getId());

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
    public Integer deleteAllQuizzesByUserChatId(Long userChatId) {
        String sql = "delete from quizz where quizz.userid=?";
        Integer rowsAffected = 0;
        Connection connection = null;

        try {
            connection = JDBCUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, userChatId);
            rowsAffected = preparedStatement.executeUpdate();

            JDBCUtil.commit(connection);
            JDBCUtil.closeConnection(connection);
            JDBCUtil.closeStatement(preparedStatement);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rowsAffected;
    }

    @Override
    public void sonrakiSoruyaGec(Integer quizId, Integer hangiSorudayiz) {
        String sql = "update quizz set testinhangisorusundayiz=? where quiz_id=?;";

        Connection connection = null;

        try {
            connection = JDBCUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, hangiSorudayiz);
            preparedStatement.setInt(2, quizId);

            preparedStatement.executeUpdate();

            JDBCUtil.commit(connection);
            JDBCUtil.closeStatement(preparedStatement);
            JDBCUtil.closeConnection(connection);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            JDBCUtil.rollback(connection);
        } finally {
            JDBCUtil.closeConnection(connection);
        }
    }

    @Override
    public void closeAllQuizzes(String userChatId) {
        String sql = "update quizz set ended=true where userid=?";
        Connection connection = null;

        try {
            connection = JDBCUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, userChatId);
            ps.executeUpdate();

            JDBCUtil.commit(connection);
            JDBCUtil.closeStatement(ps);
        } catch (SQLException throwables) {
            JDBCUtil.rollback(connection);
            throwables.printStackTrace();
        } finally {
            JDBCUtil.closeConnection(connection);
        }

    }

    public Integer size() {
        String sql = "select count(*) size from quizz;";
        Integer size = -1;
        Connection connection = null;

        try {
            connection = JDBCUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                size = resultSet.getInt("size");
                System.err.println("QuizRepository.size() -> size= " + size);
            }
            else {
                System.err.println("Fuck you, in size() method of QuizRepositoryImpl.java");
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
