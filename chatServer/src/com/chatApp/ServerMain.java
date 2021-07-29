package com.chatApp;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args){
        int port = 8800;
        Server server = new Server(port);
        server.start();
    }
}
