package com.chatApp;

import com.controller.Controller;

import java.io.*;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServerWorker extends Thread{
    private Socket clientSocket = null;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;
    private Server server;
    private Controller controller;
    private String user = null;

    public ServerWorker(Server server, Socket clientSocket, Controller controller) throws IOException {
        this.clientSocket = clientSocket;
        this.inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();
        this.server = server;
        this.controller = controller;
    }

    public void run(){
        try {
            handleClientSocket();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleClientSocket() throws IOException, SQLException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String input;
        while((input = reader.readLine()) != null){
            String[] tokens = input.split(" ");
            String cmd = tokens[0];
            if("login".equalsIgnoreCase(cmd)){
                // if user tries to re-login being logged in
                if(user != null) send("you are already logged in!\n");
                else{
                    boolean isSuccessfulLogin = handleLogIn(tokens);
                    // storing the username of the user after successful login, for future use.
                    if(isSuccessfulLogin){
                        user = tokens[1];
                        server.addWorker(this); // maintaining a list of all active users

                        // receive undelivered messages for this user (as a receiver)
                        handleUndeliveredMessages();

                        // receive undelivered chatroom messages for this user (as a receiver)
                        handleUndeliveredChatroomMessages();

                        handleGetOrSendStatus(); // get status of other users and send status of current user
                    }
                }
            }
            else if("register".equalsIgnoreCase(cmd)){
                // not allowed to register, once logged in
                if(user != null){
                    send("you are already logged in! cannot register!!\n");
                }else{
                    handleRegister(tokens);
                }
            }
            else if("exit".equalsIgnoreCase(cmd) || "logout".equalsIgnoreCase(cmd) || "quit".equalsIgnoreCase(cmd)){
                handleExit();
                break;
            }
            else{
                /* user NULL indicates that, he/she is not logged in! so that particular user is not allowed
                execute any of the commands other than (login, register, quit/logout/exit)
                */
                if(user == null){
                    outputStream.write("login required\n".getBytes());
                }else{
                    handleUserCommands(input);
                }
            }

        }
    }

    // result msg format : msg <#chatroom> <sender> <msgBody>
    private void handleUndeliveredChatroomMessages() throws SQLException, IOException {
        String receiver = getUserName();
        List<String> resultSet = controller.getUndeliveredChatroomMessages(receiver);
        for(String msg : resultSet){
            send(msg + "\n");
        }
    }

    // result msg format : msg <received> <sender> <msgBody>
    private void handleUndeliveredMessages() throws SQLException, IOException {
        String receiver  = user;
        List<String> resultSet = controller.getUndeliveredMessages(receiver);

        for(String msg : resultSet){
            send(msg + "\n");
            String[] tokens = msg.split(" ", 4);
            String sender  = tokens[2];
            String msgBody = tokens[3];

            // after the undelivered msg has been received, it has to be stored on db
            try {
                controller.addMessage(msgBody, sender, user);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }

    private void handleGetOrSendStatus() throws IOException {
        List<ServerWorker> workerList = server.getWorkerList();

        // send status of every user on the network to the current user
        for(ServerWorker worker : workerList){
            if(!(worker.getUserName().equals(getUserName()))){
                String statusMsg = "online " + worker.getUserName() + "\n";
                send(statusMsg);
            }
        }

        // current user sends his status to every other user who are already on the network
        for(ServerWorker worker : workerList){
            if(!(worker.getUserName().equals(getUserName()))){
                String statusMsg = "online " + getUserName() + "\n";
                worker.send(statusMsg);
            }
        }

    }

    private void send(String msg) throws IOException {
        outputStream.write(msg.getBytes());
    }

    private void handleUserCommands(String input) throws IOException, SQLException {
        String[] tokens = input.split(" ");
        String cmd = tokens[0];
        if("msg".equalsIgnoreCase(cmd)){
            if(tokens.length > 2){
                // check if this a group message
                if(tokens[1].charAt(0) == '#'){
                    handleChatroomMessage(input);
                }
                else{
                    //handle direct messages
                    tokens = input.split(" ", 3);
                    handleMessage(tokens);
                }
            }else{
                send("unknown command\n");
            }
        }
        else if("join chatroom".equalsIgnoreCase(input)){
            handleJoinChatroom();
        }
        else if("leave chatroom".equalsIgnoreCase(input)){
            handleLeaveChatroom();
        }
        else if("isvalid".equalsIgnoreCase(cmd)){
            if(tokens.length > 1){
                if(tokens[1].equalsIgnoreCase("chatroom")){
                    isValidChatroomUser(input);
                }
                else{
                    handleIsValid(input);
                }
            }

        }
        else if("history".equalsIgnoreCase(cmd)){
            if(tokens.length == 2){
                if(tokens[1].charAt(0) == '#'){
                    handleChatroomHistory();
                }else{
                    handleHistory(tokens);
                }
            }else{
                send("unknown command\n");
            }


        } else{
            try {
                outputStream.write("unknown command\n".getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void isValidChatroomUser(String input) throws IOException {
        String tokens[] = input.split(" ");
        if(tokens.length == 3){
            boolean isMemberOfChatroom = controller.isMemberOfChatroom(tokens[2]);
            if(isMemberOfChatroom){
                String response = "yes " + tokens[2] + "\n";
                send(response);
            }
            else{
                String response = "no " + tokens[2] + "\n";
                send(response);
            }
        }
    }

    private void handleIsValid(String input) throws IOException {
        String[] tokens = input.split(" ", 2);
        if(tokens.length == 2){
            String username = tokens[1];
            boolean isValidUser = controller.isValidUser(username);
            if(isValidUser){
                String response = "valid " + username + "\n";
                send(response);
            }
            else{
                String response = "invalid " + username + "\n";
                send(response);
            }
        }
    }

    private void handleChatroomHistory() throws SQLException, IOException {
        List<String> resultSet = controller.getChatroomHistory();
        for(String result : resultSet){
            String[] tokens = result.split(" ", 2);
            String sender = tokens[0];
            String msgBody= tokens[1];
            if(sender.equals(getUserName())){
                sender = "you";
            }

            String msg = sender + "# " + msgBody + "\n";
            send(msg);
        }

    }

    private void handleChatroomMessage(String input) throws IOException, SQLException {

        //only members of chatroom are allowed send msgs at this room
        boolean isMemberOfChatroom = controller.isMemberOfChatroom(getUserName());
        if(!isMemberOfChatroom){
            String errMsg = "you are not a member of chatroom\n";
            send(errMsg);
            return;
        }

        String[] tokens = input.split(" ", 3);
        if(tokens.length == 3){
            // get members of chatroom
            List<String> resultSet = controller.getChatroomUsers();
            List<ServerWorker> workerList = server.getWorkerList(); //get list of active users
            resultSet.remove(getUserName()); //as a sender remove yourself from receiving message
            for(ServerWorker worker : workerList){
                String username = worker.getUserName();
                if(resultSet.contains(username)){
                    String msg = "msg #chatroom "+getUserName()+" "+tokens[2]+"\n";
                    worker.send(msg);

                    resultSet.remove(username);
                }
            }

            //store chatroom messages on database;
            String sender = getUserName();
            String msgBody = tokens[2];
            controller.addChatroomMessage(sender, msgBody);

            //now resultSet contains list of members who are undelivered
            // storing undelivered chatroom members at serverside db, so they can get delivered chatroom messages when online.
            for(String receiver : resultSet){
                controller.addUndeliveredChatroomMessage(sender, receiver, msgBody);
            }

        }
        else{
            send("unknown command\n");
        }
    }

    private void handleLeaveChatroom() throws IOException, SQLException {
        boolean isMemberOfChatroom = controller.isMemberOfChatroom(getUserName());
        if(isMemberOfChatroom){
            controller.removeChatroomUser(getUserName());
            String msg = "you are removed from the chatroom\n";
            send(msg);
        }
        else{
            String errMsg = "you are not a member of chatroom\n";
            send(errMsg);
        }
    }

    private void handleJoinChatroom() throws IOException, SQLException {
        boolean isMemberOfChatroom = controller.isMemberOfChatroom(getUserName());
        if(isMemberOfChatroom){
            String errMsg = "you are already a member of chatroom\n";
            send(errMsg);
        }
        else{
            controller.addChatroomUser(getUserName());
            String msg = "you are added to chatroom\n";
            send(msg);
        }
    }

    //History receive format : sender <msgBody>
    private void handleHistory(String[] tokens) throws IOException, SQLException {
        if(tokens.length == 2){
            String peer = tokens[1];
            List<String> resultSet = controller.getChatHistory(user, peer);

            for(String chatMsg : resultSet){
                String[] chatTokens = chatMsg.split(" ", 2);
                String sender = chatTokens[0];
                String msgBody = chatTokens[1];

                if(sender.equals(user)){
                    sender = "you";
                }

                String msg = sender + "# " + msgBody +"\n";
                send(msg);
            }
        }
        else{
            send("unknown command\n");
        }
    }

    // send msg format : msg <sendTo> <msgBody>
    // receive msg format: msg <received> <from> <msgBody>
    private void handleMessage(String[] tokens) throws IOException {
        List<ServerWorker> workerList = server.getWorkerList();
        if(tokens.length == 3){
            String sendTo = tokens[1];
            String msgBody = tokens[2];

            //check if receiver is a valid user
            if(!controller.isValidUser(sendTo)) {
                send("no such user ("+sendTo+") exists!\n");
                return;
            }

            // sends msg, only to an active user
            for(ServerWorker worker : workerList){
                if(worker.getUserName().equals(sendTo)){
                    String msg = "msg [received] " +user+ " " + msgBody + "\n";
                    worker.send(msg);
                    //store successfully sent messages in db
                    try {
                        controller.addMessage(msgBody, user, sendTo);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    return;
                }
            }

            // storing undelivered messages at serverSide in a separate table;
            try {
                controller.addUndeliveredMessage(msgBody, user, sendTo);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }
        else{
            String errMsg = "unknown command\n";
            send(errMsg);
        }
    }

    private void handleExit() throws IOException {
        List<ServerWorker> workerList = server.getWorkerList();
        //remove this user from list of active users
        server.removeWorker(this);
        for(ServerWorker worker : workerList){
            String statusMsg = "offline " + getUserName() + "\n";
            worker.send(statusMsg);
        }

        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //command format : register <username> <password>
    private void handleRegister(String[] tokens) throws SQLException, IOException {
        if(tokens.length == 3){
            String username = tokens[1];
            String password = tokens[2];
            // '#' is used for representing chatroom
            if(username.contains("#")){
                String errMsg = "\'#\' as a part of username isn't allowed\n";
                send(errMsg);
                return;
            }
            boolean isSuccessfulRegistration = controller.addUser(username, password);
            if(isSuccessfulRegistration){
                outputStream.write("registration successful\n".getBytes());
            }else{
                outputStream.write("username already exists\n".getBytes());
            }
        }else{
            outputStream.write("unknown command/ enter a proper username\n".getBytes());
        }
    }

    // command format : login <username> <password>
    private boolean handleLogIn(String[] tokens) throws IOException {
        if(tokens.length == 3){
            String username = tokens[1];
            String password = tokens[2];
            boolean isValidLogin = controller.isValidLogin(username, password);
            if(isValidLogin){
                outputStream.write("login successful\n".getBytes());
                return true;
            }else{
                outputStream.write("error login\n".getBytes());
                return false;
            }
        }else{
            outputStream.write("unknown command\n".getBytes());
            return false;
        }
    }

    public String getUserName(){
        return user;
    }
}
