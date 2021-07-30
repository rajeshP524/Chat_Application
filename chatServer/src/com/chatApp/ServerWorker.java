package com.chatApp;

import com.controller.Controller;

import java.io.*;
import java.net.Socket;
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
                // if user tries to relogin being logged in
                if(user != null) send("you are already logged in!\n");
                else{
                    boolean isSuccessfulLogin = handleLogIn(tokens);
                    // storing the username of the user after successful login, for future use.
                    if(isSuccessfulLogin){
                        user = tokens[1];
                        server.addWorker(this); // maintaining a list of all active users
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
                execute any of the commands other than (login, register, quit)
                */
                if(user == null){
                    outputStream.write("login required\n".getBytes());
                }else{
                    handleUserCommands(input);
                }
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

    private void handleUserCommands(String input) {
        String[] tokens = input.split(" ");
        if(false){

        }else{
            try {
                outputStream.write("unknown command".getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleExit() {
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
            boolean isValidLogin = controller.isValidUser(username, password);
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
