package com.cebrail.botum.Util;

import com.cebrail.botum.Botum;

import java.sql.*;


public class JDBCUtil {

    private JDBCUtil(){
        new AssertionError("Don't instantiate the utility class");
    }
    public static Connection getConnection() throws SQLException {

        // Register the Java DB embedded JDBC driver
        Driver derbyEmbeddedDriver = new org.postgresql.Driver();
        DriverManager.registerDriver(derbyEmbeddedDriver);

        // Construct the connection URL
        String dbURL = Botum.properties.getProperty("mydburl");
        String userId = Botum.properties.getProperty("mydbuser");
        String password = Botum.properties.getProperty("mydbpassword");

        // Get a connection
        Connection conn = DriverManager.getConnection(dbURL, userId, password);

        // Set the auto-commit mode off
        conn.setAutoCommit(false);
        return conn;
    }

    public static void closeConnection(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void closeStatement(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeResultSet(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void commit(Connection conn) {
        try {
            if (conn != null) {
                conn.commit();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void rollback(Connection conn) {
        try {
            if (conn != null) {
                conn.rollback();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Connection conn = null;
        try {
            conn = JDBCUtil.getConnection();
            System.out.println("Connoected to the database.");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            JDBCUtil.closeConnection(conn);
        }
    }
}
