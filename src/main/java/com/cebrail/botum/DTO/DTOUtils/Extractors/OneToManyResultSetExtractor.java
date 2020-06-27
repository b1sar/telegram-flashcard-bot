package com.cebrail.botum.DTO.DTOUtils.Extractors;

import com.cebrail.botum.DTO.DTOUtils.Mappers.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class OneToManyResultSetExtractor<R, C, K> implements ResultSetExtractor<List<R>> {


    protected final RowMapper<R> rootMapper;
    protected final RowMapper<C> childMapper;

    /**
     * Creates a new {@link OneToManyResultSetExtractor} from the given {@link RowMapper}s.
     *
     * @param rootMapper {@link RowMapper} to map the root entity, must not be {@literal null}.
     * @param childMapper {@link RowMapper} to map the root entities, must not be {@literal null}.
     */


    /**
     * Creates a new {@link OneToManyResultSetExtractor} from the given {@link RowMapper}s
     *
     * @param rootMapper {@link RowMapper} to map the root entity, must not be {@literal null}.
     * @param childMapper {@link RowMapper} to map the root entities, must not be {@literal null}.
     */
    public OneToManyResultSetExtractor(RowMapper<R> rootMapper, RowMapper<C> childMapper) {

        /*
        Assert.notNull(rootMapper, "Root RowMapper must not be null!");
        Assert.notNull(childMapper, "Child RowMapper must not be null!");
         */

        this.childMapper = childMapper;
        this.rootMapper = rootMapper;
    }

    public List<R> extractData(ResultSet rs) throws SQLException {
        List<R> results = new ArrayList<R>();
        int row = 0;
        boolean more = rs.next();
        if (more) {
            row++;
        }
        while (more) {
            R root = rootMapper.mapRow(rs);
            K primaryKey = mapPrimaryKey(rs);
            if (mapForeignKey(rs) != null) {
                while (more && primaryKey.equals(mapForeignKey(rs))) {
                    System.err.println("helloo");
                    addChild(root, childMapper.mapRow(rs));
                    more = rs.next();
                    if (more) {
                        row++;  //let the control goes bact to the other class
                    }
                }
            }
            else {
                more = rs.next();
                if (more) {
                    row++;
                }
            }
            results.add(root);
        }
        return results;
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
    protected abstract K mapPrimaryKey(ResultSet rs) throws SQLException;

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
    protected abstract K mapForeignKey(ResultSet rs) throws SQLException;

    /**
     * Add the child object to the root object
     * This method must be implemented by subclasses.
     *
     * @param root the Root object
     * @param child the Child object
     */
    protected abstract void addChild(R root, C child);

}