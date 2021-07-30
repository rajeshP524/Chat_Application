package com.controller;

import com.com.dao.Database;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Controller {
    Database db = null;
    public Controller() throws SQLException {
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/ChatApplicationDataBase";
        String username = "root";
        String password = "rockzzzz";
        db = new Database(driver, url, username, password);
    }

    public boolean isValidUser(String username, String password){
        String query = "select count(*) from users where username='"+username+"' and password='"+password+"'";
        try {
            ResultSet rs = db.executeQuery(query);
            int count = 0;
            if(rs.next()){
                count = rs.getInt(1);
            }
            if(count == 0){
                return false;
            }else{
                // reCheck, if this is a case inSensitive login
                query = "select * from users where username='"+username+"' and password='"+password+"'";
                rs = db.executeQuery(query);
                String uName = null;
                String uPwd = null;
                if(rs.next()){
                    uName = rs.getString(1);
                    uPwd = rs.getString(2);
                }
                if(uName.equals(username) && uPwd.equals(password)){
                    return true;
                }else{
                    return false;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return false;
    }

    public boolean addUser(String username, String password) throws SQLException {
        String query = "select count(*) from users where username='"+username+"'";
        try {
            ResultSet rs = db.executeQuery(query);
            int count = 0;
            if(rs.next()){
                count = rs.getInt(1);
            }
            if(count > 0){
                return false;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        query = "insert into users values('"+username+"', '"+password+"')";
        db.executeUpdate(query);
        return true;
    }
}
