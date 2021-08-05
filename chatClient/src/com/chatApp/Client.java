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
            handleServerResponses();
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

}
