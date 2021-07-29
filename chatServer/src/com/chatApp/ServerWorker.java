package com.chatApp;

import com.controller.Controller;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;

public class ServerWorker extends Thread{
    Socket clientSocket = null;
    InputStream inputStream = null;
    OutputStream outputStream = null;
    Server server;
    Controller controller;

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
                handleLogIn(tokens);
            }
            if("register".equalsIgnoreCase(cmd)){
                handleRegister(tokens);
            }
        }
    }

    //command format : register <username> <password>
    private void handleRegister(String[] tokens) throws SQLException, IOException {
        if(tokens.length == 3){
            String username = tokens[1];
            String password = tokens[2];
            boolean isSuccessfulRegistration = controller.addUser(username, password);
            if(isSuccessfulRegistration){
                outputStream.write("registration successful\n".getBytes());
            }else{
                outputStream.write("username already exists\n".getBytes());
            }
        }else{
            outputStream.write("unknown command\n".getBytes());
        }
    }

    // command format : login <username> <password>
    private void handleLogIn(String[] tokens) throws IOException {
        if(tokens.length == 3){
            String username = tokens[1];
            String password = tokens[2];
            boolean isValidLogin = controller.isValidUser(username, password);
            if(isValidLogin){
                outputStream.write("login successful\n".getBytes());
            }else{
                outputStream.write("error login\n".getBytes());
            }
        }else{
            outputStream.write("unknown command\n".getBytes());
        }
    }
}
