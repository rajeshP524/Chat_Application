package com.gui;

import com.chatApp.Client;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainFrame extends JFrame {
    private Client client = null;
    private StatusPanel statusPanel;
    private ToolBar toolBar;
    private Controller controller;

    public MainFrame(Client client, Controller controller){
        super("Chat Application");
        this.client = client;
        this.controller = controller;

        // set attributes
        setVisible(true);
        setSize(800, 400);
        setMinimumSize(new Dimension(800,400));

        setLayout(new BorderLayout());

        // components
        statusPanel = controller.getStatusPanel();
        toolBar = controller.getToolBar();

        //Add components to mainFrame
        add(statusPanel, BorderLayout.WEST);
        add(toolBar, BorderLayout.NORTH);
    }

    public static void main(String[] args) throws UnknownHostException {

    }
}
