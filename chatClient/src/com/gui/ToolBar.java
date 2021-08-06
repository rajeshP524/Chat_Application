package com.gui;

import com.chatApp.Client;
import com.chatApp.UserValidityListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;

public class ToolBar extends JPanel {
    private Client client;
    private JButton directMessage;
    private JButton chatRoom;
    private JButton exit;

    private DMessageListener dMessageListener;

    public ToolBar(Client client){
        this.client = client;

        directMessage = new JButton("DirectMessage");
        chatRoom = new JButton("ChatRoom");
        exit = new JButton("Exit");

        setLayout(new FlowLayout());
        add(directMessage, FlowLayout.LEFT);
        add(chatRoom, FlowLayout.LEFT);
        add(exit, FlowLayout.RIGHT);

        // add listener for exit button
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int option = JOptionPane.showConfirmDialog(ToolBar.this, "Are you sure to exit? ", "exit", JOptionPane.OK_CANCEL_OPTION);
                if(option == JOptionPane.OK_OPTION){
                    try {
                        client.handleExit();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.exit(0);
                }

            }
        });

        // add Listener for directMessage
        directMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String username = JOptionPane.showInputDialog(ToolBar.this, "Enter username:" );
                if(username.length() == 0){
                    JOptionPane.showMessageDialog(ToolBar.this, "No such user exits", "message", JOptionPane.ERROR_MESSAGE);
                }
                else{
                    client.isValidUser(username);
                }
            }
        });

        // add Listener for chatRoom
        chatRoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String username = JOptionPane.showInputDialog(ToolBar.this, "Enter a command: " );
            }
        });

        // add toolBar as a listener for user validness
        client.setUserValidityListener(new UserValidityListener() {
            @Override
            public void validUser(String username) {
                if(dMessageListener != null){
                    dMessageListener.onValidUser(username);
                }
            }

            @Override
            public void invalidUser(String username) {
                JOptionPane.showMessageDialog(ToolBar.this, "No such user exits", "message", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public void setdMessageListener(DMessageListener dMessageListener) {
        this.dMessageListener = dMessageListener;
    }
}
