package com.gui;

import com.chatApp.Client;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainFrame extends JFrame {
    Client client = null;
    StatusPanel statusPanel;

    public MainFrame(Client client){
        super("Chat Application");
        this.client = client;

        // set attributes
        setVisible(true);
        setSize(800, 400);
        setMinimumSize(new Dimension(800,400));

        setLayout(new BorderLayout());

        // components
        statusPanel = new StatusPanel(client);

        //Add components to mainFrame
        add(statusPanel, BorderLayout.WEST);
    }

    public static void main(String[] args) throws UnknownHostException {
        MainFrame mainFrame = new MainFrame(new Client(InetAddress.getLocalHost(), 8800));
    }
}
