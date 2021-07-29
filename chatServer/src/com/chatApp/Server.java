package com.chatApp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread{
    private ServerSocket serverSocket = null;
    private int port;
    public Server(int port) {
        this.port = port;
    }

    public void run(){
        try {
            serverSocket = new ServerSocket(port);
            while(true){
                System.out.println("waiting for to accept the connections\n");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted the connection from clientSocket\n");
                // processing each client request in a separate Thread.
                ServerWorker worker = new ServerWorker(this, clientSocket);
                worker.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
