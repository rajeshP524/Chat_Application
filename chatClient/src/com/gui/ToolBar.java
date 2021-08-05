package com.gui;

import com.chatApp.Client;

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

    public ToolBar(Client client){
        this.client = client;

        directMessage = new JButton("DirectMessage");
        chatRoom = new JButton("ChatRoom");
        exit = new JButton("Exit");

        setLayout(new FlowLayout());
        add(directMessage, FlowLayout.LEFT);
        add(chatRoom, FlowLayout.LEFT);
        add(exit, FlowLayout.RIGHT);

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
    }
}
