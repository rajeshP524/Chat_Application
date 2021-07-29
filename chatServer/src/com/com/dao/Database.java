package com.com.dao;

import java.sql.*;

public class Database {
    private Connection con;
    private Statement st;
    public Database(String driver, String url, String username, String password) throws SQLException {

        this.con = DriverManager.getConnection(url, username, password);
        this.st = con.createStatement();
    }

    public ResultSet executeQuery(String query) throws SQLException {
        return st.executeQuery(query);
    }
}
