package com.chatApp;

import com.controller.Controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread{
    private ServerSocket serverSocket = null;
    private int port;
    private Controller controller;
    private List<ServerWorker> workerList = new ArrayList<>();

    public Server(int port, Controller controller) {
        this.controller = controller;
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
                ServerWorker worker = new ServerWorker(this, clientSocket, controller);
                worker.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ServerWorker> getWorkerList(){
        return workerList;
    }

    // maintaining a list of all active users
    public void addWorker(ServerWorker worker){
        workerList.add(worker);
    }
}
