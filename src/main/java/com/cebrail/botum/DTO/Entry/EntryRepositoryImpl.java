package com.cebrail.botum.DTO.Entry;

import com.cebrail.botum.DTO.DTOUtils.Mappers.RowMapper;
import com.cebrail.botum.Model.Entry;
import com.cebrail.botum.Util.JDBCUtil;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class EntryRepositoryImpl implements EntryRepository{
    final RowMapper<Entry> entryRowMapper;

    public EntryRepositoryImpl(RowMapper<Entry> entryRowMapper) {
        this.entryRowMapper = entryRowMapper;
    }

    @Override
    public void saveEntry(Entry entry) {
        Connection connection = null;
        try {
            connection = JDBCUtil.getConnection();
            String sql = "INSERT INTO public.entry1(userchatid, tarih, kelime, incorrectanswercount," +
                    " howmanytimesasked, correctanswercount, anlami, correctnespercentage) VALUES(?, ?, ?, ?, ?, ?, ?, ?);";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);//1


            preparedStatement.setLong(1, entry.getUserChatId());
            preparedStatement.setDate(2, Date.valueOf(entry.getTarihi().toLocalDate()));
            preparedStatement.setString(3, entry.getKelime());
            preparedStatement.setInt(4, entry.getIncorrectAnswerCount());
            preparedStatement.setInt(5, entry.getHowManyTimesAsked());
            preparedStatement.setInt(6, entry.getCorrectAnswerCount());
            preparedStatement.setString(7, entry.getAnlami());
            preparedStatement.setDouble(8, entry.getCorrectnesPercentage());

            //Integer effectedRows = preparedStatement.executeUpdate(sql);
            //above line causes a blabla error
            //thats because we've already give the query to the preparedstatement at the beginning
            //giving an another or the same query to execute is problematic.
            Integer effectedRows = preparedStatement.executeUpdate();

            JDBCUtil.commit(connection);
            JDBCUtil.closeConnection(connection);
            JDBCUtil.closeStatement(preparedStatement);
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            JDBCUtil.rollback(connection);

        } finally {
            JDBCUtil.closeConnection(connection);
        }
    }

    @Override
    public void saveAllEntries(List<Entry> entryList){
        for(Entry entry: entryList) {
            saveEntry(entry);
        }
    }

    //TODO: List<Entry> döndörmeli normalde, çünkü birden fazla row gelebilir. bu yanlış bir kullanım esasında
    @Override
    public Entry getEntryBy(String columnName, Object value, String userChatId) {
        String sql = "SELECT * FROM public.entry1 WHERE entry1."+columnName+"=? and userchatid=?";
        Connection connection = null;
        Entry theEntry = null;

        try {
            connection = JDBCUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            switch (columnName) {
                case "correctenspercentage"   -> {
                    preparedStatement.setDouble(1, (Double) value);
                    preparedStatement.setString(2, userChatId);
                }
                case "kelime", "anlami"   -> {
                    preparedStatement.setString(1, (String) value);
                    preparedStatement.setString(2, userChatId);
                }
                case "tarihi"    -> {
                    preparedStatement.setDate(1, Date.valueOf(((LocalDateTime) value).toLocalDate()));//düzeltilmeli
                    preparedStatement.setString(2, userChatId);
                }
                case "userchatid", "correctanswercount", "howmanytimesasked", "incorrectanswercount" -> {
                    preparedStatement.setInt(1, (Integer) value);
                    preparedStatement.setString(2, userChatId);
                }
                case "id" -> {
                    preparedStatement.setLong(1, (Long) value);
                    preparedStatement.setString(2, userChatId);
                }
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            theEntry = this.entryRowMapper.mapRow(resultSet);

            JDBCUtil.commit(connection);
            JDBCUtil.closeStatement(preparedStatement);
            JDBCUtil.closeResultSet(resultSet);
            JDBCUtil.closeConnection(connection);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            JDBCUtil.rollback(connection);
        } finally {
            JDBCUtil.closeConnection(connection);
        }
        return theEntry;
    }
    @Override
    public Entry getEntryById(Long EntryId, String userChatId) {
        return getEntryBy("id", EntryId, userChatId);
        /*
        //getEntryBy fonksiyonu yokken bu kodlar kullanılıyordu
        String sql = "SELECT * FROM public.entry1 where public.entry1.id=?";

        Connection connection = null;
        Entry theEntry = null;
        try {
            connection = JDBCUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, EntryId);
            ResultSet resultSet = preparedStatement.executeQuery();
            theEntry = EntryRowMapper.map(resultSet);

            JDBCUtil.commit(connection);
            JDBCUtil.closeStatement(preparedStatement);
            JDBCUtil.closeResultSet(resultSet);
            JDBCUtil.closeConnection(connection);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            JDBCUtil.rollback(connection);
            JDBCUtil.closeConnection(connection);
        } finally {
            JDBCUtil.closeConnection(connection);
        }

        return theEntry;
         */
    }

    @Override
    public Entry getEntryByKelime(String kelime, String userChatId) {
        return getEntryBy("kelime", kelime, userChatId);
    }

    @Override
    public Entry getEntryByAnlam(String anlam, String userChatId) {
        return getEntryBy("anlami", anlam, userChatId);
    }

    @Override
    public List<Entry> getEntriesByUserChatId(String userChatId) {
        String sql = "SELECT * FROM public.entry1 where public.entry1.userchatid =?";

        Connection connection = null;
        List<Entry> entries = new ArrayList<>();
        try {
            connection = JDBCUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userChatId);
            ResultSet resultSet = preparedStatement.executeQuery();

            //çalışmayabilir burayı kontrol et.
            while(resultSet.next()) {
                entries.add(this.entryRowMapper.mapRow(resultSet));
            }

            JDBCUtil.commit(connection);
            JDBCUtil.closeResultSet(resultSet);
            JDBCUtil.closeStatement(preparedStatement);
            JDBCUtil.closeConnection(connection);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            JDBCUtil.rollback(connection);
            //JDBCUtil.closeConnection(connection);//no need to do this, as the connection'll be closed at the finally block
        } finally {
            JDBCUtil.closeConnection(connection);
        }


        return entries;
    }

    //TODO: Henüz tamamlanmamış bir fonksiyon bu
    public void updateEntry_By(String __,Object __value,  String columnName, Object newEntity) {
        String sql = "UPDATE public.entry1 SET public.entry1."+__+"=? WHERE public.entry1."+columnName+"=?";

        Connection connection = null;
        try {
            connection = JDBCUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            switch (__) {
                case "id"-> preparedStatement.setInt(1, (Integer) __value);
            }

            switch (columnName) {
                case "kelime"-> preparedStatement.setString(2, (String) newEntity);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }



    @Override
    public void updateEntry(Entry entry) {
        String sql = "UPDATE public.entry1 SET userchatid=?, tarih=?, kelime=?, incorrectanswercount=?, " +
                "howmanytimesasked=?, correctanswercount=?, anlami=?, correctnespercentage=?" +
                "WHERE id=?;";
        Connection connection = null;

        try {
            connection = JDBCUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            //set values
            preparedStatement.setLong(1, entry.getUserChatId());
            preparedStatement.setDate(2,  Date.valueOf(entry.getTarihi().toLocalDate()));
            preparedStatement.setString(3, entry.getKelime());
            preparedStatement.setInt(4, entry.getIncorrectAnswerCount());
            preparedStatement.setInt(5, entry.getHowManyTimesAsked());
            preparedStatement.setInt(6, entry.getCorrectAnswerCount());
            preparedStatement.setString(7, entry.getAnlami());
            preparedStatement.setDouble(8, entry.getCorrectnesPercentage());
            preparedStatement.setLong(9, entry.getId());


            preparedStatement.executeUpdate();

            JDBCUtil.commit(connection);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            JDBCUtil.rollback(connection);
        } finally {
            JDBCUtil.closeConnection(connection);
        }

    }

    @Override
    public Integer deleteEntry(Entry entry) {
        return deleteEntryByKelime(entry.getKelime(), entry.getUserChatId().toString());
    }

    @Override
    public Integer deleteEntryByEntryId(Integer entryId) {
        //deleteBy kullanılmadan önce bu kodlar kullanılıyordu
        String sql = "DELETE FROM public.entry1 WHERE public.entry1.id=?";
        Connection connection = null;
        Integer rowsEffected =0;
        try {
            connection = JDBCUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, entryId);
            rowsEffected = preparedStatement.executeUpdate();
            System.out.println(rowsEffected + " tane kelime silindi");

            JDBCUtil.commit(connection);
            JDBCUtil.closeStatement(preparedStatement);
            JDBCUtil.closeConnection(connection);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            JDBCUtil.rollback(connection);
            JDBCUtil.closeConnection(connection);
        } finally {
            JDBCUtil.closeConnection(connection);
        }
        return rowsEffected;
    }

    //mükemmel çalışıyor :)
    public Integer deleteBy(String columnName, Object value, String userChatId) {
        String sql = "DELETE FROM public.entry1 WHERE public.entry1."+columnName+"=? and userchatid=?";
        Connection connection = null;
        Integer rowsEffected  = 0;

        try {
            connection = JDBCUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            switch (columnName) {
                case "correctenspercentage"   -> {
                    preparedStatement.setDouble(1, (Double) value);
                    preparedStatement.setString(2, userChatId);
                }
                case "kelime", "anlami", "id"   -> {
                    preparedStatement.setString(1, (String) value);
                    preparedStatement.setString(2, userChatId);
                }
                case "tarihi"    -> {
                    preparedStatement.setDate(1, Date.valueOf(((LocalDateTime) value).toLocalDate()));//düzeltilmeli
                    preparedStatement.setString(2, userChatId);
                }
                case "userchatid", "correctanswercount", "howmanytimesasked", "incorrectanswercount" -> {
                    preparedStatement.setInt(1, (Integer) value);
                    preparedStatement.setString(2, userChatId);
                }
            }

            rowsEffected = preparedStatement.executeUpdate();
            System.out.println(rowsEffected + " tane kelime silindi");

            JDBCUtil.commit(connection);
            JDBCUtil.closeStatement(preparedStatement);
            JDBCUtil.closeConnection(connection);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            JDBCUtil.rollback(connection);
            JDBCUtil.closeConnection(connection);
        } finally {
            JDBCUtil.closeConnection(connection);
        }

        return rowsEffected;
    }

    @Override
    public Integer deleteAllEntriesOfUserId(String userChatId) {
        String sql = "DELETE FROM public.entry1 WHERE public.entry1.userchatid = ?";
        Connection connection = null;
        Integer rowsEffected  = 0;
        try {
            connection = JDBCUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userChatId);
            rowsEffected = preparedStatement.executeUpdate();
            System.out.println(rowsEffected + " tane row silindi");

            JDBCUtil.commit(connection);
            JDBCUtil.closeConnection(connection);
            JDBCUtil.closeStatement(preparedStatement);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            JDBCUtil.rollback(connection);
        }
        return rowsEffected;
    }

    @Override
    public Integer deleteEntries(List<Entry> entries) {
        Integer rowsEffected = 0;
        for(Entry e: entries) {
            rowsEffected+= deleteBy("id", e.getId(), e.getUserChatId().toString());
        }
        return rowsEffected;
    }

    @Override
    public Integer deleteEntryByKelime(String kelime, String userChatId) {
        return deleteBy("kelime", kelime, userChatId);
        /*
        //deleteBy fonksiyonu olmadan önce bu kodlar kullanılıyordu
        String sql = "DELETE FROM public.entry1 WHERE public.entry1.kelime=?";
        Connection connection = null;

        try {
            connection = JDBCUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, kelime);
            Integer rowsEffected = preparedStatement.executeUpdate();
            System.out.println(rowsEffected + " tane kelime silindi");

            JDBCUtil.commit(connection);
            JDBCUtil.closeStatement(preparedStatement);
            JDBCUtil.closeConnection(connection);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            JDBCUtil.rollback(connection);
            JDBCUtil.closeConnection(connection);
        } finally {
            JDBCUtil.closeConnection(connection);
        }

         */
    }

    //Sorguyu daha doğru ve efficient şekilde yazabilirsin, aşırı saçma şu an, sorguyu değiştir.
    @Override
    public boolean exists(String kelime, String userChatId) {
        String sql = "SELECT * FROM public.entry1 WHERE public.entry1.userchatid=? and public.entry1.kelime LIKE ?";
        Connection connection = null;
        int ehe = 0;
        try {
            connection = JDBCUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userChatId);
            preparedStatement.setString(2, kelime);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                ehe = resultSet.getInt(1);
            }


            JDBCUtil.commit(connection);
            JDBCUtil.closeConnection(connection);
            JDBCUtil.closeStatement(preparedStatement);
            JDBCUtil.closeResultSet(resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            JDBCUtil.rollback(connection);
        }

        System.out.print(kelime+"kelimesinin varlığı kontrol ediliyor: ");
        if(ehe>0)
        {
            System.out.println(" kelime bulunamadı");
            return true;
        }
        else {
            System.out.println(" kelime bulundu");
            return false;
        }
    }

    public Integer numberOfEntriesOfUser(String userChatId) {
        String sql = "SELECT COUNT(*) as numberofentries FROM public.entry1 WHERE public.entry1.userchatid=?";
        Connection connection = null;
        Integer size = 0;
        try {
            connection  = JDBCUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userChatId);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            size = resultSet.getInt("numberofentries");

            JDBCUtil.commit(connection);
            JDBCUtil.closeStatement(preparedStatement);
            JDBCUtil.closeResultSet(resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            JDBCUtil.rollback(connection);
        } finally {
            JDBCUtil.closeConnection(connection);
        }
        return size;
    }

    @Override
    public Integer size(String userChatId) {
        String sql = "select count(*) as size from entry1 where userchatid=?";

        Integer size = null;
        Connection connection = null;

        try {
            connection = JDBCUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userChatId);
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            size = rs.getInt("size");

            JDBCUtil.closeConnection(connection);
            JDBCUtil.closeStatement(preparedStatement);
            JDBCUtil.closeResultSet(rs);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            JDBCUtil.closeConnection(connection);
        }
        return size;
    }
}
