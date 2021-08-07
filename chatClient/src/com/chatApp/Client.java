package com.chatApp;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Client {
    private InetAddress serverIp;
    private int serverPort;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private BufferedReader reader;
    private ArrayList<UserStatusListener> statusListeners = new ArrayList<>();
    private HistoryListener historyListener;
    private MessageListener messageListener;
    private UserValidityListener userValidityListener;
    private ChatroomMessageListener chatroomMessageListener;
    private String user;
    private boolean isMemberOfChatroom = false;

    // a client has been created
    public Client(InetAddress serverIp, int serverPort){
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    // main method for testing purpose
    public static void main(String[] args) throws IOException {
        Client client = new Client(InetAddress.getLocalHost(), 8800);
        client.connect();

        String response = client.register("dat", "dat");

        if(response.equalsIgnoreCase("registration successful")){
            if(client.login("tom", "tom")){
                System.out.println("login successful");
            }else{
                System.out.println("login unsuccessful");
            }
        }
        else{
            System.out.println(response);
        }



    }

    // a method for connecting client to the server
    public boolean connect(){
        try {
            this.socket = new Socket(serverIp, serverPort);
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
            this.reader = new BufferedReader(new InputStreamReader(inputStream));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // method for logging in the client
    public boolean login(String username, String password) throws IOException {
        String cmd  = "login " + username + " " + password + "\n";

        // send login command to server
        outputStream.write(cmd.getBytes());

        // read response from server
        String response = reader.readLine();
        if(response.equalsIgnoreCase("login successful")){
            user = username;

            //listen from server continuously
            handleServerResponses();

            // check if this particular user is a member of chatroom
            String str = "isvalid chatroom " + user + "\n";
            outputStream.write(str.getBytes());
            
            return true;
        }
        else{
            return false;
        }
    }

    private void handleServerResponses() {
        //clientWoker
        Thread thread = new Thread(){
            public void run(){
                try {
                    handleServerResponsesHelper();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

    }

    //client Worker
    private void handleServerResponsesHelper() throws IOException {
        String input;
        while((input = reader.readLine()) != null){
            String tokens[] = input.split(" ");
            String cmd = tokens[0];
            if(cmd.equalsIgnoreCase("online")){
                handleOnline(tokens);
            }
            else if(cmd.equalsIgnoreCase("offline")){
                handleOffline(tokens);
            }
            else if(cmd.charAt(cmd.length() - 1) == '#'){
                handleHistory(input);
            }
            else if(cmd.equalsIgnoreCase("yes")){
                isMemberOfChatroom = true;
            }
            else if(cmd.equalsIgnoreCase("valid")){
                handleValid(tokens);
            }
            else if(cmd.equalsIgnoreCase("invalid")){
                handleInValid(tokens);
            }
            else if(cmd.equalsIgnoreCase("msg")){
                if(tokens.length > 1){
                    if(tokens[1].charAt(0) == '#'){
                        //handle chatRoom message
                        handleChatroomMessage(input);
                    }
                    else{
                        //handle direct message
                        handleMessage(input);
                    }
                }
            }
        }
    }

    private void handleChatroomMessage(String input) {
        String tokens[] = input.split(" ", 4);
        if(tokens.length == 4){
            String sender = tokens[2];
            String msgBody = tokens[3];

            if(chatroomMessageListener != null){
                chatroomMessageListener.onChatroomMessage(sender, msgBody);
            }
        }
    }

    private void handleValid(String[] tokens) {
        String username = tokens[1];
        if(userValidityListener != null){
            userValidityListener.validUser(username);
        }
    }

    private void handleInValid(String[] tokens) {
        String username = tokens[1];
        if(userValidityListener != null){
            userValidityListener.invalidUser(username);
        }
    }

    private void handleMessage(String input) {
        String tokens[] = input.split(" ", 4);
        if(tokens.length == 4){
            String sender = tokens[2];
            String msgBody = tokens[3];
            if(messageListener != null){
                messageListener.onMessage(sender, msgBody);
            }
        }
    }

    private void handleHistory(String history) {
        if(historyListener != null){
            historyListener.onHistory(history + "\n");
        }
    }

    private void handleOffline(String[] tokens) {
        if(tokens.length == 2){
            String user = tokens[1];
            for(UserStatusListener listener : statusListeners){
                listener.offline(user);
            }
        }
    }

    private void handleOnline(String[] tokens) {
        if(tokens.length == 2){
            String user = tokens[1];
            for(UserStatusListener listener : statusListeners){
                listener.online(user);
            }
        }
    }

    // registration
    public String register(String username, String password) throws IOException {
        String cmd = "register " + username + " " +password + "\n";

        outputStream.write(cmd.getBytes());
        String response = reader.readLine();

        return response;
    }

    public void addStatusListener(UserStatusListener listener){
        statusListeners.add(listener);
    }

    public void removeStatusListener(UserStatusListener listener){
        statusListeners.remove(listener);
    }

    public void handleExit() throws IOException {
        outputStream.write("exit".getBytes());
    }

    public void getHistory(String cmd) throws IOException {
        outputStream.write(cmd.getBytes());
    }

    public void setHistoryListener(HistoryListener historyListener){
        this.historyListener = historyListener;
    }

    public void message(String cmd) {
        try {
            outputStream.write(cmd.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }
    
    public String getUser(){
        return user;
    }

    public void isValidUser(String username){
        String cmd = "isvalid " + username + "\n";
        try {
            outputStream.write(cmd.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUserValidityListener(UserValidityListener userValidityListener) {
        this.userValidityListener = userValidityListener;
    }

    public void joinChatroom(String cmd) {
        try {
            outputStream.write(cmd.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void leaveChatroom(String cmd) {
        try {
            outputStream.write(cmd.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isMemberOfChatroom() {
        return isMemberOfChatroom;
    }

    public void setMemberOfChatroom(boolean memberOfChatroom) {
        isMemberOfChatroom = memberOfChatroom;
    }

    public void setChatroomMessageListener(ChatroomMessageListener chatroomMessageListener) {
        this.chatroomMessageListener = chatroomMessageListener;
    }
}
