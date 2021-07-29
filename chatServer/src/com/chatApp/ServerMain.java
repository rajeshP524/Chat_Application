package com.chatApp;

import com.controller.Controller;

import java.io.IOException;
import java.sql.SQLException;

public class ServerMain {
    public static void main(String[] args) throws SQLException {
        int port = 8800;
        Controller controller = new Controller();
        Server server = new Server(port, controller);
        server.start();

    }
}
