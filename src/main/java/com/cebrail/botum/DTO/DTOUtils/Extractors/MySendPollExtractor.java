package com.cebrail.botum.DTO.DTOUtils.Extractors;

import com.cebrail.botum.DTO.DTOUtils.Mappers.RowMapper;
import com.cebrail.botum.Model.MyPollOption;
import com.cebrail.botum.Model.MySendPoll;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public final class MySendPollExtractor extends OneToManyResultSetExtractor<MySendPoll, MyPollOption, String>{
    RowMapper<MySendPoll> rootMapper;
    RowMapper<MyPollOption> childMapper;

    /**
     * Creates a new {@link OneToManyResultSetExtractor} from the given {@link RowMapper}s
     *
     * @param rootMapper  {@link RowMapper} to map the root entity, must not be {@literal null}.
     * @param childMapper {@link RowMapper} to map the root entities, must not be {@literal null}.
     */
    public MySendPollExtractor(RowMapper<MySendPoll> rootMapper, RowMapper<MyPollOption> childMapper) {
        super(rootMapper, childMapper);
        this.rootMapper = rootMapper;
        this.childMapper = childMapper;
    }

    /**
     * Map the primary key value to the required type.
     * This method must be implemented by subclasses.
     * This method should not call <code>next()</code> on
     * the ResultSet; it is only supposed to map values of the current row.
     *
     * @param rs the ResultSet
     * @return the primary key value
     * @throws SQLException
     */
    @Override
    protected String mapPrimaryKey(ResultSet rs) throws SQLException {
        return rs.getString("mysendpollid");
    }

    /**
     * Map the foreign key value to the required type.
     * This method must be implemented by subclasses.
     * This method should not call <code>next()</code> on
     * the ResultSet; it is only supposed to map values of the current row.
     *
     * @param rs the ResultSet
     * @return the foreign key value
     * @throws SQLException
     */
    @Override
    protected String mapForeignKey(ResultSet rs) throws SQLException {
        return rs.getString("f_mysendpollid");
    }

    /**
     * Add the child object to the root object
     * This method must be implemented by subclasses.
     *
     * @param root  the Root object
     * @param child the Child object
     */
    @Override
    protected void addChild(MySendPoll root, MyPollOption child) {
        root.addToOptions(child);
    }
}
