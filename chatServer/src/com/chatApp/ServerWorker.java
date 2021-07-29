package com.chatApp;

import com.controller.Controller;

import java.io.*;
import java.net.Socket;

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClientSocket() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String input;
        while((input = reader.readLine()) != null){
            String[] tokens = input.split(" ", 3);
            String cmd = tokens[0];
            if("login".equalsIgnoreCase(cmd)){
                handleLogIn(tokens);
            }
        }
    }

    // command format : login <username> <password>
    private void handleLogIn(String[] tokens) throws IOException {
        if(tokens.length == 3){
            String username = tokens[1];
            String password = tokens[2];
            boolean isValidLogin = controller.isValidUser(username, password);
            if(isValidLogin){
                outputStream.write("login successful".getBytes());
            }else{
                outputStream.write("error login".getBytes());
            }
        }
    }
}
