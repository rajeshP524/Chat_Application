package com.gui;

import com.chatApp.Client;

import javax.swing.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ChatApp {

    public static void main(String[] args) throws UnknownHostException {
        Client client = new Client(InetAddress.getLocalHost(), 8800);
        client.connect();
        Controller controller = new Controller(client);
        SwingUtilities.invokeLater(new Runnable(){

            public void run() {
                new Authorization(client, controller);
            }
        });


    }
}
