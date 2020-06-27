package com.cebrail.botum.DTO.DTOUtils.Mappers;

import java.sql.ResultSet;

public  interface RowMapper<T> {
    T mapRow(ResultSet rs);
}
