package com.controller;

import com.com.dao.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    Database db = null;
    public Controller() throws SQLException {
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/ChatApplicationDataBase";
        String username = "root";
        String password = "rockzzzz";
        db = new Database(driver, url, username, password);
    }

    public boolean isValidLogin(String username, String password){
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

    public void addMessage(String msgBody, String sender, String receiver) throws SQLException {
        String query = "insert into messages values('" +sender+ "', '"+receiver+"', '"+msgBody+"', CURRENT_TIMESTAMP)";
        db.executeUpdate(query);
    }

    public void addUndeliveredMessage(String msgBody, String sender, String receiver) throws SQLException {
        String query = "insert into toBeDelivered values('" +sender+ "', '"+receiver+"', '"+msgBody+"', CURRENT_TIMESTAMP)";
        db.executeUpdate(query);
    }

    public boolean isValidUser(String username) {
        String query = "select count(*) from users where username='"+username+"'";
        try {
            ResultSet rs = db.executeQuery(query);
            int count = 0;
            if(rs.next()){
                count = rs.getInt(1);
            }
            if(count > 0){
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return false;
    }

    public List<String> getUndeliveredMessages(String receiver) throws SQLException {
        List<String> resultSet = new ArrayList<>();

        String query = "select * from toBeDelivered where receiver='"+receiver+"'";
        ResultSet rs = db.executeQuery(query);

        while(rs.next()){
            String sender = rs.getString(1);
            String msgBody = rs.getString(3);

            String msg = "msg [received] " +sender+ " " + msgBody + "\n";
            resultSet.add(msg);
        }


        //now after the messages has been delivered, those messages has to be removed from database
        query = "delete from toBeDelivered where receiver='"+receiver+"'";
        db.executeUpdate(query);
        return resultSet;
    }


    public List<String> getChatHistory(String user1, String user2) throws SQLException {
        String query = "select * from (select * from messages as table1 where sender='"+user1+"' and receiver='"+user2+"' union select * from messages as table2 where sender='"+user2+"' and receiver='"+user1+"') as table3 order by timestamp asc";
        ResultSet rs = db.executeQuery(query);
        List<String> resultSet = new ArrayList<>();
        while(rs.next()){
            String sender = rs.getString(1);
            String msgBody = rs.getString(3);
            String chatMsg = sender + " " + msgBody;
            resultSet.add(chatMsg);
        }
        return resultSet;
    }

    public boolean isMemberOfChatroom(String username) {
        String query = "select count(*) from membersOfChatroom where username='"+username+"'";
        try {
            ResultSet rs = db.executeQuery(query);
            int count = 0;
            if(rs.next()){
                count = rs.getInt(1);
            }
            if(count > 0){
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return false;
    }

    public void addChatroomUser(String username) throws SQLException {
        String query = "insert into membersOfChatroom values('"+username+"')";
        db.executeUpdate(query);
    }

    public void removeChatroomUser(String username) throws SQLException {
        String query = "delete from membersOfChatroom where username='"+username+"'";
        db.executeUpdate(query);
    }

    public List<String> getChatroomUsers() throws SQLException {
        String query = "select * from membersOfChatroom";
        List<String> resultSet = new ArrayList<>();
        ResultSet rs = db.executeQuery(query);
        while(rs.next()){
            String user = rs.getString(1);
            resultSet.add(user);
        }

        return resultSet;
    }

    public void addChatroomMessage(String sender, String msgBody) throws SQLException {
        String query = "insert into chatroomMessages values('"+sender+"', '"+msgBody+"', CURRENT_TIMESTAMP)";
        db.executeUpdate(query);
    }
}
