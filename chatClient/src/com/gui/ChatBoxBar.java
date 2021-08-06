package com.gui;

import com.chatApp.Client;

import javax.swing.*;
import java.awt.*;

public class ChatBoxBar extends JPanel {

    private Client client;
    private JTextField chatField;
    private JButton send;

    public ChatBoxBar(Client client){
        this.client = client;

        chatField = new JTextField(30);
        send = new JButton("send");

        chatField.getPreferredSize().width = 20;
        setLayout(new FlowLayout(FlowLayout.TRAILING));

        add(new JScrollPane(chatField));
        add(send);
    }
}
